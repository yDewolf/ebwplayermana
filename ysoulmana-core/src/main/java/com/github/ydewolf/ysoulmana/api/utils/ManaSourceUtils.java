package com.github.ydewolf.ysoulmana.api.utils;

import com.github.ydewolf.ysoulmana.api.mana.ManaSourceRegistry;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class ManaSourceUtils {
    /**
     * Searches for the first valid source of mana in player hands
     */
    public static Optional<ManaSourceRegistry.ManaSourceHolder> getActiveManaSource(Player player) {
        return ManaSourceRegistry.getSourceFromPlayer(player);
    }

    /**
     * Returns current mana from the active mana source (see getActiveManaSource)
     */
    public static float getItemMana(Player player) {
        return getActiveManaSource(player)
                .map(holder -> holder.source().getMana(holder.stack()))
                .orElse(0f);
    }

    /**
     * Returns active mana source's max mana
     */
    public static float getItemMaxMana(Player player) {
        return getActiveManaSource(player)
                .map(holder -> holder.source().getMaxMana(holder.stack()))
                .orElse(0f);
    }

    /**
     * Returns active mana source's ItemStack
     */
    public static Optional<ItemStack> getItemStack(Player player) {
        return getActiveManaSource(player).map(ManaSourceRegistry.ManaSourceHolder::stack);
    }
}
