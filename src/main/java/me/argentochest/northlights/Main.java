package me.argentochest.northlights;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Town;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.internal.platform.WorldGuardPlatform;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionType;
import me.argentochest.northlights.commands.*;
import me.argentochest.northlights.configs.Config;
import me.argentochest.northlights.configs.Lang;
import me.argentochest.northlights.configs.Shop;
import me.argentochest.northlights.handlers.BarHandler;
import me.argentochest.northlights.handlers.EventsHandler;
import me.argentochest.northlights.handlers.FixHandler;
import me.argentochest.northlights.other.GUI;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class Main extends JavaPlugin {
    private static Main plugin;
    private static DB db;
    private static Config config;
    private static Lang lang;
    private static Shop shop;
    private static GUI gui;
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static WorldGuardPlugin worldGuard = null;
    private static Map<String, Long> above_cooldowns;
    public static final List<Material> logs = new ArrayList<>(Arrays.asList(
            Material.ACACIA_LOG,
            Material.BIRCH_LOG,
            Material.JUNGLE_LOG,
            Material.DARK_OAK_LOG,
            Material.OAK_LOG,
            Material.SPRUCE_LOG
    ));
    public static final  List<Material> leaves = new ArrayList<>(Arrays.asList(
            Material.ACACIA_LEAVES,
            Material.BIRCH_LEAVES,
            Material.JUNGLE_LEAVES,
            Material.OAK_LEAVES,
            Material.DARK_OAK_LEAVES,
            Material.SPRUCE_LEAVES
    ));

    @Override
    public void onEnable() {
        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException e1) {
            e1.printStackTrace();
        }
        plugin = this;
        config = new Config();
        lang = new Lang();
        shop = new Shop();
        above_cooldowns = new HashMap<>();
        if(Bukkit.getPluginManager().getPlugin("WorldGuard") != null) {
            worldGuard = (WorldGuardPlugin) Bukkit.getPluginManager().getPlugin("WorldGuard");
        }
        initGUI();
        initDB();
        Bukkit.getPluginManager().registerEvents(new FixHandler(), this);
        Bukkit.getPluginManager().registerEvents(new BarHandler(), this);
        Bukkit.getPluginManager().registerEvents(new EventsHandler(), this);

        if(Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            getLogger().info("PlaceholderAPI detected");
            new PlaceHolders(this).hook();
        }

        if(!setupVault()) {
            getLogger().warning("Vault not connected");
        }

        getCommand("setyear").setExecutor(new SetYearCommand());
        getCommand("balance").setExecutor(new BalanceCommand());
        getCommand("balancetop").setExecutor(new BalanceTopCommand());
        getCommand("dbalance").setExecutor(new DBalanceCommand());
        getCommand("dbalancetop").setExecutor(new DBalanceTopCommand());
        getCommand("dmoney").setExecutor(new DMoneyCommand());
        getCommand("dpay").setExecutor(new DPayCommand());
        getCommand("money").setExecutor(new MoneyCommand());
        getCommand("pay").setExecutor(new PayCommand());
        getCommand("dshopadd").setExecutor(new DShopAddCommand());
        getCommand("donate").setExecutor(new DShopOpen());
        getCommand("nlreload").setExecutor(new ReloadCommand());
        getCommand("dlogs").setExecutor(new DLogsCommand());

        startThreads();

        for(Player p : Bukkit.getOnlinePlayers()){
            YearBossBar.addPlayer(p);
        }

        start();
    }

    public static Map<String, Long> getAboveCooldowns() {
        return above_cooldowns;
    }

    public static void send(Player p, String msg) {
        p.sendMessage(msg.replaceAll("&", "§"));
    }

    public static Main getPlugin() {
        return plugin;
    }

    public void initGUI() {
        gui = new GUI();
    }

    public GUI getGUI() {
        return gui;
    }

    public static DB getDB() {
        return db;
    }

    public FileConfiguration getConfig() {
        return config.getConfig();
    }

    public Config getConfigFile() {
        return config;
    }

    public FileConfiguration getLang() {
        return lang.getConfig();
    }

    public Lang getLangFile() {
        return lang;
    }

    public FileConfiguration getShop() {
        return shop.getConfig();
    }

    public Shop getShopFile() {
        return shop;
    }

    private boolean setupVault() {
        Plugin vault = getServer().getPluginManager().getPlugin("Vault");
        if (vault == null) {
            return false;
        }
        getServer().getServicesManager().register(Economy.class, new VaultHandler(this),
                this, ServicePriority.Normal);
        return true;
    }

    public static void stopScheduler(int id) {
        Bukkit.getScheduler().cancelTask(id);
    }

    public boolean canBreak(Player p, Location loc) {
        WorldGuardPlatform platform = WorldGuard.getInstance().getPlatform();
        RegionContainer container = platform.getRegionContainer();
        RegionManager regionManager = container.get(BukkitAdapter.adapt(loc.getWorld()));
        if(regionManager == null){
            return false;
        }
        ApplicableRegionSet set = regionManager.getApplicableRegions(BukkitAdapter.asBlockVector(loc));
        if(set.size() > 0) {
            for(ProtectedRegion region : set) {
                if(region.getType() == RegionType.GLOBAL) continue;
                if(!region.getOwners().contains(p.getUniqueId())
                        && !region.getMembers().contains(p.getUniqueId())) {
                    return false;
                }
            }
        }

        Town town = TownyAPI.getInstance().getTown(loc);
        if(town != null) {
            return town.hasResident(p);
        }

        return true;
    }

    public boolean atSpawn(Player p) {
        WorldGuardPlatform platform = WorldGuard.getInstance().getPlatform();
        RegionContainer container = platform.getRegionContainer();
        RegionManager regionManager = container.get(BukkitAdapter.adapt(p.getLocation().getWorld()));
        if(regionManager == null){
            return false;
        }
        ApplicableRegionSet set = regionManager.getApplicableRegions(BukkitAdapter.asBlockVector(p.getLocation()));
        if(set.size() > 0) {
            for(ProtectedRegion region : set) {
                if(region.getId().equals("spawn")) return true;
            }
        }
        return false;
    }

    public int getRows(int page) {
        int res = 6;
        try {
            res = getConfig().getInt("rows."+page);
        } catch (Exception e) {}
        return res;
    }

    public int getLeft(int page) {
        int res = 45;
        try {
            res = getConfig().getInt("rows."+page)*9 - 9;
        } catch (Exception e) {}
        return res;
    }

    public int getRight(int page) {
        int res = 53;
        try {
            res = getConfig().getInt("rows."+page)*9 - 1;
        } catch (Exception e) {}
        return res;
    }

    public void initDB() {
        String url = getConfig().getString("mysql.url");
        String user = getConfig().getString("mysql.user");
        String password = getConfig().getString("mysql.password");
        String wars_table = getConfig().getString("mysql.wars_table");
        String lights_table = getConfig().getString("mysql.lights_table");
        db = new DB(url, user, password, wars_table, lights_table);
    }

    public void startThreads(){
        new YearBossBar("NLWAR", BarColor.PURPLE, BarStyle.SEGMENTED_20).runTaskTimer(this,0,4);
    }

    public void start(){
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            if(TimeChecker.newDay()){
                YearBossBar.setYear(YearBossBar.getYear()+1);
            }
        }, 0, 20);
    }

    public boolean hasEmptySlot(Player p) {
        ItemStack[] cont = p.getInventory().getContents();
        for(int i = 0; i < 36; ++i) {
            if(cont[i] == null) return true;
        }
        return false;
    }

    public boolean hasItem(Inventory inv, Material mat, int amount) {
        int sum = 0;
        for(ItemStack it : inv.getContents()) {
            if(it != null) {
                if(it.getType() == mat) {
                    sum += it.getAmount();
                    if(sum >= amount) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void removeItem(Inventory inv, Material mat, int amount) {
        int sum = amount;
        int raw = 0;
        for(ItemStack it : inv.getContents()) {
            if(it != null) {
                if(it.getType() == mat) {
                    if(sum == 0) break;
                    if(it.getAmount() <= sum) {
                        inv.setItem(raw, null);
                        sum -= it.getAmount();
                    }
                    else {
                        it.setAmount(it.getAmount()-sum);
                        sum = 0;
                    }
                }
            }
            raw++;
        }
    }

    @Override
    public void onDisable() {
        for(Player p : Bukkit.getOnlinePlayers()){
            YearBossBar.removePlayer(p);
        }
    }
}
