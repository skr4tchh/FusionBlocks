package pw.fusionmine.fusionblocks.utils;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemUtil {

    public static ItemStack create(ItemStack itemStack, String title) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(title);

        }
        itemStack.setItemMeta(meta);
        return itemStack;

    }

}