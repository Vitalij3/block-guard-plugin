package me.salatosik.blockguardplugin.commands;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.*;
import me.salatosik.blockguardplugin.core.Database;
import me.salatosik.blockguardplugin.core.PlayerBlock;
import me.salatosik.blockguardplugin.enums.Buttons;
import me.salatosik.blockguardplugin.function.IVerify;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MyBlocksCommand implements CommandExecutor {
    public MyBlocksCommand(Database database) {

        this.database = database;

        SmartInventory.Builder builder = SmartInventory.builder();
        builder.id("my-blocks-command-table");
        builder.title(ChatColor.GOLD + "Your blocks");
        builder.type(InventoryType.CHEST);
        builder.size(3, 9);
        builder.closeable(true);
        builder.provider(new MyBlockInventoryProvider(database));
        myBlocksInventory = builder.build();
    }

    public static class MyBlockInventoryProvider implements InventoryProvider {
        public MyBlockInventoryProvider(Database database) {
            this.database = database;
        }

        private final Database database;

        private ItemStack getPaperItem(int count) {
            ItemStack paperItem = new ItemStack(Material.PAPER);
            paperItem.setAmount(1);
            ItemMeta paperItemMeta = paperItem.getItemMeta();
            paperItemMeta.setLore(Collections.singletonList("block-count"));
            paperItemMeta.setDisplayName("Total blocks with protection: " + count);
            paperItem.setItemMeta(paperItemMeta);

            return paperItem;
        }

        private int getPlayerBlocksCount(String playerUuid) {
            return PlayerBlock.selectBlockByUuid(playerUuid, database.getPlayerBlocks()).size();
        }

        private int removedBlocks = 0;

        @Override
        public void init(Player player, InventoryContents inventoryContents) {
            List<PlayerBlock> playerBlocks = PlayerBlock.selectBlockByUuid(player.getUniqueId().toString(), database.getPlayerBlocks());

            Pagination pagination = inventoryContents.pagination();
            ClickableItem[] paginationItems = new ClickableItem[playerBlocks.size()];

            for(int i = 0; i < playerBlocks.size(); i++) {
                if(!playerBlocks.get(i).worldName.equals(player.getWorld().getName())) continue;

                ItemStack itemStack = new ItemStack(Material.getMaterial(playerBlocks.get(i).blockName));
                itemStack.setAmount(1);
                ItemMeta itemMeta = itemStack.getItemMeta();

                itemMeta.setLore(Arrays.asList(
                        "player-block",
                        playerBlocks.get(i).x + " " + playerBlocks.get(i).y + " " + playerBlocks.get(i).z,
                        playerBlocks.get(i).blockName
                ));

                itemStack.setItemMeta(itemMeta);

                paginationItems[i] = (ClickableItem.of(itemStack, (inventoryClickEvent) -> {

                    ItemStack currentItem = inventoryClickEvent.getCurrentItem();
                    ItemMeta currentItemMeta = currentItem.getItemMeta();
                    List<String> currentItemLore = currentItemMeta.getLore();

                    if(currentItemLore.size() == 3 && currentItemLore.get(0).equals("player-block")) {
                        if(currentItemMeta.getDisplayName() != null && currentItemMeta.getDisplayName().equals(ChatColor.RED + "[REMOVED]")) return;

                        removedBlocks++;
                        List<PlayerBlock> blocks = database.getPlayerBlocks();

                        String[] intStrings = currentItemLore.get(1).split(" ");
                        int x = Integer.parseInt(intStrings[0]), y = Integer.parseInt(intStrings[1]), z = Integer.parseInt(intStrings[2]);

                        if(blocks.size() != 0) {
                            for(PlayerBlock block: blocks) {
                                if(block.x == x & block.y == y & block.z == z & block.blockName.equals(currentItemLore.get(2))) {
                                    ItemStack itemCopied = currentItem.clone();
                                    ItemMeta copiedItemMeta = itemCopied.getItemMeta();

                                    copiedItemMeta.setDisplayName(ChatColor.RED + "[REMOVED]");
                                    itemCopied.setItemMeta(currentItemMeta);
                                    itemCopied.setType(Material.BARRIER);
                                    itemCopied.setItemMeta(copiedItemMeta);

                                    inventoryClickEvent.setCurrentItem(itemCopied);
                                    database.removePlayerBlock(block);

                                    int j = 0, blocksCount = getPlayerBlocksCount(block.uuid);

                                    if(blocksCount == 0) {
                                        inventoryContents.inventory().close(player);
                                        player.sendMessage(ChatColor.YELLOW + "The blocks with protection are over.");
                                        return;
                                    }

                                    for(ItemStack content: inventoryClickEvent.getInventory().getContents()) {
                                        if(content != null && content.getItemMeta() != null && content.getItemMeta().getLore() != null && content.getItemMeta().getLore().size() == 1 && content.getItemMeta().getLore().get(0).equals("block-count")) {
                                            inventoryClickEvent.getInventory().setItem(j, getPaperItem(blocksCount));
                                            break;
                                        }

                                        j++;
                                    }

                                    break;
                                }
                            }
                        }
                    }
                }));
            }

            inventoryContents.set(0, 4, ClickableItem.empty(getPaperItem(playerBlocks.size())));

            pagination.setItems(paginationItems);
            pagination.setItemsPerPage(7);

            try {
                pagination.addToIterator(inventoryContents.newIterator(SlotIterator.Type.HORIZONTAL, 1, 1));
                initPaginationControlButtons(pagination, player, inventoryContents, () -> playerBlocks.size() > 7);

            } catch(ArrayIndexOutOfBoundsException exception) {
                if(!pagination.isFirst()) {
                    pagination.previous().addToIterator(inventoryContents.newIterator(SlotIterator.Type.HORIZONTAL, 1, 1));
                    initPaginationControlButtons(pagination.previous(), player, inventoryContents, () -> playerBlocks.size() > 7);
                }
            }

            removedBlocks = 0;
        }

        private void initPaginationControlButtons(Pagination pagination, Player player, InventoryContents inventoryContents, IVerify iVerify) {
            if(iVerify.verify()) {
                if(!pagination.isFirst() & removedBlocks != 7) inventoryContents.set(0, 0, ClickableItem.of(Buttons.BACK.getItemStack(), event -> myBlocksInventory.open(player, pagination.previous().getPage())));
                if(!pagination.isLast()) inventoryContents.set(0, 8, ClickableItem.of(Buttons.NEXT.getItemStack(), event -> myBlocksInventory.open(player, pagination.next().getPage())));
            }
        }

        @Override
        public void update(Player player, InventoryContents inventoryContents) { /* ouch... */ }
    }

    private final Database database;

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(commandSender instanceof Player) {
            Player player = (Player) commandSender;

            if(PlayerBlock.selectBlockByUuid(player.getUniqueId().toString(), database.getPlayerBlocks()).size() != 0) myBlocksInventory.open(player);
            else player.sendMessage(ChatColor.YELLOW + "You don't currently have blocks with protection.");
        }

        return true;
    }

    private static SmartInventory myBlocksInventory;
}
