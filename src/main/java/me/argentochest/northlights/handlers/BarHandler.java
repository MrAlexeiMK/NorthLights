package me.argentochest.northlights.handlers;

import me.argentochest.northlights.YearBossBar;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class BarHandler implements Listener {

    @EventHandler
    public void join(PlayerJoinEvent e){
        YearBossBar.addPlayer(e.getPlayer());
    }

    @EventHandler
    public void quit(PlayerQuitEvent e){
        YearBossBar.removePlayer(e.getPlayer());
    }

}
