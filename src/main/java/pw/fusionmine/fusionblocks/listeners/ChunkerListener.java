package pw.fusionmine.fusionblocks.listeners;

import com.sk89q.worldguard.protection.flags.Flags;
import lombok.AllArgsConstructor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pw.fusionmine.fusionblocks.blocks.chunker.Chunker;
import pw.fusionmine.fusionblocks.blocks.chunker.ChunkerManager;
import pw.fusionmine.fusionblocks.utils.WorldGuardUtil;

@AllArgsConstructor
public class ChunkerListener implements Listener {

    private ChunkerManager chunkerManager;

    @EventHandler(priority = EventPriority.MONITOR)
    public void placeChunker(PlayerInteractEvent e) {
        Block block = e.getClickedBlock();
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK || block == null || e.getItem() == null)
            return;
        if(e.getItem().getType() != Material.STRUCTURE_BLOCK)
            return;
        Player player = e.getPlayer();
        if(player.getGameMode() != GameMode.SURVIVAL)
            return;
        ItemStack item = e.getItem();
        if (!item.hasItemMeta())
            return;
        Block target = block.getRelative(e.getBlockFace());
        Location location = target.getLocation();
        if (!WorldGuardUtil.canModify(player, location, Flags.BLOCK_PLACE))
            return;
        ItemMeta meta = item.getItemMeta();
        for (Chunker chunker : chunkerManager.getAll()) {
            if (meta.getPersistentDataContainer().has(chunker.getKey())) {
                chunkerManager.placeChunker(location, chunker);
                target.setType(item.getType());
                item.setAmount(item.getAmount() - 1);
                e.setCancelled(true);
                break;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void breakChunker(PlayerInteractEvent e) {
        Block block = e.getClickedBlock();
        if (e.getAction() != Action.LEFT_CLICK_BLOCK || block == null)
            return;
        if(block.getType() != Material.STRUCTURE_BLOCK)
            return;
        Location location = block.getLocation();
        Chunker chunker = chunkerManager.getPlacedChuncker(location);
        if(chunker == null)
            return;
        if (!WorldGuardUtil.canModify(e.getPlayer(), location, Flags.BLOCK_BREAK))
            return;
        location.getWorld().playEffect(location, Effect.STEP_SOUND, block.getType());
        block.setType(Material.AIR);
        location.getWorld().dropItemNaturally(location, chunker.getItemStack());
        chunkerManager.removeChunker(location, chunker);
    }

}
