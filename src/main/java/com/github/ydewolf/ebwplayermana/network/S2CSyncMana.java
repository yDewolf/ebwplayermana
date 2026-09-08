package com.github.ydewolf.ebwplayermana.network;

import com.github.ydewolf.ebwplayermana.client.ClientManaData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class S2CSyncMana {
    private final float mana;
    private final float maxMana;

    public S2CSyncMana(float mana, float maxMana) {
        this.mana = mana;
        this.maxMana = maxMana;
    }

    public S2CSyncMana(FriendlyByteBuf buf) {
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
            // Roda no lado do cliente
            ClientManaData.set(mana, maxMana);
        });
        return true;
    }
}