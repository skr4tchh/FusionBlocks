package pw.fusionmine.fusionblocks.blocks.chunker.storage;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import pw.fusionmine.fusionblocks.FusionBlocks;
import pw.fusionmine.fusionblocks.blocks.chunker.Chunker;
import pw.fusionmine.fusionblocks.blocks.chunker.ChunkerManager;
import pw.fusionmine.fusionblocks.utils.MinecraftColor;

import java.io.File;
import java.util.List;

public class ChunkerSettings {

    private File file;

    private FileConfiguration configuration;

    private ChunkerManager chunkerManager;

    private FusionBlocks plugin;

    public ChunkerSettings(FusionBlocks plugin, ChunkerManager chunkerManager) {
        this.chunkerManager = chunkerManager;
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "settings/chunker.yml");
        if (!this.file.exists()) {
            plugin.saveResource("settings/chunker.yml", false);
        }
        this.configuration = YamlConfiguration.loadConfiguration(this.file);
        load();
    }

    public void load() {
        ConfigurationSection chunkersection = this.configuration.getConfigurationSection("chunkers");
        if (chunkersection == null)
            return;
        for (String key : chunkersection.getKeys(false)) {
            ConfigurationSection chunker = chunkersection.getConfigurationSection(key);
            if (chunker != null) {
                String name = MinecraftColor.format(chunker.getString("name"));
                List<String> description = null;
                if (chunker.getList("description") != null) {
                    description = chunker.getStringList("description").stream().map(MinecraftColor::format).toList();
                }

                int radius = chunker.getInt("radius");
                Chunker chunkerData = new Chunker(key, name, radius);

                if (description != null) {
                    chunkerData.setDescription(description);
                }

                chunkerManager.register(key, chunkerData);
            }
        }

        plugin.getLogger().info("Загружено " + chunkerManager.ChunkerHashMap.size() + " видов прогрузчиков чанков.");
    }

}
