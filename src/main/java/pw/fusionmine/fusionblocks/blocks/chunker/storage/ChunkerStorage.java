package pw.fusionmine.fusionblocks.blocks.chunker.storage;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import pw.fusionmine.fusionblocks.FusionBlocks;
import pw.fusionmine.fusionblocks.blocks.chunker.Chunker;
import pw.fusionmine.fusionblocks.blocks.chunker.ChunkerManager;

import java.io.File;
import java.io.IOException;

public class ChunkerStorage {

    private ChunkerManager chunkerManager;

    private File file;

    private FileConfiguration config;

    public ChunkerStorage(FusionBlocks plugin, ChunkerManager chunkerManager) {
        this.chunkerManager = chunkerManager;
        this.file = new File(plugin.getDataFolder(), "storage/chunker.yml");
        if (!this.file.exists()) {
            plugin.saveResource("storage/chunker.yml", false);
        }
        this.config = YamlConfiguration.loadConfiguration(this.file);
        load();
    }

    public void load() {
        ConfigurationSection section = this.config.getConfigurationSection("chunkers");
        if (section != null) {
            for (String key : section.getKeys(false)) {
                ConfigurationSection chunkersection = section.getConfigurationSection(key);
                if (chunkersection == null)
                    continue;
                String[] split = key.split(",");
                String world = split[0];
                int x = Integer.parseInt(split[1]);
                int y = Integer.parseInt(split[2]);
                int z = Integer.parseInt(split[3]);
                Location location = new Location(Bukkit.getWorld(world), x, y, z);
                Chunker chunker = chunkerManager.getChuncker(chunkersection.getString("type"));
                if (location.getBlock().getType() != Material.STRUCTURE_BLOCK) {
                    location.getBlock().setType(Material.STRUCTURE_BLOCK);
                }
                chunkerManager.placeChunker(location, chunker);
            }
        }
    }

    public void save(Location location, Chunker chunker) {
        String key = "chunkers." + location.getWorld().getName() + "," + location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ();
        this.config.set(key + ".location.x", Integer.valueOf(location.getBlockX()));
        this.config.set(key + ".location.y", Integer.valueOf(location.getBlockY()));
        this.config.set(key + ".location.z", Integer.valueOf(location.getBlockZ()));
        this.config.set(key + ".location.world", location.getWorld().getName());
        this.config.set(key + ".type", chunker.getKey().getKey());
        try {
            this.config.save(this.file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void remove(Location location) {
        String key = "chunkers." + location.getWorld().getName() + "," + location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ();
        this.config.set(key, null);
        try {
            this.config.save(this.file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
