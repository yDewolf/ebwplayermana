package com.github.ydewolf.ebwplayermana.events.mana;

import com.binaris.wizardry.api.content.item.ICastItem;
import com.binaris.wizardry.api.content.item.IManaItem;
import com.github.ydewolf.ebwplayermana.PlayerManaConfig;
import com.github.ydewolf.ebwplayermana.content.attribute.ManaAttributes;
import com.github.ydewolf.ebwplayermana.content.attribute.ManaModifiers;
import com.github.ydewolf.ebwplayermana.content.mana.IPlayerMana;
import com.github.ydewolf.ebwplayermana.content.mana.PlayerManaProvider;
import com.github.ydewolf.ebwplayermana.network.helpers.ManaSyncHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;

import static com.github.ydewolf.ebwplayermana.content.attribute.ManaModifiers.BONUS_DIFF_THRESHOLD;

public class ManaEventHelper {
    private static void regenerateMana(IPlayerMana mana, ServerPlayer player, double regen_amount) {
        if (mana.onRegenCooldown()) {
            mana.decrementRegenCooldown();
            return;
        }

        if (mana.getMana() < mana.getMaxMana() && regen_amount > 0) {
            mana.addMana((float) regen_amount);
            if (player instanceof ServerPlayer) {
                ManaSyncHelper.syncManaToClient(player);
            }
        }
    }

    public static void handleManaRegen(ServerPlayer player) {
        if (player.tickCount % PlayerManaConfig.manaRegenTickRate != 0) {
            return;
        }

        double manaAmountPerRegenTick = player.getAttributeValue(ManaAttributes.MANA_REGEN.get()) / (20.0D / PlayerManaConfig.manaRegenTickRate);
        double maxMana = player.getAttributeValue(ManaAttributes.MAX_MANA.get());
        player.getCapability(PlayerManaProvider.PLAYER_MANA).ifPresent(mana -> {
            mana.setMaxMana((float) maxMana);
            regenerateMana(mana, player, manaAmountPerRegenTick);
        });
    }

    public static void handleWandManaBonus(ServerPlayer player) {
        ItemStack heldItem = player.getMainHandItem();
        double wand_bonus = 0.0;
        if (heldItem.getItem() instanceof IManaItem wandItem) {
            if (!(wandItem instanceof ICastItem)) { return; }

            wand_bonus = wandItem.getMana(heldItem) * PlayerManaConfig.wandMaxManaBonusRate;
        }

        AttributeInstance maxManaAttr = player.getAttribute(ManaAttributes.MAX_MANA.get());

        if (maxManaAttr == null) { return; }
        AttributeModifier currentMod = maxManaAttr.getModifier(ManaModifiers.WAND_BONUS_UUID);
        double currentBonus = currentMod != null ? currentMod.getAmount() : 0.0;

        if (Math.abs(currentBonus - wand_bonus) > BONUS_DIFF_THRESHOLD) {
            if (currentMod != null) {
                maxManaAttr.removeModifier(ManaModifiers.WAND_BONUS_UUID);
            }

            if (wand_bonus > 0) {
                maxManaAttr.addTransientModifier(new AttributeModifier(
                        ManaModifiers.WAND_BONUS_UUID, "Wand Mana Bonus", wand_bonus, AttributeModifier.Operation.ADDITION));
            }

            player.getCapability(PlayerManaProvider.PLAYER_MANA).ifPresent(mana -> {
                mana.setMaxMana((float) maxManaAttr.getValue());
                if (player instanceof ServerPlayer) {
                    ManaSyncHelper.syncManaToClient(player);
                }
            });
        }
    }
}
