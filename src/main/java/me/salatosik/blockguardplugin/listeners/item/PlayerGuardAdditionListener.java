package me.salatosik.blockguardplugin.listeners.item;

import me.salatosik.blockguardplugin.Vars;
import me.salatosik.blockguardplugin.core.Database;
import me.salatosik.blockguardplugin.enums.MagicItem;
import me.salatosik.blockguardplugin.core.PlayerBlock;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerGuardAdditionListener implements Listener {

    public PlayerGuardAdditionListener(Database database) {
        this.database = database;
    }

    private final Database database;

    @EventHandler
    public void onItemRightClick(PlayerInteractEvent event) {
        if(event.hasItem() && MagicItem.GUARD_ADDITION.equals(event.getItem())) {
            event.setCancelled(true);
            Player player = event.getPlayer();

            if(!Vars.verifyWorld(event.getClickedBlock().getWorld())) {
                player.sendMessage(ChatColor.RED + "You cannot use this item in this world.");
                return;
            }

            Block block = event.getClickedBlock();
            PlayerBlock playerBlock = new PlayerBlock(block.getX(), block.getY(), block.getZ(), event.getPlayer().getUniqueId().toString(), player.getWorld().getName(), block.getType().toString());

            if(PlayerBlock.searchIgnoreUuid(playerBlock, database.getPlayerBlocks())) {
                player.sendMessage(ChatColor.YELLOW + "This block already belongs to someone.");
                return;
            }

            int totalBlocksWithProtection = 0;
            for(PlayerBlock pb: database.getPlayerBlocks()) {
                if(pb.uuid.equals(player.getUniqueId().toString())) {
                    totalBlocksWithProtection++;

                    if(verifyMaxBlockWithProtection(totalBlocksWithProtection, player)) return;
                }
            }

            if(verifyMaxBlockWithProtection(totalBlocksWithProtection, player)) return;

            database.addPlayerBlock(playerBlock);
            player.sendMessage(ChatColor.GREEN + "This block is now yours!");
        }
    }

    private boolean verifyMaxBlockWithProtection(int total, Player player) {
        if(total >= Vars.getMaximumProtectedBlocks()) {
            player.sendMessage(ChatColor.RED + "You have exceeded the limit!" + ChatColor.GREEN + " Maximum number of blocks with protection: " + Vars.getMaximumProtectedBlocks());
            return true;
        }

        return false;
    }
}
