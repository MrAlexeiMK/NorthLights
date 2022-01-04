package me.argentochest.northlights.commands;

import me.argentochest.northlights.Main;
import me.argentochest.northlights.other.Loc;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BlockTPCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            if(p.hasPermission("nl.blocktp")) {
                if(args.length < 6) {
                    Main.send(p, "&c/blocktp [x1] [y1] [z1] [x2] [y2] [z2] (yaw) (pitch)");
                }
                else {
                    try {
                        World w = p.getWorld();
                        Location l1 = new Location(w, Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]));
                        Location l2 = new Location(w, Integer.parseInt(args[3]), Integer.parseInt(args[4]), Integer.parseInt(args[5]));
                        if(args.length >= 8) {
                            l2.setYaw(Float.parseFloat(args[6]));
                            l2.setPitch(Float.parseFloat(args[7]));
                        }
                        else {
                            l2.setYaw(p.getLocation().getYaw());
                            l2.setPitch(p.getLocation().getPitch());
                        }
                        Loc loc1 = new Loc(l1);
                        Loc loc2 = new Loc(l2);
                        List<String> list = new ArrayList<>();
                        if(Main.getPlugin().getTeleports().contains("list")) {
                            list = (List<String>) Main.getPlugin().getTeleports().get("list");
                        }
                        String res = loc1+"|"+loc2;
                        list.add(res);
                        Main.getPlugin().getTeleports().set("list", list);
                        Main.getPlugin().getTeleportsFile().accept();
                        Main.getPlugin().initTeleports();
                        Main.send(p, "&aУспешно добавлено!");

                    } catch (Exception ex) {
                        Main.send(p, "&c/blocktp [x1] [y1] [z1] [x2] [y2] [z2] (yaw) (pitch)");
                    }
                }
            }
            else {
                Main.send(p, Main.getPlugin().getLang().getString("no_pex"));
            }
        }
        return false;
    }
}
