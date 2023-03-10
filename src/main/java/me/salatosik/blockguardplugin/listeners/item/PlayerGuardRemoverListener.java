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

import java.util.List;

public class PlayerGuardRemoverListener implements Listener {
    private final Database database = Main.getDatabase();
    private final LocalizationManager LANG = Main.getLocalizationManager();

    @EventHandler
    public void onGuardRemoverInteraction(PlayerInteractEvent event) {
        if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK) & event.hasItem() && MagicItem.GUARD_REMOVER.equals(event.getItem())) {
            event.setCancelled(true);
            Player player = event.getPlayer();

            if(!Vars.verifyWorld(event.getClickedBlock().getWorld())) {
                player.sendMessage(ChatColor.RED + LANG.getKey("general-lang.cannot-use"));
                return;
            }

            Block block = event.getClickedBlock();
            PlayerBlock playerBlock = new PlayerBlock(block.getX(), block.getY(), block.getZ(), player.getUniqueId().toString(), player.getWorld().getName(), event.getClickedBlock().getType().toString());
            List<PlayerBlock> allPlayerBlocks = database.getPlayerBlocks();

            if(PlayerBlock.searchIgnoreUuid(playerBlock, allPlayerBlocks)) {
                for(PlayerBlock b: allPlayerBlocks) {
                    if(b.equals(playerBlock)) {
                        database.removePlayerBlock(playerBlock);
                        player.sendMessage(ChatColor.GREEN + LANG.getKey("player-guard-remover-listener.guard-removed"));
                        return;
                    }
                }

                player.sendMessage(ChatColor.RED + LANG.getKey("general-lang.already-belongs"));

            } else player.sendMessage(ChatColor.YELLOW + LANG.getKey("general-lang.does-not-belong"));
        }
    }
}
