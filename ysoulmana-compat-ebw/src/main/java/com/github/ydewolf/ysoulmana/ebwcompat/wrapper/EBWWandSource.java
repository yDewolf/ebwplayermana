package com.github.ydewolf.ysoulmana.ebwcompat.wrapper;

import com.binaris.wizardry.api.content.item.IManaItem;
import com.github.ydewolf.ysoulmana.api.mana.ICastManaSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class EBWWandSource implements ICastManaSource {

    @Override
    public boolean isSupported(ItemStack stack) {
        return stack.getItem() instanceof IManaItem;
    }

    @Override
    public float getMana(ItemStack stack) {
        if (stack.getItem() instanceof IManaItem manaItem) {
            return manaItem.getMana(stack);
        }
        return 0;
    }

    @Override
    public float getMaxMana(ItemStack stack) {
        if (stack.getItem() instanceof IManaItem manaItem) {
            return manaItem.getManaCapacity(stack);
        }
        return 0;
    }

    @Override
    public void consumeMana(ItemStack stack, float amount, Player player) {
        if (stack.getItem() instanceof IManaItem manaItem) {
            manaItem.consumeMana(stack, (int) amount, player);
        }
    }
}