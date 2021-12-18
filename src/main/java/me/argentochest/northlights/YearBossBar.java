package me.argentochest.northlights;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;

public class YearBossBar extends BukkitRunnable {

    public static int getYear() {
        return Main.getPlugin().getConfig().getInt("year");
    }

    public static void setYear(int year) {
        File file = new File(Main.getPlugin().getDataFolder() + File.separator + "config.yml");
        FileConfiguration data = YamlConfiguration.loadConfiguration(file);
        data.set("year", year);
        try {
            data.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Main.getPlugin().reloadConfig();
    }

    private static BossBar bar;
    private static String name;
    private static BarColor color;
    private static BarStyle style;

    public YearBossBar(String Name, BarColor Color, BarStyle Style) {
        name = Name;
        style = Style;
        color = Color;
        bar = Bukkit.createBossBar(name, color, style);
    }

    public static void addPlayer(Player player) {
        bar.addPlayer(player);
    }

    public static void removePlayer(Player player) {
        bar.removePlayer(player);
    }

    @Override
    public void run() {
        bar.setTitle("§fСейчас на дворе §e" + getYear() + "-й§f год");
        bar.setColor(BarColor.PURPLE);
        bar.setStyle(BarStyle.SEGMENTED_20);
        bar.setProgress(1);
    }

}

