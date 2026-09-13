package com.github.ydewolf.ysoulmana.events.mana;

import com.github.ydewolf.ysoulmana.SoulManaConfig;
import com.github.ydewolf.ysoulmana.content.attribute.ManaAttributes;
import com.github.ydewolf.ysoulmana.content.mana.IPlayerMana;
import com.github.ydewolf.ysoulmana.content.mana.PlayerManaProvider;
import com.github.ydewolf.ysoulmana.network.helpers.ManaSyncHelper;
import net.minecraft.server.level.ServerPlayer;

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
        if (player.tickCount % SoulManaConfig.manaRegenTickRate != 0) {
            return;
        }

        double manaAmountPerRegenTick = player.getAttributeValue(ManaAttributes.MANA_REGEN.get()) / (20.0D / SoulManaConfig.manaRegenTickRate);
        double maxMana = player.getAttributeValue(ManaAttributes.MAX_MANA.get());
        player.getCapability(PlayerManaProvider.PLAYER_MANA).ifPresent(mana -> {
            mana.setMaxMana((float) maxMana);
            regenerateMana(mana, player, manaAmountPerRegenTick);
        });
    }
}
