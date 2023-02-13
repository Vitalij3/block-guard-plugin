package me.salatosik.blockguardplugin.commands.tab;

import me.salatosik.blockguardplugin.core.Database;
import me.salatosik.blockguardplugin.core.PlayerBlock;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class RemoveBlockGuardCompleter implements TabCompleter {
    private final Database database;

    public RemoveBlockGuardCompleter(Database database) {
        this.database = database;
    }

    private List<String> getCoordinates(Player player, int index) {
        List<String> coordinates = new ArrayList<>();

        for(PlayerBlock pb: database.getPlayerBlocks()) {
            if(pb.uuid.equals(player.getUniqueId().toString()) & pb.worldName.equals(player.getWorld().getName())) {
                switch(index) {
                    case 1:
                        coordinates.add(Integer.toString(pb.x));
                        break;

                    case 2:
                        coordinates.add(Integer.toString(pb.y));
                        break;

                    case 3:
                        coordinates.add(Integer.toString(pb.z));
                        break;
                }
            }
        }

        if(coordinates.size() == 0) coordinates.add("not-found");

        return coordinates;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        if(commandSender instanceof Player) {
            Player player = (Player) commandSender;
            return getCoordinates(player, strings.length);
        }

        return null;
    }
}
