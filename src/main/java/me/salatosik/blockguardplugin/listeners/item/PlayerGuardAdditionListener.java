package me.salatosik.blockguardplugin.listeners.item;

import me.salatosik.blockguardplugin.Main;
import me.salatosik.blockguardplugin.Vars;
import me.salatosik.blockguardplugin.core.Database;
import me.salatosik.blockguardplugin.core.LocalizationManager;
import me.salatosik.blockguardplugin.enums.MagicItem;
import me.salatosik.blockguardplugin.core.PlayerBlock;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerGuardAdditionListener implements Listener {
    private final Database database = Main.getDatabase();
    private final LocalizationManager LANG = Main.getLocalizationManager();

    @EventHandler
    public void onItemRightClick(PlayerInteractEvent event) {
        if(event.hasItem() && MagicItem.GUARD_ADDITION.equals(event.getItem()) & event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            event.setCancelled(true);
            Player player = event.getPlayer();

            if(!Vars.verifyWorld(event.getClickedBlock().getWorld())) {
                player.sendMessage(ChatColor.RED + LANG.getKey("general-lang.cannot-use-item"));
                return;
            }

            Block block = event.getClickedBlock();
            PlayerBlock playerBlock = new PlayerBlock(block.getX(), block.getY(), block.getZ(), event.getPlayer().getUniqueId().toString(), player.getWorld().getName(), block.getType().toString());

            if(PlayerBlock.searchIgnoreUuid(playerBlock, database.getPlayerBlocks())) {
                player.sendMessage(ChatColor.YELLOW + LANG.getKey("player-guard-addition-listener.already-belongs"));
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
            player.sendMessage(ChatColor.GREEN + LANG.getKey("player-guard-addition-listener.is-now-yours"));
        }
    }

    private boolean verifyMaxBlockWithProtection(int total, Player player) {
        if(total >= Vars.getMaximumProtectedBlocks()) {
            player.sendMessage(ChatColor.RED + LANG.getKey("player-guard-addition-listener.exceeded-block-limit").replace("[count]", Integer.toString(Vars.getMaximumProtectedBlocks())));
            return true;
        }

        return false;
    }
}
