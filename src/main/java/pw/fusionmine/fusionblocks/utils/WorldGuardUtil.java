package pw.fusionmine.fusionblocks.utils;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.StateFlag;
import lombok.experimental.UtilityClass;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@UtilityClass
public class WorldGuardUtil {

    public boolean canModify(Player p, Location loc, StateFlag flag) {
        try {
            return WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery().testBuild(BukkitAdapter.adapt(loc), WorldGuardPlugin.inst().wrapPlayer(p), flag);
        } catch (Throwable throwable) {}
        return true;
    }

}
