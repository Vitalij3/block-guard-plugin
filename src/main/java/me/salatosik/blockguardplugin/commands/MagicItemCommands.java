package me.salatosik.blockguardplugin.commands;

import me.salatosik.blockguardplugin.util.MagicItem;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MagicItemCommands implements CommandExecutor {

    private void give(Player player, MagicItem item) {
        ItemStack[] playerItems = player.getInventory().getContents();

        if(playerItems.length != 0) {
            for(ItemStack playerItem: playerItems) {
                if(playerItem == null) break;

                ItemMeta itemPlayerMeta = playerItem.getItemMeta();

                if(playerItem.getType().equals(item.getMaterial()) && itemPlayerMeta.getLore().size() != 0 && itemPlayerMeta.getLore().get(0).equals(item.getLore())) {
                    player.sendMessage(ChatColor.RED + "This item is already in your inventory!");
                    return;
                }
            }
        }

        player.getInventory().addItem(item.toItemStack());
    }


    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(commandSender instanceof Player) {
            Player player = (Player) commandSender;

            switch(command.getName()) {
                case "give-block-picker":
                    give(player, MagicItem.BLOCK_PICKER);
                    break;

                case "give-guard-remover":
                    give(player, MagicItem.GUARD_REMOVER);
                    break;
            }
        }

        return true;
    }
}