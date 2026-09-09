package com.github.ydewolf.ebwplayermana.network;

import com.github.ydewolf.ebwplayermana.api.ManaBonusType;
import com.github.ydewolf.ebwplayermana.client.ClientManaData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

public class SyncManaS2CPacket {
    private final float currentMana;
    private final Map<ManaBonusType, Float> bonusMap;

    public SyncManaS2CPacket(float currentMana, Map<ManaBonusType, Float> bonusMap) {
        this.currentMana = currentMana;
        this.bonusMap = bonusMap;
    }

    public static void encode(SyncManaS2CPacket msg, FriendlyByteBuf buf) {
        buf.writeFloat(msg.currentMana);
        buf.writeInt(msg.bonusMap.size());

        for (Map.Entry<ManaBonusType, Float> entry : msg.bonusMap.entrySet()) {
            buf.writeEnum(entry.getKey());
            buf.writeFloat(entry.getValue());
        }
    }

    public static SyncManaS2CPacket decode(FriendlyByteBuf buf) {
        float mana = buf.readFloat();
        int size = buf.readInt();
        Map<ManaBonusType, Float> map = new EnumMap<>(ManaBonusType.class);

        for (int i = 0; i < size; i++) {
            ManaBonusType type = buf.readEnum(ManaBonusType.class);
            float value = buf.readFloat();
            map.put(type, value);
        }

        return new SyncManaS2CPacket(mana, map);
    }

    public static void handle(SyncManaS2CPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ClientManaData.set(msg.currentMana, msg.bonusMap);
        });
        ctx.get().setPacketHandled(true);
    }
}