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
    private ItemStack left, right, info;
    private Map<ItemStack, Pair<String, Double>> actionMap;

    public GUI() {
        actionMap = new HashMap<>();
        left = new ItemStack(Material.SLIME_BALL);
        ItemMeta meta = left.getItemMeta();
        meta.setDisplayName("§c§l←");
        left.setItemMeta(meta);

        right = new ItemStack(Material.SLIME_BALL);
        meta = right.getItemMeta();
        meta.setDisplayName("§c§l→");
        right.setItemMeta(meta);

        info = new ItemStack(Material.OAK_SIGN);

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
                lore.add("§7Цена: §6"+price + " §bЛайтов");
                meta = it.getItemMeta();
                meta.setLore(lore);
                it.setItemMeta(meta);

                actionMap.put(it, new Pair<>(action, price));
                inventoryList.get(gui-1).setItem(slot, it);
            } catch (Exception e) {
                Main.getPlugin().getLogger().warning("Ключи в shop.yml заданы некорректно");
            }
        }
    }

    public Pair<String, Double> getAction(ItemStack it) {
        if(!actionMap.containsKey(it)) return null;
        return actionMap.get(it);
    }

    public Inventory getDefault(int page) {
        Inventory def = Bukkit.createInventory(null, 9*5, "§eМагазин §f(§bЛайты§f)");
        ItemMeta meta = info.getItemMeta();
        meta.setDisplayName("§eСтраница: §c"+page);
        info.setItemMeta(meta);

        for(int i = 27; i < 36; ++i) {
            def.setItem(i, new ItemStack(Material.BLACK_STAINED_GLASS));
        }
        def.setItem(44, right);
        def.setItem(36, left);
        def.setItem(40, info);
        return def;
    }

    public void addItem(int page, int slot, double price, String cmd, ItemStack it) {
        List<String> lore = new ArrayList<>();
        lore.add("§7Цена: §6"+price + " §bЛайтов");
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
