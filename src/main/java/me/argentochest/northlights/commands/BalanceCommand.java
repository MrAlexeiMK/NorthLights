package me.argentochest.northlights.commands;

import me.argentochest.northlights.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BalanceCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            if(p.hasPermission("nl.balance")) {
                if(args.length == 0) {
                    Main.send(p, Main.getPlugin().getLang().getString("balance").replaceAll("%money%",
                            String.valueOf(Main.getDB().getWars(p.getName()))));
                }
                else  {
                    if(p.hasPermission("nl.balance.player")) {
                        try {
                            Player pl = Bukkit.getPlayer(args[0]);
                            if(pl.isOnline()) {
                                Main.send(p, Main.getPlugin().getLang().getString("balance").replaceAll("%money%",
                                        String.valueOf(Main.getDB().getWars(pl.getName()))));
                            }
                        } catch (Exception e) {
                            Main.send(p, Main.getPlugin().getLang().getString("player_not_found"));
                        }
                    }
                    else {
                        Main.send(p, Main.getPlugin().getLang().getString("no_pex"));
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
