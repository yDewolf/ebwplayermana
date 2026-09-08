package com.github.ydewolf.ebwplayermana.item;

import com.github.ydewolf.ebwplayermana.attribute.ManaAttributes;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class ManaRegenRingItem extends Item implements ICurioItem {

    public ManaRegenRingItem(Properties properties) {
        super(properties);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext slotContext, UUID uuid, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> modifiers = LinkedHashMultimap.create();

        modifiers.put(ManaAttributes.MANA_REGEN.get(),
                new AttributeModifier(uuid, "Mana regen ring bonus", 0.75D, AttributeModifier.Operation.ADDITION));

        return modifiers;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        tooltipComponents.add(Component.translatable("tooltip.ebwplayermana.mana_regen_ring")
                .withStyle(ChatFormatting.DARK_BLUE));

        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
    }
}