package me.salatosik.blockguardplugin.util;

import org.bukkit.block.Block;
import org.bukkit.event.block.BlockEvent;

import java.util.Collection;
import java.util.List;

public class PlayerBlock {
    public final int x, y, z;
    public final String uuid;

    public PlayerBlock(int x, int y, int z, String uuid) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.uuid = uuid;
    }

    public boolean equals(PlayerBlock playerBlock) {
        return playerBlock.uuid.equals(uuid) & playerBlock.x == x & playerBlock.y == y & playerBlock.z == z;
    }

    public boolean equalsIgnoreUuid(PlayerBlock playerBlock) {
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

    public static <E extends BlockEvent> PlayerBlock getPlayerBlockByBlockEvent(E blockEvent, String uuid) {
        Block b = blockEvent.getBlock();
        return new PlayerBlock(b.getX(), b.getY(), b.getZ(), uuid);
    }
}
