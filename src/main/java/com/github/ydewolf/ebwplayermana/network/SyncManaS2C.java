package com.github.ydewolf.ebwplayermana.network;

import com.github.ydewolf.ebwplayermana.client.ClientManaData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

public class SyncManaS2C {
    private static final Logger log = LoggerFactory.getLogger(SyncManaS2C.class);
    private final float mana;
    private final float maxMana;

    public SyncManaS2C(float mana, float maxMana) {
        this.mana = mana;
        this.maxMana = maxMana;
    }

    public SyncManaS2C(FriendlyByteBuf buf) {
        this.mana = buf.readFloat();
        this.maxMana = buf.readFloat();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeFloat(this.mana);
        buf.writeFloat(this.maxMana);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ClientManaData.set(mana, maxMana);
        });
        return true;
    }
}