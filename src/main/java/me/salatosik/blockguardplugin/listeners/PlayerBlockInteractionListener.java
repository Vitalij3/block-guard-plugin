package me.salatosik.blockguardplugin.listeners;

import me.salatosik.blockguardplugin.Vars;
import me.salatosik.blockguardplugin.commands.DisableAddingBlocksCommand;
import me.salatosik.blockguardplugin.util.GeneralDatabase;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import me.salatosik.blockguardplugin.util.PlayerBlock;

import java.util.List;

public class PlayerBlockInteractionListener implements Listener {
    public PlayerBlockInteractionListener(GeneralDatabase database, JavaPlugin plugin, DisableAddingBlocksCommand disableAddingBlocksCommand) {
        this.PLAYERS_TURNED_OFF = disableAddingBlocksCommand.PLAYERS_TURNED_ON;
        this.plugin = plugin;
        this.database = database;
    }

    private final GeneralDatabase database;

    private boolean checkForRight(PlayerBlock playerBlock) {
        List<PlayerBlock> playerBlocks = database.getPlayerBlocks();
        for(PlayerBlock block: playerBlocks) if(block.equals(playerBlock)) return false;
        for(PlayerBlock block: playerBlocks) if(block.equalsIgnoreUuid(playerBlock)) return true;
        return false;
    }

    @EventHandler
    public void onPlayerPlacedBlockIn(PlayerInteractEvent event) {
        if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && !Vars.verifyWorld(event.getClickedBlock().getWorld())) return;

        if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && !event.getClickedBlock().isEmpty()) {
            Block block = event.getClickedBlock();
            Player player = event.getPlayer();

            for(PlayerBlock playerBlock: database.getPlayerBlocks()) {
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
        if(event.getBlock().getType().equals(Material.FIRE)) return;

        if(PLAYERS_TURNED_OFF.contains(event.getPlayer().getUniqueId().toString())) {
            PlayerBlock playerBlock = PlayerBlock.getPlayerBlockByBlockEvent(event, event.getPlayer().getUniqueId().toString());
            if(!PlayerBlock.search(playerBlock, database.getPlayerBlocks())) database.addPlayerBlock(playerBlock);
        }
    }

    @EventHandler
    public void onPlayerBreaksEvent(BlockBreakEvent event) {
        if(!Vars.verifyWorld((event.getBlock().getWorld()))) return;

        PlayerBlock playerBlock = PlayerBlock.getPlayerBlockByBlockEvent(event, event.getPlayer().getUniqueId().toString());

        if(checkForRight(playerBlock)) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.YELLOW + "You can't hack this block.");

        } else database.removePlayerBlock(playerBlock);
    }

    @EventHandler
    public void onPlayerWorldTeleported(PlayerChangedWorldEvent event) {
        if(Vars.verifyWorld((event.getFrom()))) {
            event.getPlayer().sendMessage(ChatColor.RED + "In this world, you cannot protect your blocks. Be careful.");
        }
    }

    private boolean protectBlock(List<Block> movedBlocks) {
        for(Block block: movedBlocks) {
            PlayerBlock playerBlock = new PlayerBlock(block.getX(), block.getY(), block.getZ(), null);
            if(PlayerBlock.searchIgnoreUuid(playerBlock, database.getPlayerBlocks())) return true;
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

        if(PlayerBlock.searchIgnoreUuid(playerBlock, database.getPlayerBlocks())) {
            database.removePlayerBlock(playerBlock);
        }
    }

    @EventHandler
    public void onBlockBurning(BlockBurnEvent event) {
        if(!Vars.verifyWorld(event.getBlock().getWorld())) return;
        Block block = event.getBlock();
        PlayerBlock playerBlock = new PlayerBlock(block.getX(), block.getY(), block.getZ(), null);
        if(PlayerBlock.searchIgnoreUuid(playerBlock, database.getPlayerBlocks())) event.setCancelled(true);
    }

    private final Plugin plugin;

    private static final Material[] DESTRUCTIVE_FORCES = {
            Material.STONE_BUTTON,
            Material.WOOD_BUTTON,
            Material.LEVER,
            Material.GOLD_PLATE,
            Material.IRON_PLATE,
            Material.STONE_PLATE,
            Material.WOOD_PLATE
    };

    private static final Material[] DESTRUCTIVE = {
            Material.WATER,
            Material.STATIONARY_WATER,
            Material.LAVA,
            Material.STATIONARY_LAVA,
            Material.AIR
    };

    @EventHandler
    public void onBlockFromTo(BlockFromToEvent event) {
        Material blockType = event.getBlock().getType();

        for(Material d: DESTRUCTIVE) {
            if(d.equals(blockType)) {
                Material toBlockMaterial = event.getToBlock().getType();
                Block toBlock = event.getToBlock();

                for(Material df: DESTRUCTIVE_FORCES) {
                    if(df.equals(toBlockMaterial)) {
                        PlayerBlock toPlayerBlock = new PlayerBlock(toBlock.getX(), toBlock.getY(), toBlock.getZ(), null);

                        for(PlayerBlock playerBlock: database.getPlayerBlocks()) {
                            if(playerBlock.equalsIgnoreUuid(toPlayerBlock)) {

                                // delete from the database or set cancelled value "true"
                                event.setCancelled(true);
                                break;
                            }
                        }

                        break;
                    }
                }

                break;
            }
        }
    }
}
