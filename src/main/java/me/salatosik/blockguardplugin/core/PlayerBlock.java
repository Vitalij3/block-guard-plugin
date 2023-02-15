package me.salatosik.blockguardplugin.core;

import org.bukkit.block.Block;
import org.bukkit.event.block.BlockEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PlayerBlock {
    public final int x, y, z;
    public final String uuid, worldName, blockName;

    public PlayerBlock(int x, int y, int z, String uuid, String worldName, String blockName) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.uuid = uuid;
        this.worldName = worldName;
        this.blockName = blockName;
    }

    public boolean equals(PlayerBlock playerBlock) {
        return playerBlock.uuid.equals(uuid) & playerBlock.x == x & playerBlock.y == y & playerBlock.z == z & playerBlock.worldName.equals(worldName) & playerBlock.blockName.equals(blockName);
    }

    public boolean equalsIgnoreUuid(PlayerBlock playerBlock) {
        return playerBlock.x == x & playerBlock.y == y & playerBlock.z == z & playerBlock.worldName.equals(worldName) & playerBlock.blockName.equals(blockName);
    }

    public boolean equalsCoordinates(PlayerBlock playerBlock) {
        return playerBlock.x == x & playerBlock.y == y & playerBlock.z == z;
    }

    public static <L extends Collection<PlayerBlock>> boolean search(PlayerBlock playerBlock, L list) {
        for(PlayerBlock block: list) if(block.equals(playerBlock)) return true;
        return false;
    }

    public static <L extends Collection<PlayerBlock>> boolean searchIgnoreUuid(PlayerBlock playerBlock, L list) {
        for(PlayerBlock pb: list) if(pb.equalsIgnoreUuid(playerBlock)) return true;
        return false;
    }

    public static <E extends BlockEvent> PlayerBlock getPlayerBlockByBlockEvent(E blockEvent, String uuid, String worldName, String blockName) {
        Block b = blockEvent.getBlock();
        return new PlayerBlock(b.getX(), b.getY(), b.getZ(), uuid, worldName, blockName);
    }

    public static <L extends PlayerBlock> List<PlayerBlock> selectBlockByUuid(String uuid, List<L> playerBlocks) {
        List<PlayerBlock> newList = new ArrayList<>();

        for(PlayerBlock pb: playerBlocks) {
            if(pb.uuid.equals(uuid)) {
                newList.add(pb);
            }
        }

        return newList;
    }
}
