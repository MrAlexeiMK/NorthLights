package me.argentochest.northlights.commands;

import lombok.var;
import me.argentochest.northlights.Main;
import me.argentochest.northlights.other.Pair;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class BalanceTopCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            if(p.hasPermission("nl.balancetop")) {
                int rows = Main.getPlugin().getConfig().getInt("balance_top_rows");
                List<Pair<String, Double>> top = Main.getDB().getTop("wars", rows, true);
                int i = 1;
                for(var pair : top) {
                    Main.send(p, "&f"+i+". &7"+pair.getFirst() + " &f(&e"+pair.getSecond()+"&f)");
                    ++i;
                }
            }
            else {
                Main.send(p, Main.getPlugin().getLang().getString("no_pex"));
            }
        }
        return false;
    }
}
