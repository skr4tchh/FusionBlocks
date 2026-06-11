package pw.fusionmine.fusionblocks.listeners;

import lombok.AllArgsConstructor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import pw.fusionmine.fusionblocks.blocks.tnt.TNT;
import pw.fusionmine.fusionblocks.blocks.tnt.TNTManager;

@AllArgsConstructor
public class TNTListener implements Listener {

    private TNTManager tntManager;

    @EventHandler
    public void onTntIgnite(EntitySpawnEvent e) {
        if (e.getEntity().getType() == EntityType.TNT) {
            TNT TNT = tntManager.getPlacedTNT(e.getEntity().getLocation().getBlock().getLocation());
            if (TNT != null) {
                TNTPrimed primed = (TNTPrimed) e.getEntity();
                primed.setYield(TNT.getPower());
                primed.setVisualFire(TNT.isIssetfire());
                primed.getPersistentDataContainer().set(TNT.getKey(), PersistentDataType.STRING, TNT.getKey().getKey());
                tntManager.removeTnt(e.getEntity().getLocation().getBlock().getLocation());
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onTntExplode(EntityExplodeEvent e) {
        if (e.getEntity().getType() != EntityType.TNT) {
            return;
        }
        for (TNT TNT : tntManager.getAll()) {
            if (e.getEntity().getPersistentDataContainer().has(TNT.getKey())) {
                if (!TNT.isBreakblocks()) {
                    e.blockList().clear();
                }
                if (TNT.getReplacedBLocks().isEmpty() || TNT.getSetblock() == null)
                    return;
                Location location = e.getEntity().getLocation();
                double radius = TNT.getRadius();
                double x;
                for (x = -Math.ceil(radius); x <= Math.ceil(radius); x++) {
                    double y;
                    for (y = -Math.ceil(radius); y <= Math.ceil(radius); y++) {
                        double z;
                        for (z = -Math.ceil(radius); z <= Math.ceil(radius); z++) {
                            Location loc = location.clone().add(x, y, z);
                            if (loc.distanceSquared(e.getEntity().getLocation()) <= radius * radius) {

                                Block block = loc.getBlock();
                                if (TNT.getReplacedBLocks().contains(block.getType())) {
                                    block.setType(TNT.getSetblock());
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    @EventHandler
    public void breakTNT(BlockBreakEvent e) {
        TNT TNT = tntManager.getPlacedTNT(e.getBlock().getLocation().getBlock().getLocation());
        if (TNT != null) {
            e.getBlock().getLocation().getWorld().dropItemNaturally(e.getBlock().getLocation(), TNT.getItemStack());
            tntManager.removeTnt(e.getBlock().getLocation());

            e.setDropItems(false);
        }
    }


    @EventHandler
    public void placeTNT(BlockPlaceEvent e) {
        ItemStack itemStack = e.getItemInHand();
        if (itemStack.getItemMeta() == null)
            return;
        ItemMeta meta = itemStack.getItemMeta();
        for (TNT tnt : tntManager.getAll()) {
            if (meta.getPersistentDataContainer().has(tnt.getKey())) {
                tntManager.placeTnt(e.getBlockPlaced().getLocation(), tnt);
                break;
            }
        }
    }

}
