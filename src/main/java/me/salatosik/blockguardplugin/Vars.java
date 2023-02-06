package me.salatosik.blockguardplugin;

import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

public class Vars {
    private static boolean allowInWorld = false, allowInNether = false, allowInEnder = false;

    public static void init(FileConfiguration config) {
        allowInWorld = config.getBoolean("allowsInWorlds.world");
        allowInNether = config.getBoolean("allowsInWorlds.nether");
        allowInEnder = config.getBoolean("allowsInWorlds.ender");
    }

    public static boolean verifyWorld(World world) {
        switch(world.getEnvironment()) {
            case NETHER: return Vars.allowInNether;
            case NORMAL: return Vars.allowInWorld;
            case THE_END: return Vars.allowInEnder;
            default: return false;
        }
    }
}
