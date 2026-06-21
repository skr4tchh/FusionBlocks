package pw.fusionmine.fusionblocks.blocks.chunker;

import org.bukkit.Location;
import org.bukkit.World;
import pw.fusionmine.fusionblocks.FusionBlocks;
import pw.fusionmine.fusionblocks.blocks.chunker.storage.ChunkerSettings;
import pw.fusionmine.fusionblocks.blocks.chunker.storage.ChunkerStorage;

import java.util.HashMap;
import java.util.List;

public class ChunkerManager {

    public HashMap<String, Chunker> ChunkerHashMap = new HashMap<>();

    public HashMap<Location, Chunker> placedChunker = new HashMap<>();

    private ChunkerSettings settings;

    private ChunkerStorage storage;

    private FusionBlocks plugin;

    public ChunkerManager(FusionBlocks plugin) {
        this.plugin = plugin;
        this.settings = new ChunkerSettings(plugin, this);
        this.storage = new ChunkerStorage(plugin, this);
    }

    public void unload() {
        placedChunker.forEach((k, v) -> {
            if (v != null) {
                this.storage.save(k, v);
                toggleForceLoad(k, v.getRadius(), false);
            }
        });
    }

    public boolean isChunker(Location location) {
        for (Location loc : placedChunker.keySet()) {
            if (loc.equals(location)) {
                return true;
            }
        }
        return false;
    }

    public void placeChunker(Location location, Chunker chunker) {
        placedChunker.put(location, chunker);
        toggleForceLoad(location, chunker.getRadius(), true);
    }

    public void removeChunker(Location location, Chunker chunker) {
        this.storage.remove(location);
        placedChunker.remove(location);
        toggleForceLoad(location, chunker.getRadius(), false);
    }

    public void toggleForceLoad(Location loc, int radius, boolean state) {
        World w = loc.getWorld();
        if (w == null)
            return;

        int cx = loc.getChunk().getX(), cz = loc.getChunk().getZ();
        int targetRadius = Math.max(0, (radius - 1) / 2);

        for (int dx = -targetRadius; dx <= targetRadius; dx++) {
            for (int dz = -targetRadius; dz <= targetRadius; dz++) {
                int targetX = cx + dx;
                int targetZ = cz + dz;
                if (state) {
                    w.addPluginChunkTicket(targetX, targetZ, plugin);
                } else {
                    w.removePluginChunkTicket(targetX, targetZ, plugin);
                }
            }
        }
    }

    public Chunker getPlacedChuncker(Location location) {
        if (isChunker(location)) {
            return placedChunker.get(location);
        }
        return null;
    }

    public void register(String key, Chunker chunker) {
        ChunkerHashMap.put(key, chunker);
    }

    public Chunker getChunker(String key) {
        return ChunkerHashMap.get(key);
    }

    public List<Chunker> getAll() {
        return ChunkerHashMap.values().stream().toList();
    }

}
