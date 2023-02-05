package me.salatosik.blockguardplugin.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;

public enum MagicItem {
    BLOCK_PICKER(1, "Magic stick", "magic-stick", Material.STICK),
    GUARD_REMOVER(1, "Guard remover", "guard-remover", Material.BARRIER),
    GUARD_ADDITION(1, "Guard addition", "guard-addition", Material.SLIME_BALL);

    private final int count;
    private final String name, lore;
    private final Material material;

    MagicItem(int count, String name, String lore, Material material) {
        this.count = count;
        this.name = name;
        this.lore = lore;
        this.material = material;
    }

    public ItemStack toItemStack() {
        ItemStack itemStack = new ItemStack(material);
        itemStack.setAmount(count);

        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(name);
        itemMeta.setLore(Collections.singletonList(lore));

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public boolean equals(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();

        if(!itemStack.getType().equals(material)) return false;
        if(itemMeta.getLore().size() != 1) return false;
        return itemMeta.getDisplayName().equals(name) & itemMeta.getLore().get(0).equals(lore) & itemStack.getAmount() == count;
    }

    public int getCount() {
        return count;
    }

    public Material getMaterial() {
        return material;
    }

    public String getLore() {
        return lore;
    }

    public String getName() {
        return name;
    }
}
