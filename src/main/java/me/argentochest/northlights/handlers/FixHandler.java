package me.argentochest.northlights.handlers;

import net.md_5.bungee.api.chat.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
import org.bukkit.event.entity.VillagerReplenishTradeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

import java.util.ArrayList;
import java.util.List;

public class FixHandler implements Listener {

    @EventHandler
    public void piston(PlayerMoveEvent e){
        for(Entity entity : e.getPlayer().getLocation().getNearbyEntities( 10, 10, 10)){
            if(entity.getType() == EntityType.VILLAGER || entity.getType() == EntityType.ZOMBIE_VILLAGER){
                entity.remove();
            }
        }
    }

    @EventHandler
    public void piston(EntitySpawnEvent e){
        if(e.getEntityType() == EntityType.VILLAGER || e.getEntityType() == EntityType.ZOMBIE_VILLAGER){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void piston(BlockPistonExtendEvent e){
        boolean b = false;
        for(Block block : e.getBlocks()){
            if(block.getType().name().toLowerCase().contains("ore") || e.getBlock().getType().name().toLowerCase().contains(Material.SHULKER_BOX.name())){
                b = true;
            }
        }
        if(b){
            e.setCancelled(true);
            List<String> sus = new ArrayList<>();
            for(Player player : e.getBlock().getLocation().getNearbyEntitiesByType(Player.class, 15)){
                sus.add(player.getName());
                player.sendTitle("§cВНИМАНИЕ", "§fКто-то двигает запрещённый предмет");
                player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1, 1);
                player.sendMessage("§cЕсли вы двигаете руду, то остановитесь. В противном случае вы получите бан");
            }

            Location l = e.getBlock().getLocation();

            for(Player player : Bukkit.getOnlinePlayers()){
                if(player.hasPermission("antihack.notify")){
                    player.sendMessage("Кто-то пытается сдвинуть запрещённый блок");
                    player.sendMessage("Подозреваемые: §a"+String.join("§7, §a", sus));
                    BaseComponent bc = new ComponentBuilder("§a§lНАЖМИТЕ ЧТОБЫ ТЕЛЕПОРТИРОВАТЬСЯ").create()[0];
                    bc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§fНажмите чтобы тп").create()));
                    bc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp "+((int)l.getX())+" "+((int)l.getY())+" "+((int)l.getZ())));
                    player.sendMessage(bc);

                }
            }


        }
    }

}
