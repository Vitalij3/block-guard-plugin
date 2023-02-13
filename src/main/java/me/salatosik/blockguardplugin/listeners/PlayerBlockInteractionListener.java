package me.salatosik.blockguardplugin.listeners;

import me.salatosik.blockguardplugin.Vars;
import me.salatosik.blockguardplugin.core.Database;
import me.salatosik.blockguardplugin.enums.MagicItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import me.salatosik.blockguardplugin.core.PlayerBlock;

import java.util.ArrayList;
import java.util.List;

public class PlayerBlockInteractionListener implements Listener {
    public PlayerBlockInteractionListener(Database database) {
        this.database = database;
    }

    private final Database database;

    private boolean checkForRight(PlayerBlock playerBlock) {
        List<PlayerBlock> playerBlocks = database.getPlayerBlocks();
        for(PlayerBlock block: playerBlocks) if(block.equals(playerBlock)) return false;
        for(PlayerBlock block: playerBlocks) if(block.equalsIgnoreUuid(playerBlock)) return true;
        return false;
    }

    @EventHandler
    public void onPlayerPlacedBlockIn(PlayerInteractEvent event) {
        if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && !Vars.verifyWorld(event.getClickedBlock().getWorld())) return;
        if(event.hasItem() && MagicItem.verifyMagicItem(event.getItem())) return;

        if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && !event.getClickedBlock().isEmpty()) {
            Block block = event.getClickedBlock();
            Player player = event.getPlayer();

            for(PlayerBlock playerBlock: database.getPlayerBlocks()) {
                if(playerBlock.x == block.getX() & playerBlock.y == block.getY() & playerBlock.z == block.getZ() && !playerBlock.uuid.equals(player.getUniqueId().toString())) {
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.YELLOW + "You cannot place a block here");
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onPlayerBreaksEvent(BlockBreakEvent event) {
        if(!Vars.verifyWorld((event.getBlock().getWorld()))) return;

        PlayerBlock playerBlock = PlayerBlock.getPlayerBlockByBlockEvent(event, event.getPlayer().getUniqueId().toString(), event.getBlock().getWorld().getName(), event.getBlock().getType().toString());

        if(checkForRight(playerBlock)) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.YELLOW + "You can't hack this block.");

        } else database.removePlayerBlock(playerBlock);
    }

    private boolean protectBlock(List<Block> movedBlocks, String worldName) {
        if(movedBlocks.size() != 0) {
            for(Block block: movedBlocks) {
                PlayerBlock playerBlock = new PlayerBlock(block.getX(), block.getY(), block.getZ(), null, worldName, block.getType().toString());
                if(PlayerBlock.searchIgnoreUuid(playerBlock, database.getPlayerBlocks())) return true;
            }
        }

        return false;
    }

    @EventHandler
    public void onBlockPistonExtend(BlockPistonExtendEvent event) {
        if(!Vars.verifyWorld(event.getBlock().getWorld())) return;
        if(protectBlock(event.getBlocks(), event.getBlock().getWorld().getName())) event.setCancelled(true);
    }

    @EventHandler
    public void onBlockPistonRetract(BlockPistonRetractEvent event) {
        if(!Vars.verifyWorld(event.getBlock().getWorld())) return;
        if(protectBlock(event.getBlocks(), event.getBlock().getWorld().getName())) event.setCancelled(true);
    }

    @EventHandler
    public void onPhysicBlock(EntityChangeBlockEvent event) {
        if(!Vars.verifyWorld(event.getBlock().getWorld())) return;
        Block block = event.getBlock();
        PlayerBlock playerBlock = new PlayerBlock(block.getX(), block.getY(), block.getZ(), null, event.getBlock().getWorld().getName(), block.getType().toString());

        if(PlayerBlock.searchIgnoreUuid(playerBlock, database.getPlayerBlocks())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBurning(BlockBurnEvent event) {
        if(!Vars.verifyWorld(event.getBlock().getWorld())) return;
        Block block = event.getBlock();
        PlayerBlock playerBlock = new PlayerBlock(block.getX(), block.getY(), block.getZ(), null, event.getBlock().getWorld().getName(), block.getType().toString());
        if(PlayerBlock.searchIgnoreUuid(playerBlock, database.getPlayerBlocks())) event.setCancelled(true);
    }

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
        if(!Vars.verifyWorld(event.getBlock().getWorld())) return;
        Material blockType = event.getBlock().getType();

        for(Material d: DESTRUCTIVE) {
            if(d.equals(blockType)) {
                Material toBlockMaterial = event.getToBlock().getType();
                Block toBlock = event.getToBlock();

                for(Material df: DESTRUCTIVE_FORCES) {
                    if(df.equals(toBlockMaterial)) {
                        PlayerBlock toPlayerBlock = new PlayerBlock(toBlock.getX(), toBlock.getY(), toBlock.getZ(), null, event.getBlock().getWorld().getName(), toBlock.getType().toString());

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

    private void cancelBlockExplode(List<Block> blockList) {
        List<PlayerBlock> playerBlocks = database.getPlayerBlocks();
        List<Block> blocks = new ArrayList<>(blockList);

        for(PlayerBlock pb: playerBlocks) {
            for(Block block: blocks) {
                if(pb.x == block.getX() & pb.y == block.getY() & pb.z == block.getZ() & pb.worldName.equals(block.getWorld().getName())) {
                    blockList.remove(block);
                    break;
                }
            }
        }
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        if(Vars.verifyWorld(event.getBlock().getWorld())) cancelBlockExplode(event.blockList());
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        if(Vars.verifyWorld(event.getLocation().getBlock().getWorld())) cancelBlockExplode(event.blockList());
    }
}
