package me.argentochest.northlights.commands;

import me.argentochest.northlights.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DShopOpen implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            if(p.hasPermission("nl.dshopopen")) {
                int page = Main.getPlugin().getConfig().getInt("start_page");
                if(!Main.getPlugin().getGUI().getInventoryList().isEmpty()) {
                    p.closeInventory();
                    p.openInventory(Main.getPlugin().getGUI().getInventoryList().get(page-1));
                }
                else {
                    Main.send(p, "&7Магазин пуст");
                }
            }
            else {
                Main.send(p, Main.getPlugin().getLang().getString("no_pex"));
            }
        }
        return false;
    }
}
