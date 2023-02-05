package me.salatosik.blockguardplugin.listeners;

import me.salatosik.blockguardplugin.Vars;
import me.salatosik.blockguardplugin.commands.DisableAddingBlocksCommand;
import me.salatosik.blockguardplugin.util.GeneralDatabase;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import me.salatosik.blockguardplugin.util.PlayerBlock;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PlayerBlockInteractionListener implements Listener {
    public PlayerBlockInteractionListener(GeneralDatabase database, JavaPlugin plugin, DisableAddingBlocksCommand disableAddingBlocksCommand) {
        BukkitRunnable bukkitRunnable = new BukkitRunnable() {

            @Override
            public void run() {
                try {
                    database.addBlocks(playerBlocks);
                    playerBlocks.clear();

                    database.removePlayerBlocks(removedPlayerBlocks);
                    removedPlayerBlocks.clear();

                } catch (SQLException exception) {
                    exception.printStackTrace();
                }

            }
        };
        bukkitRunnable.runTaskTimer(plugin, 150, 150);

        try { database.putPlayerBlocks(allPlayerBlocks); } catch(SQLException exception) { exception.printStackTrace(); }
        this.PLAYERS_TURNED_OFF = disableAddingBlocksCommand.PLAYERS_TURNED_OFF;
    }

    private final List<PlayerBlock> playerBlocks = new ArrayList<>(), allPlayerBlocks = new ArrayList<>(), removedPlayerBlocks = new ArrayList<>();

    public List<PlayerBlock> getAllPlayerBlocks() {
        return allPlayerBlocks;
    }

    public List<PlayerBlock> getRemovedPlayerBlocks() {
        return removedPlayerBlocks;
    }

    public List<PlayerBlock> getPlayerBlocks() {
        return playerBlocks;
    }

    private boolean checkForRight(PlayerBlock playerBlock) {
        for(PlayerBlock block: allPlayerBlocks) if(block.equals(playerBlock)) return false;
        for(PlayerBlock block: allPlayerBlocks) if(block.equalsIgnoreUuid(playerBlock)) return true;
        return false;
    }

    @EventHandler
    public void onPlayerPlacedBlockIn(PlayerInteractEvent event) {
        if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && !Vars.verifyWorld(event.getClickedBlock().getWorld())) return;

        if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && !event.getClickedBlock().isEmpty()) {
            Block block = event.getClickedBlock();
            Player player = event.getPlayer();

            for(PlayerBlock playerBlock: allPlayerBlocks) {
                if(playerBlock.x == block.getX() & playerBlock.y == block.getY() & playerBlock.z == block.getZ() && !playerBlock.uuid.equals(player.getUniqueId().toString())) {
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.YELLOW + "You can't put a block here.");
                    return;
                }
            }
        }
    }

    private final List<String> PLAYERS_TURNED_OFF;

    @EventHandler
    public void onPlayerPlaceBlock(BlockPlaceEvent event) {
        if(!Vars.verifyWorld((event.getBlock().getWorld()))) return;

        if(!PLAYERS_TURNED_OFF.contains(event.getPlayer().getUniqueId().toString())) {
            PlayerBlock playerBlock = PlayerBlock.getPlayerBlockByBlockEvent(event, event.getPlayer().getUniqueId().toString());
            if(!PlayerBlock.search(playerBlock, playerBlocks)) playerBlocks.add(playerBlock);
            if(!PlayerBlock.search(playerBlock, allPlayerBlocks)) allPlayerBlocks.add(playerBlock);
        }
    }

    @EventHandler
    public void onPlayerBreaksEvent(BlockBreakEvent event) {
        if(!Vars.verifyWorld((event.getBlock().getWorld()))) return;

        PlayerBlock playerBlock = PlayerBlock.getPlayerBlockByBlockEvent(event, event.getPlayer().getUniqueId().toString());

        if(checkForRight(playerBlock)) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.YELLOW + "You can't hack this block.");

        } else {
            allPlayerBlocks.removeIf(pb -> pb.equals(playerBlock));
            playerBlocks.removeIf(pb -> pb.equals(playerBlock));
            removedPlayerBlocks.add(playerBlock);
        }
    }

    @EventHandler
    public void onPlayerWorldTeleported(PlayerChangedWorldEvent event) {
        if(Vars.verifyWorld((event.getFrom()))) {
            event.getPlayer().sendMessage(ChatColor.RED + "In this world, you cannot protect your blocks. Be careful.");
        }
    }

    private <E extends BlockPistonEvent> boolean protectBlock(List<Block> movedBlocks) {
        for(Block block: movedBlocks) {
            PlayerBlock playerBlock = new PlayerBlock(block.getX(), block.getY(), block.getZ(), null);
            if(PlayerBlock.searchIgnoreUuid(playerBlock, allPlayerBlocks)) return true;
        }

        return false;
    }

    @EventHandler
    public void onBlockPistonExtend(BlockPistonExtendEvent event) {
        if(!Vars.verifyWorld(event.getBlock().getWorld())) return;
        if(protectBlock(event.getBlocks())) event.setCancelled(true);
    }

    @EventHandler
    public void onBlockPistonRetract(BlockPistonRetractEvent event) {
        if(!Vars.verifyWorld(event.getBlock().getWorld())) return;
        if(protectBlock(event.getBlocks())) event.setCancelled(true);
    }

    @EventHandler
    public void onPhysicBlock(EntityChangeBlockEvent event) {
        if(!Vars.verifyWorld(event.getBlock().getWorld())) return;
        Block block = event.getBlock();
        PlayerBlock playerBlock = new PlayerBlock(block.getX(), block.getY(), block.getZ(), null);

        if(PlayerBlock.searchIgnoreUuid(playerBlock, allPlayerBlocks)) {
            removedPlayerBlocks.add(playerBlock);
            allPlayerBlocks.removeIf(b -> b.equalsIgnoreUuid(playerBlock));
            playerBlocks.removeIf(b -> b.equalsIgnoreUuid(playerBlock));
        }
    }
}
