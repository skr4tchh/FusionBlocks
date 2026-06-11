package pw.fusionmine.fusionblocks.listeners;

import lombok.AllArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pw.fusionmine.fusionblocks.blocks.chunker.Chunker;
import pw.fusionmine.fusionblocks.blocks.chunker.ChunkerManager;

@AllArgsConstructor
public class ChunkerListener implements Listener {

    private ChunkerManager chunkerManager;

    @EventHandler
    public void breakChunker(BlockBreakEvent e) {
        Block block = e.getBlock();
        if(block.getType() != Material.STRUCTURE_BLOCK)
            return;
        Location location = block.getLocation();
        Chunker chunker = chunkerManager.getPlacedChuncker(location);
        if (chunker != null) {
            location.getWorld().dropItemNaturally(location, chunker.getItemStack());
            chunkerManager.removeChunker(location, chunker);

            e.setDropItems(false);
        }
    }

    @EventHandler
    public void placeChunker(BlockPlaceEvent e) {
        ItemStack itemStack = e.getItemInHand();
        if(itemStack.getType() != Material.STRUCTURE_BLOCK)
            return;
        if (itemStack.getItemMeta() == null)
            return;
        ItemMeta meta = itemStack.getItemMeta();
        for (Chunker chunker : chunkerManager.getAll()) {
            if (meta.getPersistentDataContainer().has(chunker.getKey())) {
                chunkerManager.placeChunker(e.getBlockPlaced().getLocation(), chunker);
                break;
            }
        }
    }

}
