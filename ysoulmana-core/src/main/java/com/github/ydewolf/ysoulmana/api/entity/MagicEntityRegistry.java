package com.github.ydewolf.ysoulmana.api.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MagicEntityRegistry {
    public record MagicEntityConfig(double baseMaxMana, double baseManaRegen) {}

    private static final Map<EntityType<? extends LivingEntity>, MagicEntityConfig> REGISTERED_ENTITIES = new HashMap<>();

    /**
     * Registers an EntityType as a magic entity
     */
    public static void registerEntity(EntityType<? extends LivingEntity> type, double baseMaxMana, double baseManaRegen) {
        REGISTERED_ENTITIES.put(type, new MagicEntityConfig(baseMaxMana, baseManaRegen));
    }

    /**
     * Registers with maxMana = 100 and manaRegen = 1.
     */
    public static void registerEntity(EntityType<? extends LivingEntity> type) {
        registerEntity(type, 100.0D, 1.0D);
    }

    public static boolean isMagicEntity(EntityType<?> type) {
        return REGISTERED_ENTITIES.containsKey(type);
    }

    public static Optional<MagicEntityConfig> getConfig(EntityType<?> type) {
        return Optional.ofNullable(REGISTERED_ENTITIES.get(type));
    }

    public static Map<EntityType<? extends LivingEntity>, MagicEntityConfig> getRegisteredEntities() {
        return REGISTERED_ENTITIES;
    }
}
