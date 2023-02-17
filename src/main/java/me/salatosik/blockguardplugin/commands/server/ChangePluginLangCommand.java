package me.salatosik.blockguardplugin.commands.server;

import me.salatosik.blockguardplugin.Main;
import me.salatosik.blockguardplugin.core.LocalizationManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class ChangePluginLangCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(commandSender instanceof ConsoleCommandSender) {
            ConsoleCommandSender consoleSender = (ConsoleCommandSender) commandSender;
            LocalizationManager localizationManager = Main.getLocalizationManager();

            if(strings.length == 1) {
                LocalizationManager.AllowedLocalizationKey localizationKey = LocalizationManager.AllowedLocalizationKey.getByString(strings[0]);

                if(localizationKey != localizationManager.getLocalizationKey()) {
                    if(localizationManager.changeLanguage(localizationKey)) {
                        JavaPlugin javaPlugin = JavaPlugin.getProvidingPlugin(Main.class);
                        javaPlugin.getConfig().set("plugin-language", localizationKey.localeName);
                        javaPlugin.saveConfig();

                        consoleSender.sendMessage("Language key changed to " + localizationManager.getLocalizationKey().toString());

                    } else commandSender.sendMessage("Undefined language key!");

                } else consoleSender.sendMessage("This language is already installed!");

            } else consoleSender.sendMessage("Invalid arguments!");

            return true;
        }

        return false;
    }
}
