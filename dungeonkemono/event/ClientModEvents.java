package com.kiwi.dungeonkemono.event;

import com.kiwi.dungeonkemono.DungeonKemono;
import com.kiwi.dungeonkemono.client.KeyBindings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = DungeonKemono.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        DungeonKemono.LOGGER.info("Dungeon & Kemono - Client Setup Started");
        DungeonKemono.LOGGER.info("Dungeon & Kemono - Client Setup Completed");
    }

    @SubscribeEvent
    public static void onKeyRegister(RegisterKeyMappingsEvent event) {
        event.register(KeyBindings.OPEN_JOB_GUI);
        event.register(KeyBindings.OPEN_STATS_GUI);
        DungeonKemono.LOGGER.info("Keybindings registered!");
    }
}