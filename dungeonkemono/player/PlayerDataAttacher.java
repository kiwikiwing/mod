package com.kiwi.dungeonkemono.player;

import com.kiwi.dungeonkemono.DungeonKemono;
import net.minecraft.resources.ResourceLocation;
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

    // ✅ 추가: 헬퍼 메서드
    public static PlayerData getPlayerData(Player player) {
        return player.getCapability(PlayerDataProvider.PLAYER_DATA)
                .orElseThrow(() -> new IllegalStateException("Player data not found!"));
    }

    @SubscribeEvent
    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.register(PlayerData.class);
    }

    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            Player player = (Player) event.getObject();
            if (!player.getCapability(PlayerDataProvider.PLAYER_DATA).isPresent()) {
                event.addCapability(PLAYER_DATA_KEY, new PlayerDataProvider());
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            event.getOriginal().getCapability(PlayerDataProvider.PLAYER_DATA).ifPresent(oldData -> {
                event.getEntity().getCapability(PlayerDataProvider.PLAYER_DATA).ifPresent(newData -> {
                    newData.copyFrom(oldData);
                });
            });
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();

        player.getCapability(PlayerDataProvider.PLAYER_DATA).ifPresent(data -> {
            DungeonKemono.LOGGER.info("PlayerData loaded for player: {}", player.getName().getString());
            DungeonKemono.LOGGER.info("Current Job: {}, Level: {}",
                    data.getCurrentJob().name(),
                    data.getCurrentJobData().getLevel());
        });
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        Player player = event.getEntity();
        DungeonKemono.LOGGER.info("PlayerData saved for player: {}", player.getName().getString());
    }
}