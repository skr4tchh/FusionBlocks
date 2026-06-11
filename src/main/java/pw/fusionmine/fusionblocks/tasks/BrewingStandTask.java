package pw.fusionmine.fusionblocks.tasks;

import lombok.AllArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BrewingStand;
import org.bukkit.scheduler.BukkitRunnable;
import pw.fusionmine.fusionblocks.FusionBlocks;
import pw.fusionmine.fusionblocks.blocks.brewingstand.BrewingStandManager;
import pw.fusionmine.fusionblocks.blocks.brewingstand.CustomBrewingStand;
import pw.fusionmine.fusionblocks.utils.LocationUtil;

@AllArgsConstructor
public class BrewingStandTask extends BukkitRunnable {

    private FusionBlocks plugin;

    private BrewingStandManager brewingStandManager;

    public void run() {
        if (this.plugin.getServer().getOnlinePlayers().isEmpty())
            return;
        for (Location location : brewingStandManager.placedBrewingStand.keySet()) {
            if (!LocationUtil.isChunkLoaded(location))
                continue;

            Block block = location.getBlock();
            if (block.getType() != Material.BREWING_STAND) {
                continue;
            }

            CustomBrewingStand customBrewingStand = brewingStandManager.getPlacedBrewingStand(location);
            if (customBrewingStand == null || !customBrewingStand.hasAttribute("speed")) return;

            double speed = customBrewingStand.getAttribute("speed");
            if (speed <= 1.0D) return;

            BrewingStand brewingStand = (BrewingStand) block.getState();
            int brewTime = brewingStand.getBrewingTime();
            if (brewTime <= 0)
                continue;

            int extra = (int) ((speed - 1.0D) * 5.0D);
            int newTime = Math.max(1, brewTime - extra);
            brewingStand.setBrewingTime(newTime);
            brewingStand.update(true, false);
        }
    }

}
