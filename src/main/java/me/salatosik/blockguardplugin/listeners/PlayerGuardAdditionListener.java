package me.salatosik.blockguardplugin.listeners;

import me.salatosik.blockguardplugin.Vars;
import me.salatosik.blockguardplugin.util.GeneralDatabase;
import me.salatosik.blockguardplugin.util.MagicItem;
import me.salatosik.blockguardplugin.util.PlayerBlock;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerGuardAdditionListener implements Listener {

    public PlayerGuardAdditionListener(GeneralDatabase database) {
        this.database = database;
    }

    private final GeneralDatabase database;

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

            database.addPlayerBlock(playerBlock);
            player.sendMessage(ChatColor.GREEN + "This block is now yours!");
        }
    }
}
