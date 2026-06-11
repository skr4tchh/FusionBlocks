package pw.fusionmine.fusionblocks.blocks.tnt.storage;

import pw.fusionmine.fusionblocks.FusionBlocks;
import pw.fusionmine.fusionblocks.blocks.tnt.TNT;
import java.io.File;
import java.io.IOException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import pw.fusionmine.fusionblocks.blocks.tnt.TNTManager;

public class TNTStorage {

    private TNTManager tntManager;

    private File file;

    private FileConfiguration config;

    public TNTStorage(FusionBlocks plugin, TNTManager tntManager) {
        this.tntManager = tntManager;
        this.file = new File(plugin.getDataFolder(), "storage/tnt.yml");
        if (!this.file.exists()) {
            plugin.saveResource("storage/tnt.yml", false);
        }
        this.config = YamlConfiguration.loadConfiguration(this.file);
        load();
    }

    public void load() {
        ConfigurationSection section = this.config.getConfigurationSection("tnts");
        if (section != null) {
            for (String key : section.getKeys(false)) {
                ConfigurationSection tntsection = section.getConfigurationSection(key);
                if (tntsection == null)
                    continue;
                String[] split = key.split(",");
                String world = split[0];
                int x = Integer.parseInt(split[1]);
                int y = Integer.parseInt(split[2]);
                int z = Integer.parseInt(split[3]);
                Location location = new Location(Bukkit.getWorld(world), x, y, z);
                TNT customTnt = tntManager.getTNT(tntsection.getString("type"));
                if (location.getBlock().getType() != Material.TNT) {
                    location.getBlock().setType(Material.TNT);
                }
                tntManager.placeTnt(location, customTnt);
            }
        }
    }

    public void save(Location location, TNT customTnt) {
        String key = "tnts." + location.getWorld().getName() + "," + location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ();
        this.config.set(key + ".location.x", Integer.valueOf(location.getBlockX()));
        this.config.set(key + ".location.y", Integer.valueOf(location.getBlockY()));
        this.config.set(key + ".location.z", Integer.valueOf(location.getBlockZ()));
        this.config.set(key + ".location.world", location.getWorld().getName());
        this.config.set(key + ".type", customTnt.getKey().getKey());
        try {
            this.config.save(this.file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void remove(Location location) {
        String key = "tnts." + location.getWorld().getName() + "," + location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ();
        this.config.set(key, null);
        try {
            this.config.save(this.file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
