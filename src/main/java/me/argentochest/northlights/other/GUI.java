package me.argentochest.northlights.other;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.argentochest.northlights.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class GUI {
    private List<Inventory> inventoryList;
    private Map<ItemStack, Pair<String, Double>> actionMap;

    public GUI() {
        actionMap = new HashMap<>();
        inventoryList = new ArrayList<>();
        ConfigurationSection cs = Main.getPlugin().getShop().getConfigurationSection("guis");
        for(String key : cs.getKeys(false)) {
            try {
                int gui = Integer.parseInt(key.split(",")[0]);
                int slot = Integer.parseInt(key.split(",")[1]);
                int size = inventoryList.size();
                for(int i = size+1; i < gui+1; ++i) {
                    inventoryList.add(getDefault(i));
                }
                double price = Main.getPlugin().getShop().getDouble("guis."+key+".price");
                String action = Main.getPlugin().getShop().getString("guis."+key+".action");
                ItemStack it = Main.getPlugin().getShop().getItemStack("guis."+key+".item").clone();

                List<String> lore = new ArrayList<>();
                if(it != null && it.getItemMeta() != null && it.getItemMeta().getLore() != null) {
                    if (!it.getItemMeta().getLore().isEmpty()) {
                        for (String r : it.getItemMeta().getLore()) {
                            if (!r.contains("Цена")) {
                                lore.add(r.replaceAll("&", "§"));
                            }
                        }
                    }
                }
                if(price > 0.0) lore.add("§7Цена: §6" + price + " §bЛайтов");
                ItemMeta meta = it.getItemMeta();
                if(meta != null) {
                    meta.setDisplayName(meta.getDisplayName().replaceAll("&", "§"));
                    meta.setLore(lore);
                    it.setItemMeta(meta);
                }

                actionMap.put(it, new Pair<>(action, price));
                inventoryList.get(gui-1).setItem(slot, it);
            } catch (Exception e) {
                Main.getPlugin().getLogger().warning("Ключи в shop.yml заданы некорректно, лог ошибки:");
                e.printStackTrace();
            }
        }
    }

    public Pair<String, Double> getAction(ItemStack it) {
        if(!actionMap.containsKey(it)) return null;
        return actionMap.get(it);
    }

    public Inventory getDefault(int page) {
        Inventory def = Bukkit.createInventory(null, 9*Main.getPlugin().getRows(page), "§eМагазин §f(§bЛайты§f)");
        return def;
    }
    public void addItem(int page, int slot, double price, String cmd, ItemStack it) {
        List<String> lore = new ArrayList<>();
        if(price > 0.0) lore.add("§7Цена: §6"+price + " §bЛайтов");
        ItemMeta meta = it.getItemMeta();
        meta.setLore(lore);
        it.setItemMeta(meta);
        actionMap.put(it, new Pair<>(cmd, price));
        int size = inventoryList.size();
        for(int i = size+1; i < page+1; ++i) {
            inventoryList.add(getDefault(i));
        }
        inventoryList.get(page-1).setItem(slot, it);
    }
}
