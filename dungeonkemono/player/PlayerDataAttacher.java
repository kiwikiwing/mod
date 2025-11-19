package com.kiwi.dungeonkemono.player;

import com.kiwi.dungeonkemono.DungeonKemono;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DungeonKemono.MOD_ID)
public class PlayerDataAttacher {

    private static final ResourceLocation PLAYER_DATA_KEY =
            ResourceLocation.fromNamespaceAndPath(DungeonKemono.MOD_ID, "player_data");

    /**
     * 헬퍼 메서드 - PlayerData 가져오기
     */
    public static PlayerData getPlayerData(Player player) {
        return player.getCapability(PlayerDataProvider.PLAYER_DATA)
                .orElseThrow(() -> new IllegalStateException("Player data not found!"));
    }

    /**
     * Capability 등록
     */
    @SubscribeEvent
    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.register(PlayerData.class);
        DungeonKemono.LOGGER.info("PlayerData capability registered!");
    }

    /**
     * 플레이어 엔티티에 Capability 첨부
     */
    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player player) {
            if (!player.getCapability(PlayerDataProvider.PLAYER_DATA).isPresent()) {
                event.addCapability(PLAYER_DATA_KEY, new PlayerDataProvider());
                DungeonKemono.LOGGER.debug("PlayerData attached to player entity");
            }
        }
    }

    /**
     * 사망 후 리스폰 시 데이터 복사 (핵심!)
     */
    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (!event.isWasDeath()) {
            return; // 사망이 아니면 무시
        }

        Player originalPlayer = event.getOriginal();
        Player newPlayer = event.getEntity();

        DungeonKemono.LOGGER.info("PlayerClone event - Death detected");

        // ✅ 원본 플레이어에게 Capability 강제 활성화
        originalPlayer.reviveCaps();

        try {
            originalPlayer.getCapability(PlayerDataProvider.PLAYER_DATA).ifPresent(oldData -> {
                newPlayer.getCapability(PlayerDataProvider.PLAYER_DATA).ifPresent(newData -> {
                    // 데이터 복사 전 로그
                    DungeonKemono.LOGGER.info("Old data - Job: {}, Level: {}",
                            oldData.getCurrentJob().getDisplayName(),
                            oldData.getCurrentLevel());

                    // ✅ 핵심: 데이터 복사
                    newData.copyFrom(oldData);

                    // 데이터 복사 후 로그
                    DungeonKemono.LOGGER.info("New data - Job: {}, Level: {}",
                            newData.getCurrentJob().getDisplayName(),
                            newData.getCurrentLevel());

                    // 서버 플레이어인 경우 클라이언트에 동기화
                    if (newPlayer instanceof ServerPlayer serverPlayer) {
                        newData.syncToClient(serverPlayer);
                        DungeonKemono.LOGGER.info("PlayerData synced to client after death");
                    }
                });
            });
        } finally {
            // ✅ 원본 플레이어 Capability 무효화 (메모리 누수 방지)
            originalPlayer.invalidateCaps();
        }
    }

    /**
     * 플레이어 로그인 시 - 데이터 로드 확인 및 동기화
     */
    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            serverPlayer.getCapability(PlayerDataProvider.PLAYER_DATA).ifPresent(data -> {
                DungeonKemono.LOGGER.info("PlayerData loaded for player: {}", serverPlayer.getName().getString());
                DungeonKemono.LOGGER.info("Current Job: {}, Level: {}",
                        data.getCurrentJob().getDisplayName(),
                        data.getCurrentJobData().getLevel());

                // 클라이언트에 동기화
                data.syncToClient(serverPlayer);
            });
        }
    }

    /**
     * 플레이어 로그아웃 시 - 데이터 저장 확인
     */
    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        Player player = event.getEntity();
        player.getCapability(PlayerDataProvider.PLAYER_DATA).ifPresent(data -> {
            DungeonKemono.LOGGER.info("PlayerData saved for player: {} (Job: {}, Level: {})",
                    player.getName().getString(),
                    data.getCurrentJob().getDisplayName(),
                    data.getCurrentLevel());
        });
    }

    /**
     * 플레이어 리스폰 시 - 추가 동기화 (안전장치)
     */
    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            serverPlayer.getCapability(PlayerDataProvider.PLAYER_DATA).ifPresent(data -> {
                DungeonKemono.LOGGER.info("PlayerRespawn event. Re-syncing data for: {}",
                        serverPlayer.getName().getString());

                // 클라이언트에 재동기화
                data.syncToClient(serverPlayer);
            });
        }
    }
}