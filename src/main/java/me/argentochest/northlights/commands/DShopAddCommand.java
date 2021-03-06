package me.argentochest.northlights.commands;

import me.argentochest.northlights.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

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
                            action += args[i];
                            if(i != args.length - 1) action += " ";
                        }
                        String key = page+","+slot;
                        ItemStack it = p.getItemInHand();
                        if(action.equalsIgnoreCase("super-pickaxe")) {
                            ItemMeta meta = it.getItemMeta();
                            List<String> lore = new ArrayList<>();
                            lore.add("§7Супер-кирка");
                            meta.setLore(lore);
                            it.setItemMeta(meta);
                        }
                        if(action.equalsIgnoreCase("super-axe")) {
                            ItemMeta meta = it.getItemMeta();
                            List<String> lore = new ArrayList<>();
                            lore.add("§7Супер-топор");
                            meta.setLore(lore);
                            it.setItemMeta(meta);
                        }
                        Main.getPlugin().getShop().set("guis."+key+".item", it);
                        Main.getPlugin().getShop().set("guis."+key+".price", price);
                        Main.getPlugin().getShop().set("guis."+key+".action", action);
                        Main.getPlugin().getShopFile().accept();
                        Main.getPlugin().getGUI().addItem(page, slot, price, action, it);
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
