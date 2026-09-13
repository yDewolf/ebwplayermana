package com.github.ydewolf.ysoulmana.api.mana;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface ICastManaSource {
    /** Returns true if this Adapter can use the stack */
    boolean isSupported(ItemStack stack);

    /** Returns current mana from the item stack */
    float getMana(ItemStack stack);

    /** Returns max mana from the item stack */
    float getMaxMana(ItemStack stack);

    /** Consumes mana from the item stack */
    void consumeMana(ItemStack stack, float amount, Player player);
}