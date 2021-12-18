package me.argentochest.northlights.commands;

import me.argentochest.northlights.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DShopAddCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            if(p.hasPermission("nl.dshopadd")) {
                if(args.length >= 4) {
                    try {
                        int page = Integer.parseInt(args[0]);
                        int slot = Integer.parseInt(args[1]);
                        double price = Double.parseDouble(args[2]);
                        String action = "";
                        for(int i = 3; i < args.length; ++i) {
                            action += args[i] + " ";
                        }
                        String key = page+","+slot;
                        Main.getPlugin().getShop().set("guis."+key+".item", p.getItemInHand());
                        Main.getPlugin().getShop().set("guis."+key+".price", price);
                        Main.getPlugin().getShop().set("guis."+key+".action", action);
                        Main.getPlugin().getShopFile().accept();
                        Main.getPlugin().getGUI().addItem(page, slot, price, action, p.getItemInHand());
                        Main.send(p, "&aУспешно!");
                    } catch (Exception e) {
                        Main.send(p, "&7/dshopadd [страница] [слот] [цена] [cmd] - добавить предмет в донатный шоп в вашей руке");
                    }
                }
                else {
                    Main.send(p, "&7/dshopadd [страница] [слот] [цена] [cmd] - добавить предмет в донатный шоп в вашей руке");
                }
            }
            else {
                Main.send(p, Main.getPlugin().getLang().getString("no_pex"));
            }
        }
        return false;
    }
}
