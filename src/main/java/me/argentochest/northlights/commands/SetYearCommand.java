package me.argentochest.northlights.commands;

import me.argentochest.northlights.YearBossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class SetYearCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender.hasPermission("nl.setyear")){
            if(args.length < 1){
                sender.sendMessage("Введите год");
                return false;
            }
            int year;
            try {
                year = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                sender.sendMessage("§cЭто не число");
                return false;
            }
            YearBossBar.setYear(year);
            sender.sendMessage("§fВы установили §e"+year+"й §fгод");
        }
        return false;
    }
}
