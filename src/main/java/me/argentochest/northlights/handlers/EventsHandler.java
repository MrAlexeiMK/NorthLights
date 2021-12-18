package me.argentochest.northlights.handlers;

import me.argentochest.northlights.Main;
import me.argentochest.northlights.other.GUI;
import me.argentochest.northlights.other.Pair;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class EventsHandler implements Listener {
    @EventHandler
    public void join(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        Main.getDB().addToDB(p.getName()
                , Main.getPlugin().getConfig().getDouble("start_capital.wars")
                , Main.getPlugin().getConfig().getDouble("start_capital.lights"));
    }

    @EventHandler
    public void click(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        ItemStack it = e.getCurrentItem();
        if(it != null) {
            GUI gui = Main.getPlugin().getGUI();
            for (Inventory inv : gui.getInventoryList()) {
                if (e.getInventory().equals(inv)) {
                    ItemStack info = inv.getItem(40);
                    int page = Integer.parseInt(info.getItemMeta().getDisplayName().split(": §c")[1]);
                    if(it.equals(gui.getLeft())) {
                        if(page > 1) {
                            p.openInventory(gui.getInventoryList().get(page-2));
                        }
                    }
                    else if(it.equals(gui.getRight())) {
                        if(page < gui.getInventoryList().size()) {
                            p.openInventory(gui.getInventoryList().get(page));
                        }
                    }
                    else if(gui.getActionMap().containsKey(it)) {
                        Pair<String, Double> pair = gui.getAction(it);
                        String cmd = pair.getFirst();
                        double price = pair.getSecond();
                        if(Main.getDB().getLights(p.getName()) >= price) {
                            Main.getDB().removeLights(p.getName(), price);
                            Main.send(p, Main.getPlugin().getLang().getString("success_buy"));
                            if(!cmd.equalsIgnoreCase("give")) {
                                ConsoleCommandSender ccs = Main.getPlugin().getServer().getConsoleSender();
                                Bukkit.dispatchCommand(ccs, cmd);
                                Main.getPlugin().getLogger().info(cmd);
                                Main.getDB().logLights(p.getName(), price, cmd);
                            }
                            else {
                                ItemStack clone = it.clone();
                                ItemMeta meta = clone.getItemMeta();
                                meta.setLore(new ArrayList<>());
                                clone.setItemMeta(meta);
                                if(Main.getPlugin().hasEmptySlot(p)) {
                                    p.getInventory().addItem(clone);
                                }
                                else {
                                    p.getWorld().dropItemNaturally(p.getLocation(), clone);
                                }
                            }
                        }
                        else {
                            Main.send(p, Main.getPlugin().getLang().getString("money_not_enough"));
                        }
                    }
                    e.setCancelled(true);
                    break;
                }
            }
        }
    }
}
