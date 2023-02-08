package me.salatosik.blockguardplugin.listeners;

import me.salatosik.blockguardplugin.Vars;
import me.salatosik.blockguardplugin.util.GeneralDatabase;
import me.salatosik.blockguardplugin.util.MagicItem;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;
import me.salatosik.blockguardplugin.util.PlayerBlock;

import java.util.UUID;

public class PlayerMagicStickListener implements Listener {
    private final JavaPlugin plugin;
    private final GeneralDatabase database;

    public PlayerMagicStickListener(GeneralDatabase database, JavaPlugin plugin) {
        this.plugin = plugin;
        this.database = database;
    }

    @EventHandler()
    public void onMagicStickUsed(PlayerInteractEvent event) {
        if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK) & event.hasItem() && MagicItem.BLOCK_PICKER.equals(event.getItem())) {
            event.setCancelled(true);
            Player eventPlayer = event.getPlayer();

            if(!Vars.verifyWorld(event.getClickedBlock().getWorld())) {
                eventPlayer.sendMessage(ChatColor.RED + "You cannot use this item in this world.");
                return;
            }

            Block block = event.getClickedBlock();

            for(PlayerBlock playerBlock: database.getPlayerBlocks()) {
                if(playerBlock.equalsIgnoreUuid(new PlayerBlock(block.getX(), block.getY(), block.getZ(), null, eventPlayer.getWorld().getName(), block.getType().toString()))) {
                    OfflinePlayer player = plugin.getServer().getOfflinePlayer(UUID.fromString(playerBlock.uuid));

                    if(player != null) {
                        String message = "\n" + ChatColor.YELLOW + "Block player: " + ChatColor.GREEN + player.getName() + "\n" +
                                ChatColor.YELLOW + "Network Status: " + getNetworkStatusString(player) + "\n" +
                                ChatColor.YELLOW + "Block coordinates: " + ChatColor.GREEN + playerBlock.x + ", " + playerBlock.y + ", " + playerBlock.z + "\n\n";

                        event.getPlayer().sendMessage(message);

                    } else event.getPlayer().sendMessage(ChatColor.RED + "[ERROR]" + ChatColor.YELLOW + "Information not found");

                    return;
                }
            }

            event.getPlayer().sendMessage(ChatColor.GREEN + "This block does not belong to anyone");
        }
    }

    private String getNetworkStatusString(OfflinePlayer offlinePlayer) {
        if(offlinePlayer.isOnline()) return ChatColor.GREEN + "ONLINE";
        else return ChatColor.RED + "OFFLINE";
    }
}
