package pw.fusionmine.fusionblocks.blocks.brewingstand;

import lombok.Getter;
import org.bukkit.Location;
import pw.fusionmine.fusionblocks.FusionBlocks;
import pw.fusionmine.fusionblocks.blocks.brewingstand.storage.BrewingStandSettings;
import pw.fusionmine.fusionblocks.blocks.brewingstand.storage.BrewingStandStorage;

import java.util.HashMap;
import java.util.List;

public class BrewingStandManager {

    public HashMap<String, CustomBrewingStand> BrewingStandHashMap = new HashMap<>();

    public HashMap<Location, CustomBrewingStand> placedBrewingStand = new HashMap<>();

    private BrewingStandSettings settings;

    private BrewingStandStorage storage;

    public BrewingStandManager(FusionBlocks plugin) {
        this.settings = new BrewingStandSettings(plugin, this);
        this.storage = new BrewingStandStorage(plugin, this);
    }

    public void unload() {
        placedBrewingStand.forEach((k, v) -> {
            if (v != null) {
                this.storage.save(k, v);
            }
        });
    }

    public boolean isBrewingStand(Location location) {
        for (Location loc : placedBrewingStand.keySet()) {
            if (loc.equals(location)) {
                return true;
            }
        }
        return false;
    }

    public void placeBrewingStand(Location location, CustomBrewingStand brewingStand) {
        placedBrewingStand.put(location, brewingStand);
    }

    public void removeBrewingStand(Location location) {
        this.storage.remove(location);
        placedBrewingStand.remove(location);
    }

    public CustomBrewingStand getPlacedBrewingStand(Location location) {
        if (isBrewingStand(location)) {
            return placedBrewingStand.get(location);
        }
        return null;
    }


    public void register(String key, CustomBrewingStand brewingStand) {
        BrewingStandHashMap.put(key, brewingStand);
    }

    public CustomBrewingStand getBrewingStand(String key) {
        return BrewingStandHashMap.get(key);
    }

    public List<CustomBrewingStand> getAll() {
        return BrewingStandHashMap.values().stream().toList();
    }

}
