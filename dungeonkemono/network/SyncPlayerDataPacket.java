package com.kiwi.dungeonkemono.network;

import com.kiwi.dungeonkemono.DungeonKemono;
import com.kiwi.dungeonkemono.job.JobType;
import com.kiwi.dungeonkemono.player.PlayerData;
import com.kiwi.dungeonkemono.player.PlayerDataProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * 서버에서 클라이언트로 플레이어 데이터를 동기화하는 패킷
 */
public class SyncPlayerDataPacket {

    private final CompoundTag data;

    // CompoundTag를 직접 받는 생성자 (새로 추가)
    public SyncPlayerDataPacket(CompoundTag data) {
        this.data = data;
    }

    // 기존 생성자 (호환성 유지)
    public SyncPlayerDataPacket(JobType currentJob, int currentLevel, int currentExp,
                                int str, int dex, int vit, int intel, int luk, int freePoints,
                                int maxExp) {
        this.data = new CompoundTag();

        // 기본 정보를 CompoundTag에 저장
        this.data.putString("currentJob", currentJob.name());

        // JobData 정보
        CompoundTag jobData = new CompoundTag();
        jobData.putInt("level", currentLevel);
        jobData.putInt("exp", currentExp);
        this.data.put("currentJobData", jobData);

        // Stats 정보
        CompoundTag stats = new CompoundTag();
        stats.putInt("strength", str);
        stats.putInt("dexterity", dex);
        stats.putInt("vitality", vit);
        stats.putInt("intelligence", intel);
        stats.putInt("luck", luk);
        stats.putInt("freePoints", freePoints);
        this.data.put("stats", stats);

        // 추가 정보
        this.data.putInt("maxExp", maxExp);
    }

    // 네트워크 버퍼로 인코딩
    public static void encode(SyncPlayerDataPacket packet, FriendlyByteBuf buf) {
        buf.writeNbt(packet.data);
    }

    // 네트워크 버퍼에서 디코딩
    public static SyncPlayerDataPacket decode(FriendlyByteBuf buf) {
        CompoundTag data = buf.readNbt();
        return new SyncPlayerDataPacket(data);
    }

    // 패킷 처리
    public static void handle(SyncPlayerDataPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            // 클라이언트 사이드에서 실행
            if (Minecraft.getInstance().player != null) {
                Minecraft.getInstance().player.getCapability(PlayerDataProvider.PLAYER_DATA).ifPresent(playerData -> {
                    // CompoundTag 데이터로 플레이어 데이터 업데이트
                    playerData.deserializeNBT(packet.data);

                    DungeonKemono.LOGGER.debug("클라이언트 플레이어 데이터 동기화 완료");
                });
            }
        });
        ctx.get().setPacketHandled(true);
    }
}