package me.argentochest.northlights.handlers;

import com.extendedclip.deluxemenus.menu.Menu;
import me.argentochest.northlights.Main;
import me.argentochest.northlights.other.GUI;
import me.argentochest.northlights.other.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class EventsHandler implements Listener {
    @EventHandler
    public void join(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        Main.getDB().addToDB(p.getName()
                , Main.getPlugin().getConfig().getDouble("start_capital.wars")
                , Main.getPlugin().getConfig().getDouble("start_capital.lights"));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void breakk(BlockBreakEvent e) {
        Player p = e.getPlayer();
        try {
            ItemStack it = p.getItemInHand();
            if (it != null) {
                Location loc = e.getBlock().getLocation();
                ItemMeta meta = it.getItemMeta();
                if (meta != null && meta.getLore() != null) {
                    if (meta.getLore().contains("§7Супер-кирка")) {
                        for(int x = loc.getBlockX()-1; x <= loc.getBlockX()+1; ++x) {
                            for(int y = loc.getBlockY()-1; y <= loc.getBlockY()+1; ++y) {
                                for(int z = loc.getBlockZ()-1; z <= loc.getBlockZ()+1; ++z) {
                                    if(x == loc.getBlockX() && y == loc.getBlockY() && z == loc.getBlockZ()) continue;
                                    Location new_loc = new Location(loc.getWorld(), x, y, z);
                                    if(Main.getPlugin().canBreak(p, new_loc)) {
                                        if(new_loc.getBlock().getType() != Material.BEDROCK && new_loc.getBlock().getType() != Material.BARRIER) {
                                            new_loc.getBlock().breakNaturally();
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else if(meta.getLore().contains("§7Супер-топор")) {
                        boolean isTree = false;
                        for(int y = loc.getBlockY()+1; y < 255; ++y) {
                            Location pred_loc = new Location(loc.getWorld(), loc.getBlockX(), y-1, loc.getBlockZ());
                            Location new_loc = new Location(loc.getWorld(), loc.getBlockX(), y, loc.getBlockZ());
                            if(new_loc.getBlock().getType() == Material.AIR) break;
                            if(Main.logs.contains(pred_loc.getBlock().getType())
                                    && Main.leaves.contains(new_loc.getBlock().getType())) {
                                isTree = true;
                                break;
                            }
                        }
                        if(isTree) {
                            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), () -> {
                                bfsTree(p, loc, 5);
                            }, 1L);
                        }
                    }
                }
            }
        } catch (Exception ex) {}
    }

    public void removeAbove(String name) {
        try {
            if (Main.getAboveCooldowns().containsKey(name)) {
                long sec = Main.getAboveCooldowns().get(name);
                long cur = System.currentTimeMillis() / 1000;
                if (cur - sec >= 4) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tab player " + name + " abovename");
                }
            }
        } catch (Exception ignored) {}
    }

    @EventHandler(priority=EventPriority.LOW)
    public void onChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        String name = p.getName();
        String above = e.getMessage().replaceAll("&", "");
        if(above.length() > 25) {
            above = above.substring(0, 25);
            above += "...";
        }
        String finalAbove = above;

        try {
            //Bukkit.getScheduler().runTask(Main.getPlugin(), () -> {
            //    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tab player " + name + " abovename " + finalAbove);
            //});
            //Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), () -> removeAbove(name), 20 * 5);
        } catch (Exception ignored) {}

        long time = System.currentTimeMillis()/1000;
        if(!Main.getAboveCooldowns().containsKey(name)) {
            Main.getAboveCooldowns().put(name, time);
        }
        else {
            Main.getAboveCooldowns().replace(name, time);
        }
        for(Player pl : Bukkit.getOnlinePlayers()) {
            Location loc1 = pl.getLocation();
            Location loc2 = p.getLocation();
            double x1 = loc1.getX(), z1 = loc1.getZ();
            double x2 = loc2.getX(), z2 = loc2.getZ();
            if(loc1.getWorld().getName().equals(loc2.getWorld().getName())) {
                if(Math.abs(x2-x1)*Math.abs(x2-x1)+Math.abs(z2-z1)*Math.abs(z2-z1) <= 400) {
                    pl.playSound(pl.getLocation(), Sound.BLOCK_NOTE_BLOCK_COW_BELL, 1, 1);
                }
            }
        }
    }

    public void bfsTree(Player p, Location start, int max_radius) {
        Queue<Location> queue = new LinkedList<>();
        queue.add(start.clone());
        while(!queue.isEmpty()) {
            Location loc = queue.remove();
            if(loc.getBlock().getType() == Material.BEDROCK || loc.getBlock().getType() == Material.BARRIER) continue;
            if(Main.getPlugin().canBreak(p, loc)) {
                if (Main.logs.contains(loc.getBlock().getType()) || (
                        loc.getBlockX() == start.getBlockX() &&
                                loc.getBlockZ() == start.getBlockZ() &&
                                loc.getBlockY() == start.getBlockY())) {
                    if (Math.abs(loc.getBlockX() - start.getBlockX()) <= max_radius &&
                            Math.abs(loc.getBlockZ() - start.getBlockZ()) <= max_radius) {
                        loc.getBlock().breakNaturally();
                        List<Location> locs = new ArrayList<>();
                        List<Integer> shifts = new ArrayList<>(Arrays.asList(-1, 0, 1));
                        for (int x : shifts) {
                            for (int y : shifts) {
                                for (int z : shifts) {
                                    if (x == 0 && y == 0 && z == 0) continue;
                                    locs.add(new Location(loc.getWorld(), loc.getBlockX() + x,
                                            loc.getBlockY() + y, loc.getBlockZ() + z));
                                }
                            }
                        }
                        queue.addAll(locs);
                    }
                }
            }
        }
    }

    @EventHandler
    public void closeInv(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();
        p.updateInventory();
    }

    @EventHandler
    public void click(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        ItemStack it = e.getCurrentItem();
        if (it != null) {
            GUI gui = Main.getPlugin().getGUI();
            for (int page = 1; page <= gui.getInventoryList().size(); ++page) {
                Inventory inv = gui.getInventoryList().get(page - 1);
                if (e.getInventory().equals(inv)) {
                    if (!e.isShiftClick() && e.getClick() != ClickType.NUMBER_KEY && e.getClick() != ClickType.SWAP_OFFHAND) {
                        int raw = e.getRawSlot();
                        if (raw == Main.getPlugin().getLeft(page)) {
                            if (page > 1) {
                                p.openInventory(gui.getInventoryList().get(page - 2));
                            }
                        } else if (raw == Main.getPlugin().getRight(page)) {
                            if (page < gui.getInventoryList().size()) {
                                p.openInventory(gui.getInventoryList().get(page));
                            }
                        } else if (gui.getActionMap().containsKey(it)) {
                            Pair<String, Double> pair = gui.getAction(it);
                            String cmd = pair.getFirst();
                            double price = pair.getSecond();
                            if (price <= 0.0) {
                                Main.send(p, "&fСсылка: " + it.getItemMeta().getDisplayName());
                            } else if (Main.getDB().getLights(p.getName()) >= price) {
                                Main.getDB().removeLights(p.getName(), price);
                                Main.send(p, Main.getPlugin().getLang().getString("success_buy"));
                                if (cmd.equalsIgnoreCase("give") ||
                                        cmd.equalsIgnoreCase("super-pickaxe") ||
                                        cmd.equalsIgnoreCase("super-axe")) {
                                    ItemStack clone = it.clone();
                                    ItemMeta meta = clone.getItemMeta();
                                    List<String> lore = meta.getLore();
                                    if (!lore.contains("§7Супер-кирка") && !lore.contains("§7Супер-топор")) {
                                        meta.setLore(new ArrayList<>());
                                    }
                                    clone.setItemMeta(meta);
                                    if (Main.getPlugin().hasEmptySlot(p)) {
                                        p.getInventory().addItem(clone);
                                    } else {
                                        p.getWorld().dropItemNaturally(p.getLocation(), clone);
                                    }
                                } else if (cmd.contains("player:")) {
                                    cmd = cmd.split(":")[1];
                                    Menu menu = Menu.getMenuByCommand(cmd.toLowerCase());
                                    if (menu != null) {
                                        if (!menu.registersCommand()) {
                                            menu.openMenu(p);
                                        }
                                    }
                                } else {
                                    ConsoleCommandSender ccs = Main.getPlugin().getServer().getConsoleSender();
                                    String[] arr = cmd.split("\\|");
                                    for (String str : arr) {
                                        str = str.replaceAll("\\{user}", p.getName());
                                        str = str.replaceAll("\\{player}", p.getName());
                                        str = str.replaceAll("%player%", p.getName());
                                        Bukkit.dispatchCommand(ccs, str);
                                        Main.getPlugin().getLogger().info(str);
                                        Main.getDB().logLights(p.getName(), price, str);
                                    }
                                }
                            } else {
                                Main.send(p, Main.getPlugin().getLang().getString("money_not_enough"));
                            }
                        }
                        e.setCancelled(true);
                        break;
                    }
                    else {
                        p.closeInventory();
                        Bukkit.getScheduler().runTaskLater(Main.getPlugin(), p::updateInventory, 1L);
                        e.setCancelled(true);
                    }
                }
            }
        }
    }
}
