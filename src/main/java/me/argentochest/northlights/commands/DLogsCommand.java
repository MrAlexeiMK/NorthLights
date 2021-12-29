package me.argentochest.northlights.commands;

import lombok.var;
import me.argentochest.northlights.Main;
import me.argentochest.northlights.other.Pair;
import me.argentochest.northlights.other.Triple;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class DLogsCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            if(p.hasPermission("nl.dlogs")) {
                if(args.length >= 1) {
                    try {
                        int count = Integer.parseInt(args[0]);
                        List<Triple<String, Double, String>> rows = Main.getDB().getLastLogs(count);
                        for(Triple<String, Double, String> row : rows) {
                            Main.send(p, row.getFirst() + ", " + row.getSecond() + ", " + row.getThird());
                        }
                    } catch (Exception ex) {}
                }
                else {
                    Main.send(p, "&7/dlogs [количество]");
                }
            }
            else {
                Main.send(p, Main.getPlugin().getLang().getString("no_pex"));
            }
        }
        return false;
    }
}
