package me.salatosik.blockguardplugin.commands;

import me.salatosik.blockguardplugin.core.Database;
import me.salatosik.blockguardplugin.core.PlayerBlock;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class MyBlocksCommand implements CommandExecutor {
    private final Database database;

    public MyBlocksCommand(Database database) {
        this.database = database;
    }

    private static final String BLOCK_TEXT_EXAMPLE = String.valueOf(ChatColor.GOLD) + ChatColor.BOLD + "[num]. " + ChatColor.YELLOW + ChatColor.ITALIC + "[block]" + ", [[x], [y], [z]]";

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(commandSender instanceof Player) {
            Player player = (Player) commandSender;
            List<PlayerBlock> playerBlocks = database.getPlayerBlocks();

            if(playerBlocks.size() != 0) {
                List<PlayerBlock> currentPlayerBlock = new ArrayList<>();

                for(PlayerBlock pb: playerBlocks) {
                    if(pb.uuid.equals(player.getUniqueId().toString()) & pb.worldName.equals(player.getWorld().getName())) {
                        currentPlayerBlock.add(pb);
                    }
                }

                if(currentPlayerBlock.size() != 0) {
                    int count = 1;
                    player.sendMessage("\n");

                    for(PlayerBlock pb: currentPlayerBlock) {
                        TextComponent textComponent = new TextComponent();
                        textComponent.addExtra(BLOCK_TEXT_EXAMPLE.replace("[block]", pb.blockName).replace("[num]", Integer.toString(count)).replace("[x]", Integer.toString(pb.x)).replace("[y]", Integer.toString(pb.y)).replace("[z]", Integer.toString(pb.z)));
                        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/remove-block-guard [x] [y] [z]".replace("[x]", String.valueOf(pb.x)).replace("[y]", String.valueOf(pb.y)).replace("[z]", String.valueOf(pb.z))));

                        count++;

                        player.spigot().sendMessage(textComponent);
                    }

                    player.sendMessage(ChatColor.GREEN + "Click for remove guard.\n");

                } else player.sendMessage(ChatColor.YELLOW + "You don't have secure blocks yet.");

            } else player.sendMessage(ChatColor.YELLOW + "The database with blocks is empty.");
        }

        return true;
    }
}
