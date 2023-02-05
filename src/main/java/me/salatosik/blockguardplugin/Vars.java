package me.salatosik.blockguardplugin;

import org.bukkit.World;

public class Vars {
    private static boolean allowInWorld = false, allowInNether = false, allowInEnder = false;

    public static void initAllows(boolean world, boolean nether, boolean ender) {
        allowInWorld = world;
        allowInNether = nether;
        allowInEnder = ender;
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
