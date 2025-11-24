package com.kiwi.dungeonkemono.network;

import com.kiwi.dungeonkemono.DungeonKemono;
import com.kiwi.dungeonkemono.player.PlayerDataProvider;
import com.kiwi.dungeonkemono.skill.SkillExecutor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * 클라이언트 → 서버: 스킬 사용 요청
 */
public class UseSkillPacket {

    private final int skillSlot; // 1~10

    public UseSkillPacket(int skillSlot) {
        this.skillSlot = skillSlot;
    }

    public static void encode(UseSkillPacket packet, FriendlyByteBuf buf) {
        buf.writeInt(packet.skillSlot);
    }

    public static UseSkillPacket decode(FriendlyByteBuf buf) {
        return new UseSkillPacket(buf.readInt());
    }

    public static void handle(UseSkillPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                player.getCapability(PlayerDataProvider.PLAYER_DATA).ifPresent(data -> {
                    // 스킬 실행
                    boolean success = SkillExecutor.executeSkill(player, data, packet.skillSlot);

                    if (!success) {
                        DungeonKemono.LOGGER.debug("Skill {} execution failed for {}",
                                packet.skillSlot, player.getName().getString());
                    }
                });
            }
        });
        ctx.get().setPacketHandled(true);
    }
}