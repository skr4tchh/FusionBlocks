package pw.fusionmine.fusionblocks.listeners;

import lombok.AllArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.BrewingStandFuelEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pw.fusionmine.fusionblocks.blocks.brewingstand.BrewingStandManager;
import pw.fusionmine.fusionblocks.blocks.brewingstand.CustomBrewingStand;

@AllArgsConstructor
public class BrewingStandListener implements Listener {

    private BrewingStandManager brewingStandManager;

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBrewingFuel(BrewingStandFuelEvent event) {
        Block block = event.getBlock();
        Location location = block.getLocation();
        CustomBrewingStand brewingStand = brewingStandManager.getPlacedBrewingStand(location);
        if(brewingStand == null)
            return;
        if(brewingStand.hasAttribute("fuel_efficiency")) {
            double fuelEff = brewingStand.getAttribute("fuel_efficiency");
            if (fuelEff != 1.0D)
                event.setFuelPower(Math.max(1, (int) (event.getFuelPower() / fuelEff)));
        }
    }

    @EventHandler
    public void breakBrewingStand(BlockBreakEvent e) {
        Block block = e.getBlock();
        if(block.getType() != Material.BREWING_STAND)
            return;
        Location location = block.getLocation();
        CustomBrewingStand customBrewingStand = brewingStandManager.getPlacedBrewingStand(location);
        if (customBrewingStand != null) {
            brewingStandManager.dropBrewingStand(location, block, customBrewingStand);
            brewingStandManager.removeBrewingStand(location);
            e.setDropItems(false);
        }
    }

    @EventHandler
    public void placeBrewingStand(BlockPlaceEvent e) {
        ItemStack itemStack = e.getItemInHand();
        if(itemStack.getType() != Material.BREWING_STAND)
            return;
        if (itemStack.getItemMeta() == null)
            return;
        ItemMeta meta = itemStack.getItemMeta();
        for (CustomBrewingStand brewingStand : brewingStandManager.getAll()) {
            if (meta.getPersistentDataContainer().has(brewingStand.getKey())) {
                brewingStandManager.placeBrewingStand(e.getBlockPlaced().getLocation(), brewingStand);
                break;
            }
        }
    }

}
