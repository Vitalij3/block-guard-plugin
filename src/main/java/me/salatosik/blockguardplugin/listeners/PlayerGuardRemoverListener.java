package me.salatosik.blockguardplugin.listeners;

import me.salatosik.blockguardplugin.util.MagicItem;
import me.salatosik.blockguardplugin.util.PlayerBlock;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

public class PlayerGuardRemoverListener implements Listener {
    private final List<PlayerBlock> allPlayerBlocks, removePlayerBlocks;

    public PlayerGuardRemoverListener(List<PlayerBlock> allPlayerBlocks, List<PlayerBlock> removePlayerBlocks) {
        this.allPlayerBlocks = allPlayerBlocks;
        this.removePlayerBlocks = removePlayerBlocks;
    }

    private boolean searchPlayerBlock(Block block) {
        for(PlayerBlock playerBlock: allPlayerBlocks) {
            if(playerBlock.x == block.getX() & playerBlock.y == block.getY() & playerBlock.z == block.getZ()) {
                return true;
            }
        }

        return false;
    }

    @EventHandler
    public void onGuardRemoverInteraction(PlayerInteractEvent event) {
        if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK) & event.hasItem() && MagicItem.GUARD_REMOVER.equals(event.getItem())) {
            Player player = event.getPlayer();
            Block block = event.getClickedBlock();

            if(searchPlayerBlock(block)) {
                PlayerBlock playerBlock = new PlayerBlock(block.getX(), block.getY(), block.getZ(), player.getUniqueId().toString());

                for(PlayerBlock b: allPlayerBlocks) {
                    if(b.equals(playerBlock)) {
                        allPlayerBlocks.removeIf(pb -> pb.equals(b));
                        removePlayerBlocks.add(playerBlock);
                        player.sendMessage(ChatColor.GREEN + "Block guard removed!");
                        return;
                    }
                }

                player.sendMessage(ChatColor.RED + "This block belongs to another player!");

            } else player.sendMessage(ChatColor.YELLOW + "This block does not belong to anyone.");
        }
    }
}
