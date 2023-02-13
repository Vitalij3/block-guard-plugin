package me.salatosik.blockguardplugin;

import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

public class Vars {
    private static boolean allowInWorld = false, allowInNether = false, allowInEnder = false;
    private static int maximumProtectedBlocks = 0;

    public static void init(FileConfiguration config) {
        allowInWorld = config.getBoolean("allows-in-worlds.world");
        allowInNether = config.getBoolean("allows-in-worlds.nether");
        allowInEnder = config.getBoolean("allows-in-worlds.ender");
        maximumProtectedBlocks = config.getInt("maximum-protected-block");
    }

    public static boolean verifyWorld(World world) {
        switch(world.getEnvironment()) {
            case NETHER: return Vars.allowInNether;
            case NORMAL: return Vars.allowInWorld;
            case THE_END: return Vars.allowInEnder;
            default: return false;
        }
    }

    public static int getMaximumProtectedBlocks() {
        return maximumProtectedBlocks;
    }
}
