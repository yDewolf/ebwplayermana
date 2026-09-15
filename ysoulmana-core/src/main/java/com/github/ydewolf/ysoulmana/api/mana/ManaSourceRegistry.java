package com.github.ydewolf.ysoulmana.api.mana;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ManaSourceRegistry {
    private static final List<ICastManaSource> SOURCES = new ArrayList<>();

    public static void registerSource(ICastManaSource source) {
        SOURCES.add(source);
    }

    public static Optional<ManaSourceHolder> getSourceFromEntity(LivingEntity entity) {
//      checks for entity hands
        for (ItemStack stack : entity.getHandSlots()) {
            if (stack.isEmpty()) continue;

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
