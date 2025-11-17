package com.kiwi.dungeonkemono;

import com.kiwi.dungeonkemono.network.PacketHandler;
import com.mojang.logging.LogUtils;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(DungeonKemono.MOD_ID)
public class DungeonKemono {

    public static final String MOD_ID = "dungeonkemono";
    public static final Logger LOGGER = LogUtils.getLogger();

    public DungeonKemono() {
        LOGGER.info("Dungeon & Kemono 모드 로딩 시작!");

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Common Setup 이벤트 등록
        modEventBus.addListener(this::commonSetup);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            // 패킷 등록
            PacketHandler.register();
            LOGGER.info("Network packets registered!");
        });

        LOGGER.info("Dungeon & Kemono 초기화 완료!");
    }
}