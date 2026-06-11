package pw.fusionmine.fusionblocks.blocks.brewingstand.storage;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import pw.fusionmine.fusionblocks.FusionBlocks;
import pw.fusionmine.fusionblocks.blocks.brewingstand.BrewingStandManager;
import pw.fusionmine.fusionblocks.blocks.brewingstand.CustomBrewingStand;

import java.io.File;
import java.io.IOException;

public class BrewingStandStorage {

    private BrewingStandManager brewingStandManager;

    private File file;

    private FileConfiguration config;

    private FusionBlocks plugin;

    public BrewingStandStorage(FusionBlocks plugin, BrewingStandManager brewingStandManager) {
        this.plugin = plugin;
        this.brewingStandManager = brewingStandManager;
        this.file = new File(plugin.getDataFolder(), "storage/brewing-stand.yml");
        if (!this.file.exists()) {
            plugin.saveResource("storage/brewing-stand.yml", false);
        }
        this.config = YamlConfiguration.loadConfiguration(this.file);
        load();
    }

    public void load() {
        ConfigurationSection section = this.config.getConfigurationSection("brewing-stands");
        if (section != null) {
            for (String key : section.getKeys(false)) {
                ConfigurationSection brewingstandsection = section.getConfigurationSection(key);
                if (brewingstandsection == null)
                    continue;
                String[] split = key.split(",");
                String world = split[0];
                int x = Integer.parseInt(split[1]);
                int y = Integer.parseInt(split[2]);
                int z = Integer.parseInt(split[3]);
                Location location = new Location(Bukkit.getWorld(world), x, y, z);
                CustomBrewingStand customBrewingStand = brewingStandManager.getBrewingStand(brewingstandsection.getString("type"));
                if (location.getBlock().getType() != Material.BREWING_STAND) {
                    location.getBlock().setType(Material.BREWING_STAND);
                }
                brewingStandManager.placeBrewingStand(location, customBrewingStand);
            }
        }
        this.plugin.getLogger().info("На сервере установлено " + brewingStandManager.placedBrewingStand.size() + " кастомных зельеварок.");
    }

    public void save(Location location, CustomBrewingStand customBrewingStand) {
        String key = "brewing-stands." + location.getWorld().getName() + "," + location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ();
        this.config.set(key + ".location.x", Integer.valueOf(location.getBlockX()));
        this.config.set(key + ".location.y", Integer.valueOf(location.getBlockY()));
        this.config.set(key + ".location.z", Integer.valueOf(location.getBlockZ()));
        this.config.set(key + ".location.world", location.getWorld().getName());
        this.config.set(key + ".type", customBrewingStand.getKey().getKey());
        try {
            this.config.save(this.file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void remove(Location location) {
        String key = "brewing-stands." + location.getWorld().getName() + "," + location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ();
        this.config.set(key, null);
        try {
            this.config.save(this.file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
