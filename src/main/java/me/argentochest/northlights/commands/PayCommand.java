package me.argentochest.northlights.commands;

import me.argentochest.northlights.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PayCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            if(p.hasPermission("nl.pay")) {
                if(args.length >= 2) {
                    Player pl = Bukkit.getPlayer(args[0]);
                    if(pl != null && pl.isOnline()) {
                        try {
                            long wars = Long.parseLong(args[1]);
                            if(Main.getDB().getWars(p.getName()) >= wars) {
                                Main.getDB().removeWars(p.getName(), wars);
                                Main.getDB().addWars(pl.getName(), wars);
                                Main.send(p, Main.getPlugin().getLang().getString("pay_to_player").
                                        replaceAll("%money%", String.valueOf(wars)).
                                        replaceAll("%player%", pl.getName()));
                                Main.send(pl, Main.getPlugin().getLang().getString("pay_from_player").
                                        replaceAll("%money%", String.valueOf(wars)).
                                        replaceAll("%player%", p.getName()));
                            }
                            else {
                                Main.send(p, Main.getPlugin().getLang().getString("money_not_enough"));
                            }
                        } catch (Exception e) {
                            Main.send(p, "&7/pay [&eигрок&7] [&eмонеты&7] &f- перевести монеты игроку");
                        }
                    }
                    else {
                        Main.send(p, Main.getPlugin().getLang().getString("player_not_found"));
                    }
                }
                else {
                    Main.send(p, "&7/pay [&eигрок&7] [&eмонеты&7] &f- перевести монеты игроку");
                }
            }
            else {
                Main.send(p, Main.getPlugin().getLang().getString("no_pex"));
            }
        }
        return false;
    }
}
