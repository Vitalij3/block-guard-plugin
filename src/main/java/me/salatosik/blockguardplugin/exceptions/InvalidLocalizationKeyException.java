package me.salatosik.blockguardplugin.exceptions;

import me.salatosik.blockguardplugin.Main;
import org.bukkit.plugin.java.JavaPlugin;

public class InvalidLocalizationKeyException extends Exception {
    public InvalidLocalizationKeyException(String message) {
        super(message);
        stopPlugin();
    }

    private static void stopPlugin() {
        JavaPlugin javaPlugin = JavaPlugin.getProvidingPlugin(Main.class);
        javaPlugin.getPluginLoader().disablePlugin(javaPlugin);
    }
}
