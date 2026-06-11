package pw.fusionmine.fusionblocks.blocks.tnt;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import pw.fusionmine.fusionblocks.FusionBlocks;
import pw.fusionmine.fusionblocks.utils.ItemUtil;

@Getter
@Setter
public class TNT {

    private NamespacedKey key;

    private String name;

    private int power;

    private int stonedamage;

    private boolean breakblocks;

    private boolean issetfire;

    private List<String> description;

    private int radius;

    private List<Material> replacedBLocks = new ArrayList<>();

    private Material setblock;

    public TNT(String key, String name, int power, int stonedamage, boolean issetfire, boolean breakblocks) {
        this.key = new NamespacedKey(FusionBlocks.getInstance(), key);
        this.name = name;
        this.power = power;
        this.stonedamage = stonedamage;
        this.breakblocks = breakblocks;
        this.issetfire = issetfire;
        this.radius = power / 2;
    }

    public ItemStack getItemStack() {
        ItemStack itemStack = ItemUtil.create(new ItemStack(Material.TNT), this.name);
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            meta.getPersistentDataContainer().set(this.key, PersistentDataType.STRING, this.key.getKey());
            if (this.description != null) {
                meta.setLore(this.description);
            }
            itemStack.setItemMeta(meta);
        }
        return itemStack;
    }

}
