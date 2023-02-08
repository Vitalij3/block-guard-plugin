package me.salatosik.blockguardplugin.util;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class DatabaseCleaner extends BukkitRunnable {
    public DatabaseCleaner(GeneralDatabase database, List<World> worlds) {
        this.database = database;
        this.worlds = worlds;
    }

    private final GeneralDatabase database;
    private final List<World> worlds;

    @Override
    public void run() {
        List<PlayerBlock> playerBlocks = database.getPlayerBlocks();

        for(PlayerBlock pb: playerBlocks) {
            for(World world: worlds) {
                if(world.getName().equals(pb.worldName)) {
                    Block block = world.getBlockAt(new Location(world, pb.x, pb.y, pb.z));

                    if(block != null && !block.getType().getData().getName().equals(pb.blockName)) {
                        database.removePlayerBlock(pb);
                        break;
                    }
                }
            }
        }
    }
}
