package me.argentochest.northlights;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;

public class VaultHandler implements Economy, Listener {
    private Main plugin;

    public VaultHandler(Main plugin) {
        this.plugin = plugin;
        Bukkit.getServer().getPluginManager().registerEvents((Listener) this, plugin);
        plugin.getLogger().info("Vault support enabled");
    }

    @Override
    public EconomyResponse bankBalance(String name) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Не поддерживается");
    }

    @Override
    public EconomyResponse bankDeposit(String arg0, double arg1) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Не поддерживается");
    }

    @Override
    public EconomyResponse bankHas(String arg0, double arg1) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Не поддерживается");
    }

    @Override
    public EconomyResponse bankWithdraw(String arg0, double arg1) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Не поддерживается");
    }

    @Override
    public EconomyResponse createBank(String arg0, String arg1) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Не поддерживается");
    }

    @Override
    public EconomyResponse createBank(String arg0, OfflinePlayer arg1) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Не поддерживается");
    }

    @Override
    public boolean createPlayerAccount(String name) {
        return false;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer arg0) {
        return false;
    }

    @Override
    public boolean createPlayerAccount(String arg0, String arg1) {
        return false;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer arg0, String arg1) {
        return false;
    }

    @Override
    public String currencyNamePlural() {
        return null;
    }

    @Override
    public String currencyNameSingular() {
        return null;
    }

    @Override
    public EconomyResponse deleteBank(String arg0) {
        return null;
    }

    @Override
    public EconomyResponse depositPlayer(String name, double count) {
        return Main.getDB().addWars(name, count);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer p, double count) {
        return Main.getDB().addWars(p.getName(), count);
    }

    @Override
    public EconomyResponse depositPlayer(String name, String world, double count) {
        return Main.getDB().addWars(name, count);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer p, String arg1, double count) {
        return Main.getDB().addWars(p.getName(), count);
    }

    @Override
    public String format(double arg0) {
        return null;
    }

    @Override
    public int fractionalDigits() {
        return 0;
    }

    @Override
    public double getBalance(String arg0) {
        return Main.getDB().getWars(arg0);
    }

    @Override
    public double getBalance(OfflinePlayer arg0) {
        return Main.getDB().getWars(arg0.getName());
    }

    @Override
    public double getBalance(String arg0, String arg1) {
        return Main.getDB().getWars(arg0);
    }

    @Override
    public double getBalance(OfflinePlayer arg0, String arg1) {
        return Main.getDB().getWars(arg0.getName());
    }

    @Override
    public List<String> getBanks() {
        return null;
    }

    @Override
    public String getName() {
        return "NorthLights";
    }

    @Override
    public boolean has(String arg0, double arg1) {
        return Main.getDB().getWars(arg0) >= arg1;
    }

    @Override
    public boolean has(OfflinePlayer arg0, double arg1) {
        return Main.getDB().getWars(arg0.getName()) >= arg1;
    }

    @Override
    public boolean has(String arg0, String arg1, double arg2) {
        return Main.getDB().getWars(arg0) >= arg2;
    }

    @Override
    public boolean has(OfflinePlayer arg0, String arg1, double arg2) {
        return Main.getDB().getWars(arg0.getName()) >= arg2;
    }

    @Override
    public boolean hasAccount(String arg0) {
        return Main.getDB().isPlayerInDB(arg0);
    }

    @Override
    public boolean hasAccount(OfflinePlayer arg0) {
        return Main.getDB().isPlayerInDB(arg0.getName());
    }

    @Override
    public boolean hasAccount(String arg0, String arg1) {
        return Main.getDB().isPlayerInDB(arg0);
    }

    @Override
    public boolean hasAccount(OfflinePlayer arg0, String arg1) {
        return Main.getDB().isPlayerInDB(arg0.getName());
    }

    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public EconomyResponse isBankMember(String arg0, String arg1) {
        return null;
    }

    @Override
    public EconomyResponse isBankMember(String arg0, OfflinePlayer arg1) {
        return null;
    }

    @Override
    public EconomyResponse isBankOwner(String arg0, String arg1) {
        return null;
    }

    @Override
    public EconomyResponse isBankOwner(String arg0, OfflinePlayer arg1) {
        return null;
    }

    @Override
    public boolean isEnabled() {
        return plugin != null;
    }

    @Override
    public EconomyResponse withdrawPlayer(String arg0, double arg1) {
        return Main.getDB().removeWars(arg0, arg1);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer arg0, double arg1) {
        return Main.getDB().removeWars(arg0.getName(), arg1);
    }

    @Override
    public EconomyResponse withdrawPlayer(String arg0, String arg1, double arg2) {
        return Main.getDB().removeWars(arg0, arg2);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer arg0, String arg1, double arg2) {
        return Main.getDB().removeWars(arg0.getName(), arg2);
    }

    public class EconomyServerListener implements Listener {
        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginEnable(PluginEnableEvent event) {
            if (plugin == null) {
                Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("NorthLights");

                if (plugin != null && plugin.isEnabled()) {
                    VaultHandler.this.plugin = (Main) plugin;

                    VaultHandler.this.plugin.getLogger().info("Vault support enabled");
                }
            }
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginDisable(PluginDisableEvent event) {
            if (plugin != null) {
                if (event.getPlugin().getDescription().getName().equals("NorthLights")) {
                    plugin = null;

                    Bukkit.getLogger().info("Vault support disabled");
                }
            }
        }
    }
}
