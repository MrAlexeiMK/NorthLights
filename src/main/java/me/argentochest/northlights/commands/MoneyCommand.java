package me.argentochest.northlights.commands;

import me.argentochest.northlights.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class MoneyCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof ConsoleCommandSender) {
            if(args.length >= 3) {
                if(args[0].equalsIgnoreCase("give")) {
                    try {
                        double wars = Double.parseDouble(args[2]);
                        Main.getDB().addWars(args[1], wars);
                    } catch (Exception e) {}
                }
                else if(args[0].equalsIgnoreCase("remove")) {
                    try {
                        double wars = Double.parseDouble(args[2]);
                        Main.getDB().removeWars(args[1], wars);
                    } catch (Exception e) {}
                }
            }
        }
        else if(sender instanceof Player) {
            Player p = (Player) sender;
            if(args.length == 0) {
                if(p.hasPermission("nl.balance")) {
                    Main.send(p, Main.getPlugin().getLang().getString("balance").replaceAll("%money%",
                            String.valueOf(Main.getDB().getWars(p.getName()))));
                }
                else {
                    Main.send(p, Main.getPlugin().getLang().getString("no_pex"));
                }
            }
            else if(args.length >= 3) {
                if(args[0].equalsIgnoreCase("give")) {
                    if(p.hasPermission("nl.money.give")) {
                        Player pl = Bukkit.getPlayer(args[1]);
                        if(pl != null && pl.isOnline()) {
                            try {
                                double wars = Double.parseDouble(args[2]);
                                Main.getDB().addWars(pl.getName(), wars);
                                Main.send(p, Main.getPlugin().getLang().getString("pay_to_player").
                                        replaceAll("%money%", String.valueOf(wars)).
                                        replaceAll("%player%", pl.getName()));
                                Main.send(pl, Main.getPlugin().getLang().getString("pay_from_player").
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
                    if(p.hasPermission("nl.money.remove")) {
                        Player pl = Bukkit.getPlayer(args[1]);
                        if(pl != null && pl.isOnline()) {
                            try {
                                double wars = Double.parseDouble(args[2]);
                                Main.getDB().removeWars(pl.getName(), wars);
                                Main.send(p, Main.getPlugin().getLang().getString("success"));
                                Main.send(pl, Main.getPlugin().getLang().getString("money_remove").
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
