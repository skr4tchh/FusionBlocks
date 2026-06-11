package pw.fusionmine.fusionblocks.blocks.chunker;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import pw.fusionmine.fusionblocks.FusionBlocks;
import pw.fusionmine.fusionblocks.utils.ItemUtil;

import java.util.List;

@Getter
@Setter
public class Chunker {

    private NamespacedKey key;

    private String name;

    private List<String> description;

    private int radius;

    public Chunker(String key, String name, int radius) {
        this.key = new NamespacedKey(FusionBlocks.getInstance(), key);
        this.name = name;
        this.radius = radius;
    }

    public ItemStack getItemStack() {
        ItemStack itemStack = ItemUtil.create(new ItemStack(Material.STRUCTURE_BLOCK), this.name);
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            meta.getPersistentDataContainer().set(this.key, PersistentDataType.STRING, this.key.getKey());
            if (this.description != null) {
                meta.setLore(this.description);
            }
            meta.addEnchant(Enchantment.UNBREAKING, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            itemStack.setItemMeta(meta);
        }
        return itemStack;
    }

}
