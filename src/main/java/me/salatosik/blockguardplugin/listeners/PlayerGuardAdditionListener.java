package me.salatosik.blockguardplugin.listeners;

import me.salatosik.blockguardplugin.util.MagicItem;
import me.salatosik.blockguardplugin.util.PlayerBlock;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

public class PlayerGuardAdditionListener implements Listener {

    public PlayerGuardAdditionListener(List<PlayerBlock> allBlockPlayers, List<PlayerBlock> blockPlayers) {
        this.allBlockPlayers = allBlockPlayers;
        this.blockPlayers = blockPlayers;
    }

    private final List<PlayerBlock> allBlockPlayers, blockPlayers;

    @EventHandler
    public void onItemRightClick(PlayerInteractEvent event) {
        if(event.hasItem() && MagicItem.GUARD_ADDITION.equals(event.getItem())) {
            Block block = event.getClickedBlock();
            Player player = event.getPlayer();
            PlayerBlock playerBlock = new PlayerBlock(block.getX(), block.getY(), block.getZ(), event.getPlayer().getUniqueId().toString());

            if(PlayerBlock.searchIgnoreUuid(playerBlock, allBlockPlayers)) {
                player.sendMessage(ChatColor.YELLOW + "This block already belongs to someone.");
                return;
            }

            allBlockPlayers.add(playerBlock);
            blockPlayers.add(playerBlock);
            player.sendMessage(ChatColor.GREEN + "This block is now yours!");
        }
    }
}
