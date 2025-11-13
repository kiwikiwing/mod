package com.kiwi.dungeonkemono.network;

import com.kiwi.dungeonkemono.DungeonKemono;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class PacketHandler {

    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(DungeonKemono.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int packetId = 0;

    private static int id() {
        return packetId++;
    }

    public static void register() {
        // 서버 → 클라이언트 (데이터 동기화)
        INSTANCE.registerMessage(id(), SyncPlayerDataPacket.class,
                SyncPlayerDataPacket::encode,
                SyncPlayerDataPacket::decode,
                SyncPlayerDataPacket::handle);

        // 클라이언트 → 서버 (스탯 적용)
        INSTANCE.registerMessage(id(), ApplyStatsPacket.class,
                ApplyStatsPacket::encode,
                ApplyStatsPacket::decode,
                ApplyStatsPacket::handle);
    }
}