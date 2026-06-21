package pw.fusionmine.fusionblocks.commands;

import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import pw.fusionmine.fusionblocks.FusionBlocks;
import pw.fusionmine.fusionblocks.blocks.chunker.Chunker;
import pw.fusionmine.fusionblocks.blocks.chunker.ChunkerManager;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class ChunkerCommands implements CommandExecutor, TabCompleter {

    private FusionBlocks plugin;

    private ChunkerManager chunkerManager;

    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        if (cs.hasPermission("fusionblocks.admin")) {
            if (args.length >= 3 && args[0].equalsIgnoreCase("give")) {
                String key = args[1];
                Chunker chunker = chunkerManager.getChunker(key);

                if (chunker == null) {
                    cs.sendMessage(plugin.getLang().prefix("nofound"));
                    return true;
                }

                Player p = Bukkit.getPlayer(args[2]);
                if (p == null) {
                    cs.sendMessage(plugin.getLang().prefix("nofoundplayer"));
                    return true;
                }

                p.getInventory().addItem(chunker.getItemStack());
                return true;
            }
        } else {
            cs.sendMessage(plugin.getLang().prefix("nopermission"));
        }

        return true;
    }

    public List<String> onTabComplete(CommandSender cs, Command cmd, String s, String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (args.length == 1) {
            suggestions.add("give");
        }

        if(args[0].equalsIgnoreCase("give")) {
            if (args.length == 2) {
                suggestions.addAll(chunkerManager.ChunkerHashMap.keySet());
            } else if (args.length == 3) {
                suggestions.addAll(Bukkit.getOnlinePlayers().stream().map(p -> p.getName()).toList());
            }
        }

        return suggestions;
    }

}
