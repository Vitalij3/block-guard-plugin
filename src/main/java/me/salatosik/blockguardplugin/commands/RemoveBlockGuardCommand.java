package me.salatosik.blockguardplugin.commands;

import me.salatosik.blockguardplugin.Main;
import me.salatosik.blockguardplugin.core.Database;
import me.salatosik.blockguardplugin.core.PlayerBlock;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class RemoveBlockGuardCommand implements CommandExecutor {
    private final Database database;

    public RemoveBlockGuardCommand() {
        this.database = Main.getDatabase();
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(commandSender instanceof Player & strings.length == 3) {
            Player player = (Player) commandSender;
            List<PlayerBlock> playerBlocks = database.getPlayerBlocks();
            int x, y, z;

            try {
                x = Integer.parseInt(strings[0]);
                y = Integer.parseInt(strings[1]);
                z = Integer.parseInt(strings[2]);

            } catch(NumberFormatException numberFormatException) {
                player.sendMessage(ChatColor.RED + "Please enter the numbers.");
                return true;
            }

            for(PlayerBlock pb: playerBlocks) {
                if(pb.x == x & pb.y == y & pb.z == z & pb.worldName.equals(player.getWorld().getName())) {
                    if(pb.uuid.equals(player.getUniqueId().toString())) {
                        database.removePlayerBlock(pb);
                        player.sendMessage(ChatColor.GREEN + "Block protection removed!");

                    } else player.sendMessage(ChatColor.RED + "You are not the owner of this block and therefore do not have the right to do so.");

                    return true;
                }
            }

            player.sendMessage(ChatColor.YELLOW + "The block not found.");
        }

        return true;
    }
}
