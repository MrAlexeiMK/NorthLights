package me.argentochest.northlights;

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

public class Main extends JavaPlugin {
    private static Main plugin;
    private static DB db;
    private static Config config;
    private static Lang lang;
    private static Shop shop;
    private static GUI gui;
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";

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
        gui = new GUI();
        initDB();
        Bukkit.getPluginManager().registerEvents(new FixHandler(), this);
        Bukkit.getPluginManager().registerEvents(new BarHandler(), this);
        Bukkit.getPluginManager().registerEvents(new EventsHandler(), this);

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
        getCommand("dshopopen").setExecutor(new DShopOpen());

        startThreads();

        for(Player p : Bukkit.getOnlinePlayers()){
            YearBossBar.addPlayer(p);
        }

        start();
    }

    public static void send(Player p, String msg) {
        p.sendMessage(msg.replaceAll("&", "§"));
    }

    public static Main getPlugin() {
        return plugin;
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

    public void initDB() {
        String url = getConfig().getString("mysql.url");
        String user = getConfig().getString("mysql.user");
        String password = getConfig().getString("mysql.password");
        db = new DB(url, user, password);
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
        config.accept();
        lang.accept();
        shop.accept();
        for(Player p : Bukkit.getOnlinePlayers()){
            YearBossBar.removePlayer(p);
        }
    }
}
