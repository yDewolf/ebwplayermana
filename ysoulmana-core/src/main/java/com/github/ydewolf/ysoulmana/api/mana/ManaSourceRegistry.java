package com.github.ydewolf.ysoulmana.api.mana;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ManaSourceRegistry {
    private static final List<ICastManaSource> SOURCES = new ArrayList<>();

    public static void registerSource(ICastManaSource source) {
        SOURCES.add(source);
    }

    public static Optional<ManaSourceHolder> getSourceFromPlayer(Player player) {
//      checks for player hands
        for (ItemStack stack : player.getHandSlots()) {
            for (ICastManaSource source : SOURCES) {
                if (source.isSupported(stack)) {
                    return Optional.of(new ManaSourceHolder(source, stack));
                }
            }
        }
        return Optional.empty();
    }

    public record ManaSourceHolder(ICastManaSource source, ItemStack stack) {}
}
