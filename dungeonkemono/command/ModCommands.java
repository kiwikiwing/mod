package com.kiwi.dungeonkemono.command;

import com.kiwi.dungeonkemono.DungeonKemono;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 모든 명령어를 등록하는 메인 클래스
 */
@Mod.EventBusSubscriber(modid = DungeonKemono.MOD_ID)
public class ModCommands {

    /**
     * 서버 시작 시 명령어 등록
     */
    @SubscribeEvent
    public static void onCommandsRegister(RegisterCommandsEvent event) {
        // 각 명령어 클래스의 register 메서드 호출
        JobCommands.register(event.getDispatcher());
        ExpCommands.register(event.getDispatcher());
        LevelCommands.register(event.getDispatcher());
        StatsCommands.register(event.getDispatcher());

        DungeonKemono.LOGGER.info("Dungeon & Kemono 명령어 등록 완료!");
    }
}