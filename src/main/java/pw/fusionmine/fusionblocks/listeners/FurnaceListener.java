package pw.fusionmine.fusionblocks.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Furnace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import pw.fusionmine.fusionblocks.FusionBlocks;
import pw.fusionmine.fusionblocks.blocks.brewingstand.CustomBrewingStand;
import pw.fusionmine.fusionblocks.blocks.furnace.CustomFurnace;
import pw.fusionmine.fusionblocks.blocks.furnace.FurnaceManager;
import pw.fusionmine.fusionblocks.utils.LocationUtil;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FurnaceListener implements Listener {

    private FurnaceManager furnaceManager;

    private FusionBlocks plugin;

    private final Map<String, SpeedEntry> activeFurnaces = new ConcurrentHashMap<>();

    private boolean taskRunning = false;

    public FurnaceListener(FusionBlocks plugin, FurnaceManager furnaceManager) {
        this.plugin = plugin;
        this.furnaceManager = furnaceManager;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onFurnaceBurn(FurnaceBurnEvent event) {
        Block block = event.getBlock();
        Location location = block.getLocation();
        CustomFurnace furnace = furnaceManager.getPlacedFurnace(location);
        if(furnace == null)
            return;
        if(furnace.hasAttribute("speed")) {
            double speed = furnace.getAttribute("speed");
            if (speed > 1.0D) {
                String key = LocationUtil.toKey(location);
                int extra = (int) (speed - 1.0D);
                if (extra < 1)
                    extra = 1;
                this.activeFurnaces.put(key, new SpeedEntry(location, extra));
                startGlobalTask();
            }
        }
        if(furnace.hasAttribute("fuel_efficiency")) {
            double fuelMul = furnace.getAttribute("fuel_efficiency");
            event.setBurnTime(Math.max(1, (int)(event.getBurnTime() / fuelMul)));
        }
    }

    private void startGlobalTask() {
        if (this.taskRunning)
            return;
        this.taskRunning = true;
        (new BukkitRunnable() {
            public void run() {
                if (activeFurnaces.isEmpty()) {
                    FurnaceListener.this.taskRunning = false;
                    cancel();
                    return;
                }
                Iterator<Map.Entry<String, SpeedEntry>> it = FurnaceListener.this.activeFurnaces.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<String, FurnaceListener.SpeedEntry> entry = it.next();
                    SpeedEntry se = entry.getValue();
                    if (!LocationUtil.isChunkLoaded(se.location))
                        continue;
                    Block block = se.location.getBlock();
                    Material mat = block.getType();
                    if (mat != Material.FURNACE) {
                        it.remove();
                        continue;
                    }
                    Furnace furnace = (Furnace) block.getState();
                    if (furnace.getBurnTime() <= 0) {
                        se.idle++;
                        if (se.idle > 3)
                            it.remove();
                        continue;
                    }
                    se.idle = 0;
                    int cook = furnace.getCookTime();
                    if (cook <= 0)
                        continue;
                    int total = furnace.getCookTimeTotal();
                    int newCook = Math.min(cook + se.extra, total);
                    furnace.setCookTime((short)newCook);
                    furnace.update(true, false);
                }
            }
        }).runTaskTimer(this.plugin, 1L, 1L);
    }

    @EventHandler
    public void breakFurnace(BlockBreakEvent e) {
        Block block = e.getBlock();
        if(block.getType() != Material.FURNACE)
            return;
        Location location = block.getLocation();
        CustomFurnace customFurnace = furnaceManager.getPlacedFurnace(location);
        if (customFurnace != null) {
            furnaceManager.dropFurnace(location, block, customFurnace);
            furnaceManager.removeFurnace(location);
            e.setDropItems(false);
        }
    }

    @EventHandler
    public void placeFurnace(BlockPlaceEvent e) {
        ItemStack itemStack = e.getItemInHand();
        if(itemStack.getType() != Material.FURNACE)
            return;
        if (itemStack.getItemMeta() == null)
            return;
        ItemMeta meta = itemStack.getItemMeta();
        for (CustomFurnace furnace : furnaceManager.getAll()) {
            if (meta.getPersistentDataContainer().has(furnace.getKey())) {
                furnaceManager.placeFurnace(e.getBlockPlaced().getLocation(), furnace);
                break;
            }
        }
    }

    private static class SpeedEntry {
        final Location location;

        final int extra;

        int idle;

        SpeedEntry(Location location, int extra) {
            this.location = location;
            this.extra = extra;
            this.idle = 0;
        }

    }

}
