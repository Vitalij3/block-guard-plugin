package me.salatosik.blockguardplugin.listeners.item;

import me.salatosik.blockguardplugin.Main;
import me.salatosik.blockguardplugin.Vars;
import me.salatosik.blockguardplugin.core.Database;
import me.salatosik.blockguardplugin.core.LocalizationManager;
import me.salatosik.blockguardplugin.enums.MagicItem;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;
import me.salatosik.blockguardplugin.core.PlayerBlock;

import java.util.UUID;

public class PlayerMagicStickListener implements Listener {
    private final JavaPlugin plugin = JavaPlugin.getProvidingPlugin(Main.class);
    private final Database database = Main.getDatabase();
    private final LocalizationManager LANG = Main.getLocalizationManager();


    @EventHandler()
    public void onMagicStickUsed(PlayerInteractEvent event) {
        if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK) & event.hasItem() && MagicItem.BLOCK_PICKER.equals(event.getItem())) {
            event.setCancelled(true);
            Player eventPlayer = event.getPlayer();

            if(!Vars.verifyWorld(event.getClickedBlock().getWorld())) {
                eventPlayer.sendMessage(ChatColor.RED + LANG.getKey("general-lang.cannot-use-item"));
                return;
            }

            Block block = event.getClickedBlock();

            for(PlayerBlock playerBlock: database.getPlayerBlocks()) {
                if(playerBlock.equalsIgnoreUuid(new PlayerBlock(block.getX(), block.getY(), block.getZ(), null, eventPlayer.getWorld().getName(), block.getType().toString()))) {
                    OfflinePlayer player = plugin.getServer().getOfflinePlayer(UUID.fromString(playerBlock.uuid));

                    if(player != null) {
                        String message = "\n" + ChatColor.YELLOW + LANG.getKey("player-magic-stick-listener.player-information-stages.block-player") + ": " + ChatColor.GREEN + player.getName() + "\n" +
                                ChatColor.YELLOW + LANG.getKey("player-magic-stick-listener.player-information-stages.network-status") + ": " + getNetworkStatusString(player) + "\n" +
                                ChatColor.YELLOW + LANG.getKey("player-magic-stick-listener.player-information-stages.block-coordinates") + ": " + ChatColor.GREEN + playerBlock.x + ", " + playerBlock.y + ", " + playerBlock.z + "\n\n";

                        event.getPlayer().sendMessage(message);

                    } else event.getPlayer().sendMessage(ChatColor.RED + LANG.getKey("player-magic-stick-listener.information-not-found"));

                    return;
                }
            }

            event.getPlayer().sendMessage(ChatColor.GREEN + LANG.getKey("general-lang.does-not-belong"));
        }
    }

    private String getNetworkStatusString(OfflinePlayer offlinePlayer) {
        if(offlinePlayer.isOnline()) return ChatColor.GREEN + LANG.getKey("general-lang.network-states.online").toUpperCase();
        else return ChatColor.RED + LANG.getKey("general-lang.network-states.offline").toUpperCase();
    }
}
