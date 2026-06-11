package pw.fusionmine.fusionblocks.blocks.furnace.storage;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import pw.fusionmine.fusionblocks.FusionBlocks;
import pw.fusionmine.fusionblocks.blocks.furnace.CustomFurnace;
import pw.fusionmine.fusionblocks.blocks.furnace.FurnaceManager;
import pw.fusionmine.fusionblocks.utils.MinecraftColor;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FurnaceSettings {

    private File file;

    private FileConfiguration configuration;

    private FurnaceManager furnaceManager;

    private FusionBlocks plugin;

    public FurnaceSettings(FusionBlocks plugin, FurnaceManager furnaceManager) {
        this.furnaceManager = furnaceManager;
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "settings/furnace.yml");
        if (!this.file.exists()) {
            plugin.saveResource("settings/furnace.yml", false);
        }
        this.configuration = YamlConfiguration.loadConfiguration(this.file);
        load();
    }

    public void load() {
        ConfigurationSection furnacesection = this.configuration.getConfigurationSection("furnaces");
        if (furnacesection == null)
            return;
        for (String key : furnacesection.getKeys(false)) {
            ConfigurationSection furnace = furnacesection.getConfigurationSection(key);
            if (furnace != null) {
                String name = MinecraftColor.format(furnace.getString("name"));
                List<String> description = null;
                if (furnace.getList("description") != null) {
                    description = furnace.getStringList("description").stream().map(MinecraftColor::format).toList();
                }

                ConfigurationSection attributesSection = furnace.getConfigurationSection("attributes");
                Map<String, Double> attributes = new HashMap<>();
                if(attributesSection != null) {
                    for (String attributeName : attributesSection.getKeys(false)) {
                        if(attributesSection.isDouble(attributeName)) {
                            double value = attributesSection.getDouble(attributeName);
                            attributes.put(attributeName, value);
                        }
                    }
                }

                CustomFurnace customFurnace = new CustomFurnace(key, name, attributes);

                if (description != null) {
                    customFurnace.setDescription(description);
                }

                furnaceManager.register(key, customFurnace);
            }
        }

        plugin.getLogger().info("Загружено " + furnaceManager.FurnaceHashMap.size() + " кастомных печек.");
    }

}

