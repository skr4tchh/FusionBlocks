package pw.fusionmine.fusionblocks.utils;

import lombok.experimental.UtilityClass;
import org.bukkit.Location;

@UtilityClass
public class LocationUtil {

    public String toKey(Location loc) {
        return loc.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
    }

    public boolean isChunkLoaded(Location loc) {
        return loc.getWorld().isChunkLoaded(loc.getBlockX() >> 4, loc.getBlockZ() >> 4);
    }

}
