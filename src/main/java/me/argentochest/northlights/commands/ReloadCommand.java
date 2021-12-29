package me.argentochest.northlights.commands;

import me.argentochest.northlights.Main;
import me.argentochest.northlights.YearBossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReloadCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender.hasPermission("nl.reload")){
            Main.getPlugin().getConfigFile().reloadConfig();
            Main.getPlugin().getLangFile().reloadConfig();
            Main.getPlugin().getShopFile().reloadConfig();
            Main.getPlugin().initGUI();
            if(sender instanceof Player) {
                Player p = (Player) sender;
                Main.send(p, "&aПерезагружено");
            }
        }
        return false;
    }
}
