package com.github.ydewolf.ysoulmana.content.item.debug;
import com.github.ydewolf.ysoulmana.content.mana.PlayerManaProvider;
import com.github.ydewolf.ysoulmana.content.mana.helpers.ManaUsageHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DebugManaStaffItem extends Item {
    private static final float[] PRESET_COSTS = {5.0f, 15.0f, 30.0f, 50.0f, 100.0f};
    private static final String COST_NBT_KEY = "DebugManaCost";

    public DebugManaStaffItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (level.isClientSide()) {
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
        }

//      serverside
        if (player.isShiftKeyDown()) {
            float nextCost = getNextCost(stack);
            setManaCost(stack, nextCost);
            player.displayClientMessage(
                    Component.translatable("message.ysoulmana.debug.mana_cost", nextCost),
                    true
            );
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
        }

        float cost = getManaCost(stack);
        boolean success = ManaUsageHelper.handleCastManaConsumption(player, true, cost);
        player.displayClientMessage(
                Component.translatable(
                    success ? "message.ysoulmana.debug.mana_cost.success" : "message.ysoulmana.debug.mana_cost.fail",
                    cost),
                true
        );

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    public static float getManaCost(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        if (!tag.contains(COST_NBT_KEY)) {
            tag.putFloat(COST_NBT_KEY, PRESET_COSTS[0]);
        }
        return tag.getFloat(COST_NBT_KEY);
    }

    public static void setManaCost(ItemStack stack, float cost) {
        stack.getOrCreateTag().putFloat(COST_NBT_KEY, cost);
    }

    private float getNextCost(ItemStack stack) {
        float current = getManaCost(stack);
        for (int i = 0; i < PRESET_COSTS.length; i++) {
            if (PRESET_COSTS[i] == current) {
                return PRESET_COSTS[(i + 1) % PRESET_COSTS.length];
            }
        }
        return PRESET_COSTS[0];
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltip, @NotNull TooltipFlag isAdvanced) {
        float currentCost = getManaCost(stack);
        tooltip.add(Component.translatable("message.ysoulmana.debug.mana_cost.current", currentCost));
        tooltip.add(Component.literal("§8 Right click: consume mana"));
        tooltip.add(Component.literal("§8 Shift + right click: alternate mana cost"));
        super.appendHoverText(stack, level, tooltip, isAdvanced);
    }
}
