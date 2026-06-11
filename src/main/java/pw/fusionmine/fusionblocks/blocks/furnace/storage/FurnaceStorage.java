package pw.fusionmine.fusionblocks.blocks.furnace.storage;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import pw.fusionmine.fusionblocks.FusionBlocks;
import pw.fusionmine.fusionblocks.blocks.furnace.CustomFurnace;
import pw.fusionmine.fusionblocks.blocks.furnace.FurnaceManager;

import java.io.File;
import java.io.IOException;

public class FurnaceStorage {

    private FurnaceManager furnaceManager;

    private File file;

    private FileConfiguration config;

    public FurnaceStorage(FusionBlocks plugin, FurnaceManager furnaceManager) {
        this.furnaceManager = furnaceManager;
        this.file = new File(plugin.getDataFolder(), "storage/furnace.yml");
        if (!this.file.exists()) {
            plugin.saveResource("storage/furnace.yml", false);
        }
        this.config = YamlConfiguration.loadConfiguration(this.file);
        load();
    }

    public void load() {
        ConfigurationSection section = this.config.getConfigurationSection("furnaces");
        if (section != null) {
            for (String key : section.getKeys(false)) {
                ConfigurationSection furnacesection = section.getConfigurationSection(key);
                if (furnacesection == null)
                    continue;
                String[] split = key.split(",");
                String world = split[0];
                int x = Integer.parseInt(split[1]);
                int y = Integer.parseInt(split[2]);
                int z = Integer.parseInt(split[3]);
                Location location = new Location(Bukkit.getWorld(world), x, y, z);
                CustomFurnace customFurnace = furnaceManager.getFurnace(furnacesection.getString("furnacetype"));
                if (location.getBlock().getType() != Material.FURNACE) {
                    location.getBlock().setType(Material.FURNACE);
                }
                furnaceManager.placeFurnace(location, customFurnace);
            }
        }
    }

    public void save(Location location, CustomFurnace customFurnace) {
        String key = "furnaces." + location.getWorld().getName() + "," + location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ();
        this.config.set(key + ".location.x", Integer.valueOf(location.getBlockX()));
        this.config.set(key + ".location.y", Integer.valueOf(location.getBlockY()));
        this.config.set(key + ".location.z", Integer.valueOf(location.getBlockZ()));
        this.config.set(key + ".location.world", location.getWorld().getName());
        this.config.set(key + ".furnacetype", customFurnace.getKey().getKey());
        try {
            this.config.save(this.file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void remove(Location location) {
        String key = "furnaces." + location.getWorld().getName() + "," + location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ();
        this.config.set(key, null);
        try {
            this.config.save(this.file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
