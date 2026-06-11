package pw.fusionmine.fusionblocks.blocks.brewingstand;

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
import java.util.Map;

@Getter
@Setter
public class CustomBrewingStand {

    private NamespacedKey key;

    private String name;

    private List<String> description;

    private Map<String, Double> attributes;

    public CustomBrewingStand(String key, String name, Map<String, Double> attributes) {
        this.key = new NamespacedKey(FusionBlocks.getInstance(), key);
        this.name = name;
        this.attributes = attributes;
    }

    public double getAttribute(String key) {
        return this.attributes.get(key);
    }

    public boolean hasAttribute(String key) {
        Double value = this.attributes.get(key);
        if (value == null)
            return false;
        return (value.doubleValue() > 0.0D);
    }

    public ItemStack getItemStack() {
        ItemStack itemStack = ItemUtil.create(new ItemStack(Material.BREWING_STAND), this.name);
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
