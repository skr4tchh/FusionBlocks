package pw.fusionmine.fusionblocks.listeners;

import lombok.AllArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import pw.fusionmine.fusionblocks.blocks.brewingstand.BrewingStandManager;
import pw.fusionmine.fusionblocks.blocks.brewingstand.CustomBrewingStand;
import pw.fusionmine.fusionblocks.blocks.furnace.CustomFurnace;
import pw.fusionmine.fusionblocks.blocks.furnace.FurnaceManager;
import pw.fusionmine.fusionblocks.blocks.tnt.TNT;
import pw.fusionmine.fusionblocks.blocks.tnt.TNTManager;

import java.util.List;

@AllArgsConstructor
public class BlockPositionListener implements Listener {

    private BrewingStandManager brewingStandManager;

    private TNTManager tntManager;

    private FurnaceManager furnaceManager;

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent event) {
        event.blockList().removeIf(block -> {
            Location location = block.getLocation();
            Material type = block.getType();
            switch (type) {
                case BREWING_STAND -> {
                    CustomBrewingStand customBrewingStand = brewingStandManager.getPlacedBrewingStand(location);
                    if (customBrewingStand != null) {
                        brewingStandManager.dropBrewingStand(location, block, customBrewingStand);
                        brewingStandManager.removeBrewingStand(location);
                        block.setType(Material.AIR);
                        return true;
                    }
                }
                case FURNACE -> {
                    CustomFurnace customFurnace = furnaceManager.getPlacedFurnace(location);
                    if (customFurnace != null) {
                        furnaceManager.dropFurnace(location, block, customFurnace);
                        furnaceManager.removeFurnace(location);
                        block.setType(Material.AIR);
                        return true;
                    }
                }
            }
            return false;
        });
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPistonExtend(BlockPistonExtendEvent event) {
        handlePistonMove(event.getBlocks(), event.getDirection());
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPistonRetract(BlockPistonRetractEvent event) {
        handlePistonMove(event.getBlocks(), event.getDirection());
    }

    private void handlePistonMove(List<Block> blocks, BlockFace direction) {
        for (int i = blocks.size() - 1; i >= 0; i--) {
            Block block = blocks.get(i);
            Location oldLocation = block.getLocation();
            Location newLocation = oldLocation.clone().add(direction.getModX(), direction.getModY(), direction.getModZ());

            switch (block.getType()) {
                case BREWING_STAND -> {
                    CustomBrewingStand customBrewingStand = brewingStandManager.getPlacedBrewingStand(oldLocation);
                    if (customBrewingStand != null) {
                        brewingStandManager.removeBrewingStand(oldLocation);
                        brewingStandManager.placeBrewingStand(newLocation, customBrewingStand);
                    }
                }
                case FURNACE -> {
                    CustomFurnace customFurnace = furnaceManager.getPlacedFurnace(oldLocation);
                    if (customFurnace != null) {
                        furnaceManager.removeFurnace(oldLocation);
                        furnaceManager.placeFurnace(newLocation, customFurnace);
                    }
                }
                case TNT -> {
                    TNT tnt = tntManager.getPlacedTNT(oldLocation);
                    if (tnt != null) {
                        tntManager.removeTNT(oldLocation);
                        tntManager.placeTNT(newLocation, tnt);
                    }
                }
            }
        }
    }

}
