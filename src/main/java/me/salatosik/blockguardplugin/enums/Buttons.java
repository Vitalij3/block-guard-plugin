package me.salatosik.blockguardplugin.enums;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public enum Buttons {
    BACK(Material.BARRIER, "Back"), NEXT(Material.SLIME_BLOCK, "Next");

    private final Material material;
    private final String name;

    Buttons(Material material, String name) {
        this.material = material;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Material getMaterial() {
        return material;
    }

    public ItemStack getItemStack() {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(name);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}
