package com.github.ydewolf.ebwplayermana.utils;

import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.UUID;

public class AttributeUtils {
    public static double getModifierValue(AttributeInstance attribute, UUID uuid) {
        AttributeModifier mod = attribute.getModifier(uuid);
        return mod != null ? mod.getAmount() : 0.0;
    }

    public static void applyOrUpdateModifier(AttributeInstance attribute, UUID uuid, String name, double amount) {
        applyOrUpdateModifier(attribute, uuid, name, amount, AttributeModifier.Operation.ADDITION);
    }

    public static void applyOrUpdateModifier(AttributeInstance attribute, UUID uuid, String name, double amount, AttributeModifier.Operation operation) {
        if (attribute.getModifier(uuid) != null) {
            attribute.removeModifier(uuid);
        }

        AttributeModifier modifier = new AttributeModifier(uuid, name, amount, operation);
        attribute.addPermanentModifier(modifier);
    }
}
