package com.github.ydewolf.ysoulmana.ebwcompat.wrapper;

import com.binaris.wizardry.api.content.item.IManaItem;
import com.github.ydewolf.ysoulmana.api.mana.ICastManaSource;
import net.minecraft.world.entity.LivingEntity;
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
    public void consumeMana(ItemStack stack, float amount, LivingEntity entity) {
        if (stack.getItem() instanceof IManaItem manaItem) {
            if (canConsumeMana(stack, amount)) {
                manaItem.consumeMana(stack, (int) amount, entity);
            }
        }
    }

    @Override
    public boolean canConsumeMana(ItemStack stack, float amount) {
        return getMana(stack) >= amount;
    }
}