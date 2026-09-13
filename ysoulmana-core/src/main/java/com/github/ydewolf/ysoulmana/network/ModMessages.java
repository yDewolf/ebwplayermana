package com.github.ydewolf.ysoulmana.network;

import com.github.ydewolf.ysoulmana.SoulManaMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;


public class ModMessages {
    private static SimpleChannel INSTANCE;
    private static int packetId = 0;

    private static int id() { return packetId++; }

    public static void register() {
        SimpleChannel net = NetworkRegistry.ChannelBuilder
                .named(ResourceLocation.tryBuild(SoulManaMod.MODID, "messages"))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        INSTANCE = net;

        net.messageBuilder(CastRingSpellC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(CastRingSpellC2SPacket::new)
                .encoder(CastRingSpellC2SPacket::toBytes)
                .consumerMainThread(CastRingSpellC2SPacket::handle)
                .add();

        net.messageBuilder(SyncManaS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(SyncManaS2CPacket::decode)
                .encoder(SyncManaS2CPacket::encode)
                .consumerMainThread(SyncManaS2CPacket::handle)
                .add();
    }

    public static void sendToPlayer(Object message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    public static void sendToServer(Object message) {
        INSTANCE.sendToServer(message);
    }
}