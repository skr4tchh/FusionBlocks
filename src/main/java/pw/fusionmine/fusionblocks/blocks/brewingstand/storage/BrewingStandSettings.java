package pw.fusionmine.fusionblocks.blocks.brewingstand.storage;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import pw.fusionmine.fusionblocks.FusionBlocks;
import pw.fusionmine.fusionblocks.blocks.brewingstand.BrewingStandManager;
import pw.fusionmine.fusionblocks.blocks.brewingstand.CustomBrewingStand;
import pw.fusionmine.fusionblocks.utils.MinecraftColor;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BrewingStandSettings {

    private File file;

    private FileConfiguration configuration;

    private BrewingStandManager brewingStandManager;

    private FusionBlocks plugin;

    public BrewingStandSettings(FusionBlocks plugin, BrewingStandManager brewingStandManager) {
        this.brewingStandManager = brewingStandManager;
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "settings/brewing-stand.yml");
        if (!this.file.exists()) {
            plugin.saveResource("settings/brewing-stand.yml", false);
        }
        this.configuration = YamlConfiguration.loadConfiguration(this.file);
        load();
    }

    public void load() {
        ConfigurationSection brewingstandsection = this.configuration.getConfigurationSection("brewing-stands");
        if (brewingstandsection == null)
            return;
        for (String key : brewingstandsection.getKeys(false)) {
            ConfigurationSection brewingstand = brewingstandsection.getConfigurationSection(key);
            if (brewingstand != null) {
                String name = MinecraftColor.format(brewingstand.getString("name"));
                List<String> description = null;
                if (brewingstand.getList("description") != null) {
                    description = brewingstand.getStringList("description").stream().map(MinecraftColor::format).toList();
                }

                ConfigurationSection attributesSection = brewingstand.getConfigurationSection("attributes");
                Map<String, Double> attributes = new HashMap<>();
                if(attributesSection != null) {
                    for (String attributeName : attributesSection.getKeys(false)) {
                        if(attributesSection.isDouble(attributeName)) {
                            double value = attributesSection.getDouble(attributeName);
                            attributes.put(attributeName, value);
                        }
                    }
                }

                CustomBrewingStand customBrewinStand = new CustomBrewingStand(key, name, attributes);

                if (description != null) {
                    customBrewinStand.setDescription(description);
                }

                brewingStandManager.register(key, customBrewinStand);
            }
        }

        plugin.getLogger().info("Загружено " + brewingStandManager.BrewingStandHashMap.size() + " кастомных зельеварок.");
    }

}
