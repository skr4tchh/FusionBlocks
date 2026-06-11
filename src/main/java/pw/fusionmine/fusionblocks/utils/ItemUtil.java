package pw.fusionmine.fusionblocks.utils;

import lombok.experimental.UtilityClass;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@UtilityClass
public class ItemUtil {

    public ItemStack create(ItemStack itemStack, String title) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(title);
        }
        itemStack.setItemMeta(meta);
        return itemStack;
    }

}