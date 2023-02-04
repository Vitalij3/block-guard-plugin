package me.salatosik.blockguardplugin.commands;

import me.salatosik.blockguardplugin.util.MagicItem;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GuardRemoverCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(commandSender instanceof Player) ((Player) commandSender).getInventory().addItem(MagicItem.GUARD_REMOVER.toItemStack());
        return true;
    }
}
