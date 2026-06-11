package pw.fusionmine.fusionblocks.blocks.tnt;

import org.bukkit.Location;
import pw.fusionmine.fusionblocks.FusionBlocks;
import pw.fusionmine.fusionblocks.blocks.tnt.storage.TNTSettings;
import pw.fusionmine.fusionblocks.blocks.tnt.storage.TNTStorage;

import java.util.HashMap;
import java.util.List;

public class TNTManager {

    public HashMap<String, TNT> TNTHashMap = new HashMap<>();

    public HashMap<Location, TNT> placedTNT = new HashMap<>();

    private TNTSettings settings;

    private TNTStorage storage;

    public TNTManager(FusionBlocks plugin) {
        this.settings = new TNTSettings(plugin, this);
        this.storage = new TNTStorage(plugin, this);
    }

    public void unload() {
        placedTNT.forEach((k, v) -> {
            if (v != null) {
                this.storage.save(k, v);
            }
        });
    }

    public boolean isTNT(Location location) {
        for (Location loc : placedTNT.keySet()) {
            if (loc.equals(location)) {
                return true;
            }
        }
        return false;
    }

    public void placeTnt(Location location, TNT TNT) {
        placedTNT.put(location, TNT);
    }

    public void removeTnt(Location location) {
        this.storage.remove(location);
        placedTNT.remove(location);
    }

    public TNT getPlacedTNT(Location location) {
        if (isTNT(location)) {
            return placedTNT.get(location);
        }
        return null;
    }


    public void register(String key, TNT TNT) {
        TNTHashMap.put(key, TNT);
    }

    public TNT getTNT(String key) {
        return TNTHashMap.get(key);
    }

    public List<TNT> getAll() {
        return TNTHashMap.values().stream().toList();
    }

}
