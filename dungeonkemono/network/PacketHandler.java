package com.kiwi.dungeonkemono.network;

import com.kiwi.dungeonkemono.DungeonKemono;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
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
    
    /**
     * 서버에서 특정 플레이어에게 패킷 전송
     * @param packet 전송할 패킷
     * @param player 대상 플레이어
     */
    public static void sendToPlayer(Object packet, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }
    
    /**
     * 서버에서 모든 플레이어에게 패킷 전송
     * @param packet 전송할 패킷
     */
    public static void sendToAllPlayers(Object packet) {
        INSTANCE.send(PacketDistributor.ALL.noArg(), packet);
    }
    
    /**
     * 클라이언트에서 서버로 패킷 전송
     * @param packet 전송할 패킷
     */
    public static void sendToServer(Object packet) {
        INSTANCE.sendToServer(packet);
    }
}
