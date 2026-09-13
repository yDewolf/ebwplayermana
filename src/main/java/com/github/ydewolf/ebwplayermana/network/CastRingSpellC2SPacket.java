package com.github.ydewolf.ebwplayermana.network;

import com.github.ydewolf.ebwplayermana.content.item.CasterRingItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

import java.util.function.Supplier;

public class CastRingSpellC2SPacket {
    public CastRingSpellC2SPacket() {}
    public CastRingSpellC2SPacket(FriendlyByteBuf buf) {}

    public void toBytes(FriendlyByteBuf buf) {}

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            if (player == null) {
                return;
            }

            CasterRingItem ringItem = CasterRingItem.getEquipped(player);
            if (ringItem != null) {
                ringItem.use(player.level(), player, InteractionHand.MAIN_HAND);
            }
        });
        ctx.setPacketHandled(true);
    }
}