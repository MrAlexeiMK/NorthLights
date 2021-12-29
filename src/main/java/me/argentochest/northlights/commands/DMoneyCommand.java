package me.argentochest.northlights.commands;

import me.argentochest.northlights.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class DMoneyCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            if(args.length >= 3) {
                if(args[0].equalsIgnoreCase("give")) {
                    try {
                        double wars = Double.parseDouble(args[2]);
                        Main.getDB().addLights(args[1], wars);
                    } catch (Exception e) {}
                }
                else if(args[0].equalsIgnoreCase("remove")) {
                    try {
                        double wars = Double.parseDouble(args[2]);
                        Main.getDB().removeLights(args[1], wars);
                    } catch (Exception e) {}
                }
            }
        }
        return false;
    }
}
