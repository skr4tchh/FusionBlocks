package pw.fusionmine.fusionblocks.blocks.tnt.storage;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import pw.fusionmine.fusionblocks.FusionBlocks;
import pw.fusionmine.fusionblocks.blocks.tnt.TNT;
import pw.fusionmine.fusionblocks.blocks.tnt.TNTManager;
import pw.fusionmine.fusionblocks.utils.MinecraftColor;

import java.io.File;
import java.util.List;

public class TNTSettings {

    private File file;

    private FileConfiguration configuration;

    private TNTManager tntManager;

    private FusionBlocks plugin;

    public TNTSettings(FusionBlocks plugin, TNTManager tntManager) {
        this.tntManager = tntManager;
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "settings/tnt.yml");
        if (!this.file.exists()) {
            plugin.saveResource("settings/tnt.yml", false);
        }
        this.configuration = YamlConfiguration.loadConfiguration(this.file);
        load();
    }

    public void load() {
        ConfigurationSection tntsection = this.configuration.getConfigurationSection("tnts");
        if(tntsection == null)
            return;
        for (String key : tntsection.getKeys(false)) {
            ConfigurationSection tnt = tntsection.getConfigurationSection(key);
            if (tnt != null) {
                String name = MinecraftColor.format(tnt.getString("name"));
                int power = tnt.getInt("power");
                int stonedamage = tnt.getInt("stonedamage");
                boolean setfire = tnt.getBoolean("setfire");
                boolean breakblocks = tnt.getBoolean("breakblocks");
                List<String> description = null;
                if (tnt.getList("description") != null) {
                    description = tnt.getStringList("description").stream().map(MinecraftColor::format).toList();
                }

                TNT customTnt = new TNT(key, name, power, stonedamage, setfire, breakblocks);

                ConfigurationSection replace = tnt.getConfigurationSection("replaceblocks");
                if (replace != null && replace.getBoolean("replace")) {
                    if (replace.get("replaced-blocks") != null) {
                        List<Material> materials = replace.getList("replaced-blocks").stream().map(f -> Material.valueOf((String) f)).toList();
                        if (!materials.isEmpty()) {
                            customTnt.setReplacedBLocks(materials);
                        }
                    }
                    if (replace.get("setblock") != null) {
                        Material setblock = Material.valueOf(replace.getString("setblock"));
                        customTnt.setSetblock(setblock);
                    }
                }

                if (description != null) {
                    customTnt.setDescription(description);
                }
                tntManager.register(key, customTnt);
            }
        }

        plugin.getLogger().info("Загружено " + tntManager.TNTHashMap.size() + " кастомных динамитов.");
    }

}
