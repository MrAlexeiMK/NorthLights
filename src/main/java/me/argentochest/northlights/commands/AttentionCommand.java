package me.argentochest.northlights.commands;

import me.argentochest.northlights.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AttentionCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            if(p.hasPermission("nl.attention")) {
                String msg = "";
                if(args.length >= 1) {
                    for(int i = 0; i < args.length; ++i) msg += args[i] + " ";
                    for(Player pl : Bukkit.getOnlinePlayers()) {
                        Main.send(pl, "&f=== &c&lОБЪЯВЛЕНИЕ &f===");
                        String[] msgs = msg.split("\\|");
                        for(String str : msgs) {
                            Main.send(pl, "&e" + str);
                        }
                        Main.send(pl, "&f=== &a------------ &f===");
                    }
                }
            }
            else {
                Main.send(p, Main.getPlugin().getLang().getString("no_pex"));
            }
        }
        return false;
    }
}
