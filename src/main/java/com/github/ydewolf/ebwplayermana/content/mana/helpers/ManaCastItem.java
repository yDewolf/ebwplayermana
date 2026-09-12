package com.github.ydewolf.ebwplayermana.content.mana.helpers;

import com.binaris.wizardry.api.content.item.ICastItem;
import com.binaris.wizardry.api.content.item.IManaItem;
import net.minecraft.world.item.ItemStack;

public record ManaCastItem(
        ICastItem castItem,
        IManaItem manaItem,
        ItemStack stack
) {
}
