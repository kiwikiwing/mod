package com.kiwi.dungeonkemono.event;

import com.kiwi.dungeonkemono.DungeonKemono;
import com.kiwi.dungeonkemono.client.KeyBindings;
import com.kiwi.dungeonkemono.client.gui.JobInfoScreen;
import com.kiwi.dungeonkemono.client.gui.StatsScreen;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DungeonKemono.MOD_ID, value = Dist.CLIENT)
public class ClientForgeEvents {

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) {
            return;
        }

        // K키 - 직업 정보 GUI
        if (KeyBindings.OPEN_JOB_GUI.consumeClick()) {
            DungeonKemono.LOGGER.info("Opening Job Info GUI");
            minecraft.setScreen(new JobInfoScreen());
        }

        // C키 - 스탯 분배 GUI
        if (KeyBindings.OPEN_STATS_GUI.consumeClick()) {
            DungeonKemono.LOGGER.info("Opening Stats GUI");
            minecraft.setScreen(new StatsScreen());
        }
    }
}