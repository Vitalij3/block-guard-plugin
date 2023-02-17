package me.salatosik.blockguardplugin.commands.server.tab;

import me.salatosik.blockguardplugin.core.LocalizationManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class ChangePluginLangCommandTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        if(commandSender instanceof ConsoleCommandSender) {
            List<String> langNames = new ArrayList<>();

            for(LocalizationManager.AllowedLocalizationKey key: LocalizationManager.AllowedLocalizationKey.values())
                if(!key.localeName.equalsIgnoreCase(LocalizationManager.AllowedLocalizationKey.UNDEFINED.localeName))
                    langNames.add(key.localeName);

            return langNames;
        }

        return null;
    }
}
