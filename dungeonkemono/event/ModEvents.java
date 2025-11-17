package com.kiwi.dungeonkemono.event;

import com.kiwi.dungeonkemono.DungeonKemono;
import com.kiwi.dungeonkemono.player.PlayerData;
import com.kiwi.dungeonkemono.player.PlayerDataProvider;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 서버 사이드 이벤트 핸들러
 */
@Mod.EventBusSubscriber(modid = DungeonKemono.MOD_ID)
public class ModEvents {
    
    /**
     * 플레이어가 로그인할 때 - 데이터 동기화
     */
    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            serverPlayer.getCapability(PlayerDataProvider.PLAYER_DATA).ifPresent(data -> {
                DungeonKemono.LOGGER.info("PlayerData loaded for player: {}", serverPlayer.getName().getString());
                DungeonKemono.LOGGER.info("Current Job: {}, Level: {}", 
                        data.getCurrentJob(), 
                        data.getCurrentJobData().getLevel());
                
                // 클라이언트로 데이터 전송
                data.syncToClient(serverPlayer);
            });
        }
    }
    
    /**
     * 몬스터 처치 시 경험치 획득
     */
    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (event.getSource().getEntity() instanceof ServerPlayer player) {
            player.getCapability(PlayerDataProvider.PLAYER_DATA).ifPresent(data -> {
                // 경험치 계산
                int exp = calculateExpGain(event.getEntity());
                
                if (exp > 0) {
                    DungeonKemono.LOGGER.debug("{} gained {} exp from {}", 
                            player.getName().getString(), 
                            exp, 
                            event.getEntity().getType().getDescription().getString());
                    
                    // 경험치 획득 메시지 (액션바)
                    player.displayClientMessage(
                            net.minecraft.network.chat.Component.literal("§a+" + exp + " EXP"),
                            true
                    );
                    
                    // 경험치 추가 전 레벨 저장
                    int oldLevel = data.getCurrentLevel();
                    
                    // 경험치 추가
                    data.addExperience(exp);
                    
                    // 레벨업 체크
                    int newLevel = data.getCurrentLevel();
                    if (newLevel > oldLevel) {
                        sendLevelUpMessage(player, data, newLevel - oldLevel);
                    }
                    
                    // 클라이언트로 데이터 동기화
                    data.syncToClient(player);
                }
            });
        }
    }
    
    /**
     * 엔티티별 경험치 계산
     */
    private static int calculateExpGain(net.minecraft.world.entity.Entity entity) {
        if (entity instanceof net.minecraft.world.entity.monster.Monster) {
            return 50; // 몬스터
        } else if (entity instanceof net.minecraft.world.entity.animal.Animal) {
            return 10; // 동물
        }
        return 0;
    }
    
    /**
     * 레벨업 메시지 전송
     */
    private static void sendLevelUpMessage(ServerPlayer player, PlayerData data, int levelUps) {
        int level = data.getCurrentJobData().getLevel();
        
        DungeonKemono.LOGGER.info("{} leveled up {} to Lv.{}", 
                player.getName().getString(), 
                data.getCurrentJob().getDisplayName(), 
                level);
        
        // 채팅 메시지
        player.sendSystemMessage(
                net.minecraft.network.chat.Component.literal(
                        "§e§l[레벨업!] §r§6" + data.getCurrentJob().getDisplayName() + " §fLv." + level
                )
        );
        
        player.sendSystemMessage(
                net.minecraft.network.chat.Component.literal(
                        "§6자유 포인트: §e+5 §f(총: " + data.getStats().getFreePoints() + ")"
                )
        );
        
        // 액션바 메시지
        player.displayClientMessage(
                net.minecraft.network.chat.Component.literal(
                        "§6★ 레벨업! §e" + data.getCurrentJob().getDisplayName() + " Lv." + level + " §6★"
                ),
                true
        );
        
        // 전직 알림
        if (level == 10 || level == 20 || level == 30 || level == 40) {
            player.sendSystemMessage(
                    net.minecraft.network.chat.Component.literal("§e§l[알림] §r§6" + (level / 10) + "차 전직이 가능합니다!")
            );
            player.sendSystemMessage(
                    net.minecraft.network.chat.Component.literal("§6전직소 NPC를 찾아가세요!")
            );
        }
    }
}
