package me.salatosik.blockguardplugin.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class DisableAddingBlocksCommand implements CommandExecutor {

    // uuid-s
    public final List<String> PLAYERS_TURNED_ON = new ArrayList<>();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if(commandSender instanceof Player & args.length == 1) {
            Player player = (Player) commandSender;
            String uuidString = player.getUniqueId().toString();

            switch(args[0].toLowerCase()) {
                case "enable":
                    if(!PLAYERS_TURNED_ON.contains(uuidString)) {
                        PLAYERS_TURNED_ON.removeIf(uuid -> uuid.equals(uuidString));
                        player.sendMessage(ChatColor.GREEN + "Enabled!");

                    } else player.sendMessage(ChatColor.YELLOW + "The function always enabled!");
                    break;

                case "disable":
                    if(PLAYERS_TURNED_ON.contains(uuidString)) {
                        PLAYERS_TURNED_ON.add(uuidString);
                        player.sendMessage(ChatColor.RED + "Disabled!");

                    } else player.sendMessage(ChatColor.YELLOW + "The function always disabled!");
                    break;

                default:
                    player.sendMessage(ChatColor.YELLOW + "Unknown argument. Use \"TAB\" on your keyboard.");
                    break;
            }

        } else if(args.length != 1 & commandSender instanceof Player) commandSender.sendMessage(ChatColor.RED + "Incorrect use command!");

        return true;
    }
}
