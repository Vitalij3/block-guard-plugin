package me.salatosik.blockguardplugin.core;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.List;

public class DatabaseCleaner implements Runnable {
    public DatabaseCleaner(Database database, List<World> worlds) {
        this.database = database;
        this.worlds = worlds;
    }

    private final Database database;
    private final List<World> worlds;

    @Override
    public void run() {
        List<PlayerBlock> playerBlocks = database.getPlayerBlocks();

        for(PlayerBlock pb: playerBlocks) {
            for(World world: worlds) {
                if(world.getName().equals(pb.worldName)) {
                    Block block = world.getBlockAt(new Location(world, pb.x, pb.y, pb.z));

                    if(block != null && !block.getType().toString().equals(pb.blockName)) {
                        database.removePlayerBlock(pb);
                        break;
                    }
                }
            }
        }
    }
}
