package pw.fusionmine.fusionblocks;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import pw.fusionmine.fusionblocks.blocks.brewingstand.BrewingStandManager;
import pw.fusionmine.fusionblocks.blocks.furnace.FurnaceManager;
import pw.fusionmine.fusionblocks.blocks.tnt.TNTManager;
import pw.fusionmine.fusionblocks.commands.FurnaceCommands;
import pw.fusionmine.fusionblocks.commands.TNTCommands;
import pw.fusionmine.fusionblocks.lang.Lang;
import pw.fusionmine.fusionblocks.listeners.FurnaceListener;
import pw.fusionmine.fusionblocks.listeners.TNTListener;
import pw.fusionmine.fusionblocks.tasks.BrewingStandTask;

import java.io.File;

@Getter
public class FusionBlocks extends JavaPlugin {

    private TNTManager tntManager;

    private FurnaceManager furnaceManager;

    private BrewingStandManager brewingStandManager;

    private Lang lang;

    private static FusionBlocks instance;

    @Override
    public void onEnable() {
        instance = this;

        this.lang = new Lang(getDataFolder() + File.separator + "lang.yml");
        this.tntManager = new TNTManager(this);
        this.furnaceManager = new FurnaceManager(this);
        this.brewingStandManager = new BrewingStandManager(this);

        registerListeners();
        registerCommands();
        registerTasks();
    }

    @Override
    public void onDisable() {
        this.tntManager.unload();
        this.furnaceManager.unload();
        this.brewingStandManager.unload();
    }

    private void registerListeners() {
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new TNTListener(this.tntManager), this);
        pluginManager.registerEvents(new FurnaceListener(this, this.furnaceManager), this);
    }

    private void registerCommands() {
        getServer().getPluginCommand("fusioncustomtnt").setExecutor(new TNTCommands(this, tntManager));
        getServer().getPluginCommand("fusioncustomfurnace").setExecutor(new FurnaceCommands(this, furnaceManager));
    }

    private void registerTasks() {
        BrewingStandTask brewingStandTask = new BrewingStandTask(this, this.brewingStandManager);
        brewingStandTask.runTaskTimer(this, 1L, 1L);
    }

    public static FusionBlocks getInstance() {
        return instance;
    }

}
