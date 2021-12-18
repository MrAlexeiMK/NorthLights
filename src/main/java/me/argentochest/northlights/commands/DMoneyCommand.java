package me.argentochest.northlights.commands;

import me.argentochest.northlights.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DMoneyCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            if(args.length == 0) {
                if(p.hasPermission("nl.dbalance")) {
                    Main.send(p, Main.getPlugin().getLang().getString("dbalance").replaceAll("%money%",
                            String.valueOf(Main.getDB().getLights(p.getName()))));
                }
                else {
                    Main.send(p, Main.getPlugin().getLang().getString("no_pex"));
                }
            }
            else if(args.length >= 3) {
                if(args[0].equalsIgnoreCase("give")) {
                    if(p.hasPermission("nl.dmoney.give")) {
                        Player pl = Bukkit.getPlayer(args[1]);
                        if(pl != null && pl.isOnline()) {
                            try {
                                double wars = Double.parseDouble(args[2]);
                                Main.getDB().addLights(pl.getName(), wars);
                                Main.send(p, Main.getPlugin().getLang().getString("dpay_to_player").
                                        replaceAll("%money%", String.valueOf(wars)).
                                        replaceAll("%player%", pl.getName()));
                                Main.send(pl, Main.getPlugin().getLang().getString("dpay_from_player").
                                        replaceAll("%money%", String.valueOf(wars)).
                                        replaceAll("%player%", p.getName()));
                            } catch (Exception e) {}
                        }
                    }
                    else {
                        Main.send(p, Main.getPlugin().getLang().getString("no_pex"));
                    }
                }
                else if(args[0].equalsIgnoreCase("remove")) {
                    if(p.hasPermission("nl.dmoney.remove")) {
                        Player pl = Bukkit.getPlayer(args[1]);
                        if(pl != null && pl.isOnline()) {
                            try {
                                double wars = Double.parseDouble(args[2]);
                                Main.getDB().removeLights(pl.getName(), wars);
                                Main.send(p, Main.getPlugin().getLang().getString("success"));
                                Main.send(pl, Main.getPlugin().getLang().getString("dmoney_remove").
                                        replaceAll("%money%", String.valueOf(wars)));
                            } catch (Exception e) {}
                        }
                    }
                    else {
                        Main.send(p, Main.getPlugin().getLang().getString("no_pex"));
                    }
                }
            }
        }
        return false;
    }
}
