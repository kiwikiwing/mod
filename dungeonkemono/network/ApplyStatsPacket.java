package com.kiwi.dungeonkemono.network;

import com.kiwi.dungeonkemono.DungeonKemono;
import com.kiwi.dungeonkemono.player.PlayerDataProvider;
import com.kiwi.dungeonkemono.stats.StatType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * 클라이언트에서 서버로 스탯 변경 요청을 보내는 패킷
 */
public class ApplyStatsPacket {

    private final int strength;
    private final int dexterity;
    private final int vitality;
    private final int intelligence;
    private final int luck;

    public ApplyStatsPacket(int strength, int dexterity, int vitality, int intelligence, int luck) {
        this.strength = strength;
        this.dexterity = dexterity;
        this.vitality = vitality;
        this.intelligence = intelligence;
        this.luck = luck;
    }

    // 패킷 인코딩 (클라이언트 → 서버)
    public static void encode(ApplyStatsPacket packet, FriendlyByteBuf buf) {
        buf.writeInt(packet.strength);
        buf.writeInt(packet.dexterity);
        buf.writeInt(packet.vitality);
        buf.writeInt(packet.intelligence);
        buf.writeInt(packet.luck);
    }

    // 패킷 디코딩
    public static ApplyStatsPacket decode(FriendlyByteBuf buf) {
        return new ApplyStatsPacket(
                buf.readInt(),
                buf.readInt(),
                buf.readInt(),
                buf.readInt(),
                buf.readInt()
        );
    }

    // 패킷 처리 (서버에서)
    public static void handle(ApplyStatsPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                player.getCapability(PlayerDataProvider.PLAYER_DATA).ifPresent(data -> {
                    // 현재 스탯
                    int currentStr = data.getStats().getStat(StatType.STRENGTH);
                    int currentDex = data.getStats().getStat(StatType.DEXTERITY);
                    int currentVit = data.getStats().getStat(StatType.VITALITY);
                    int currentInt = data.getStats().getStat(StatType.INTELLIGENCE);
                    int currentLuk = data.getStats().getStat(StatType.LUCK);

                    // 변경된 스탯
                    int newStr = packet.strength;
                    int newDex = packet.dexterity;
                    int newVit = packet.vitality;
                    int newInt = packet.intelligence;
                    int newLuk = packet.luck;

                    // 사용된 포인트 계산
                    int usedPoints = (newStr - currentStr) + (newDex - currentDex) +
                            (newVit - currentVit) + (newInt - currentInt) +
                            (newLuk - currentLuk);

                    // 자유 포인트 확인
                    if (usedPoints <= data.getStats().getFreePoints() && usedPoints >= 0) {
                        // 스탯 적용
                        data.getStats().setStat(StatType.STRENGTH, newStr);
                        data.getStats().setStat(StatType.DEXTERITY, newDex);
                        data.getStats().setStat(StatType.VITALITY, newVit);
                        data.getStats().setStat(StatType.INTELLIGENCE, newInt);
                        data.getStats().setStat(StatType.LUCK, newLuk);

                        // 자유 포인트 차감
                        data.getStats().setFreePoints(data.getStats().getFreePoints() - usedPoints);

                        // 성공 메시지
                        player.sendSystemMessage(
                                net.minecraft.network.chat.Component.literal("§a스탯이 성공적으로 적용되었습니다!")
                        );

                        DungeonKemono.LOGGER.info("{} applied stats. Used {} points. Remaining: {}",
                                player.getName().getString(),
                                usedPoints,
                                data.getStats().getFreePoints());

                        // 클라이언트로 동기화
                        SyncPlayerDataPacket syncPacket = new SyncPlayerDataPacket(
                                data.getCurrentJob(),
                                data.getCurrentJobData().getLevel(),
                                data.getCurrentJobData().getExperience(),
                                data.getCurrentJobData().getExpForNextLevel(),
                                data.getStats().getStat(StatType.STRENGTH),
                                data.getStats().getStat(StatType.DEXTERITY),
                                data.getStats().getStat(StatType.VITALITY),
                                data.getStats().getStat(StatType.INTELLIGENCE),
                                data.getStats().getStat(StatType.LUCK),
                                data.getStats().getFreePoints()
                        );

                        PacketHandler.INSTANCE.send(
                                net.minecraftforge.network.PacketDistributor.PLAYER.with(() -> player),
                                syncPacket
                        );
                    } else {
                        // 실패 메시지
                        player.sendSystemMessage(
                                net.minecraft.network.chat.Component.literal("§c자유 포인트가 부족합니다!")
                        );
                    }
                });
            }
        });
        ctx.get().setPacketHandled(true);
    }
}