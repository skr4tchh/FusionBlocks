package pw.fusionmine.fusionblocks.blocks.furnace;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Furnace;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.ItemStack;
import pw.fusionmine.fusionblocks.FusionBlocks;
import pw.fusionmine.fusionblocks.blocks.furnace.storage.FurnaceSettings;
import pw.fusionmine.fusionblocks.blocks.furnace.storage.FurnaceStorage;

import java.util.HashMap;
import java.util.List;

public class FurnaceManager {

    public HashMap<String, CustomFurnace> FurnaceHashMap = new HashMap<>();

    public HashMap<Location, CustomFurnace> placedFurnace = new HashMap<>();

    private FurnaceSettings settings;

    private FurnaceStorage storage;

    public FurnaceManager(FusionBlocks plugin) {
        this.settings = new FurnaceSettings(plugin, this);
        this.storage = new FurnaceStorage(plugin, this);
    }

    public void unload() {
        placedFurnace.forEach((k, v) -> {
            if (v != null) {
                this.storage.save(k, v);
            }
        });
    }

    public boolean isFurnace(Location location) {
        for (Location loc : placedFurnace.keySet()) {
            if (loc.equals(location)) {
                return true;
            }
        }
        return false;
    }

    public void placeFurnace(Location location, CustomFurnace furnace) {
        placedFurnace.put(location, furnace);
    }

    public void removeFurnace(Location location) {
        this.storage.remove(location);
        placedFurnace.remove(location);
    }

    public void dropFurnace(Location location, Block block, CustomFurnace customFurnace) {
        if (block.getState() instanceof Furnace) {
            Furnace furnace = (Furnace) block.getState();
            FurnaceInventory inv = furnace.getInventory();
            dropIfPresent(inv.getSmelting(), location);
            dropIfPresent(inv.getFuel(), location);
            dropIfPresent(inv.getResult(), location);
            inv.clear();
        }
        location.getWorld().dropItemNaturally(location, customFurnace.getItemStack());
    }

    private void dropIfPresent(ItemStack item, Location location) {
        if (item != null && item.getType() != Material.AIR)
            location.getWorld().dropItemNaturally(location, item);
    }

    public CustomFurnace getPlacedFurnace(Location location) {
        if (isFurnace(location)) {
            return placedFurnace.get(location);
        }
        return null;
    }

    public void register(String key, CustomFurnace furnace) {
        FurnaceHashMap.put(key, furnace);
    }

    public CustomFurnace getFurnace(String key) {
        return FurnaceHashMap.get(key);
    }

    public List<CustomFurnace> getAll() {
        return FurnaceHashMap.values().stream().toList();
    }

}
