package me.argentochest.northlights.commands;

import me.argentochest.northlights.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DPayCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            if(p.hasPermission("nl.dpay")) {
                if(args.length >= 2) {
                    Player pl = Bukkit.getPlayer(args[0]);
                    if(pl != null && pl.isOnline()) {
                        try {
                            double lights = Double.parseDouble(args[1]);
                            if(Main.getDB().getLights(p.getName()) >= lights) {
                                Main.getDB().removeLights(p.getName(), lights);
                                Main.getDB().addLights(pl.getName(), lights);
                                Main.send(p, Main.getPlugin().getLang().getString("dpay_to_player").
                                        replaceAll("%money%", String.valueOf(lights)).
                                        replaceAll("%player%", pl.getName()));
                                Main.send(pl, Main.getPlugin().getLang().getString("dpay_from_player").
                                        replaceAll("%money%", String.valueOf(lights)).
                                        replaceAll("%player%", p.getName()));
                                Main.getDB().logLights(p.getName(), lights, "pay to "+pl.getName());
                            }
                            else {
                                Main.send(p, Main.getPlugin().getLang().getString("money_not_enough"));
                            }
                        } catch (Exception e) {
                            Main.send(p, "&7/dpay [&eигрок&7] [&eмонеты&7] &f- перевести монеты игроку");
                        }
                    }
                    else {
                        Main.send(p, Main.getPlugin().getLang().getString("player_not_found"));
                    }
                }
                else {
                    Main.send(p, "&7/dpay [&eигрок&7] [&eмонеты&7] &f- перевести монеты игроку");
                }
            }
            else {
                Main.send(p, Main.getPlugin().getLang().getString("no_pex"));
            }
        }
        return false;
    }
}
