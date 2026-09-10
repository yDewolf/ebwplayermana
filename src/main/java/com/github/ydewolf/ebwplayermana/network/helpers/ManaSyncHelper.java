package com.github.ydewolf.ebwplayermana.network.helpers;

import com.github.ydewolf.ebwplayermana.api.ManaBonusType;
import com.github.ydewolf.ebwplayermana.content.attribute.ManaAttributes;
import com.github.ydewolf.ebwplayermana.content.attribute.ManaModifiers;
import com.github.ydewolf.ebwplayermana.content.mana.PlayerManaProvider;
import com.github.ydewolf.ebwplayermana.network.ModMessages;
import com.github.ydewolf.ebwplayermana.network.SyncManaS2CPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.LinkedHashMap;
import java.util.Map;

public class ManaSyncHelper {
    public static void syncManaToClient(ServerPlayer player) {
        AttributeInstance maxManaAttr = player.getAttribute(ManaAttributes.MAX_MANA.get());
        if (maxManaAttr == null) return;

        Map<ManaBonusType, Float> bonusMap = new LinkedHashMap<>();
        bonusMap.put(ManaBonusType.BASE, (float) maxManaAttr.getBaseValue());

        float progression = 0.0f;
        float equipment = 0.0f;
        float buff = 0.0f;

        for (AttributeModifier modifier : maxManaAttr.getModifiers()) {
            if (modifier.getId().equals(ManaModifiers.SPELL_PROGRESSION_MANA_UUID)) {
                progression += (float) modifier.getAmount();
            } else if (modifier.getName().toLowerCase().startsWith("curios")) {
                equipment += (float) modifier.getAmount();
            } else {
                buff += (float) modifier.getAmount();
            }
        }

        if (progression > 0) bonusMap.put(ManaBonusType.PROGRESSION, progression);
        if (equipment > 0) bonusMap.put(ManaBonusType.EQUIPMENT, equipment);
        if (buff > 0) bonusMap.put(ManaBonusType.BUFF, buff);

        player.getCapability(PlayerManaProvider.PLAYER_MANA).ifPresent(mana -> {
            ModMessages.sendToPlayer(
                    new SyncManaS2CPacket(mana.getMana(), bonusMap),
                    player
            );
        });
    }
}
