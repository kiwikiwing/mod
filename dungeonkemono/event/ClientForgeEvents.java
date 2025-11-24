package com.kiwi.dungeonkemono.event;

import com.kiwi.dungeonkemono.DungeonKemono;
import com.kiwi.dungeonkemono.client.ClickComboDetector;
import com.kiwi.dungeonkemono.client.KeyBindings;
import com.kiwi.dungeonkemono.client.gui.JobInfoScreen;
import com.kiwi.dungeonkemono.client.gui.StatsScreen;
import com.kiwi.dungeonkemono.network.PacketHandler;
import com.kiwi.dungeonkemono.network.UseSkillPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
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
            minecraft.setScreen(new JobInfoScreen(minecraft.player));
        }

        // C키 - 스탯 분배 GUI
        if (KeyBindings.OPEN_STATS_GUI.consumeClick()) {
            DungeonKemono.LOGGER.info("Opening Stats GUI");
            minecraft.setScreen(new StatsScreen());
        }

        // R키 - 스킬 4 (궁극기)
        if (KeyBindings.SKILL_4.consumeClick()) {
            DungeonKemono.LOGGER.info("Using Skill 4 (Ultimate) via R key");
            PacketHandler.INSTANCE.sendToServer(new UseSkillPacket(4));
        }
    }

    /**
     * 마우스 클릭 이벤트 (우클릭)
     */
    @SubscribeEvent
    public static void onMouseClick(InputEvent.InteractionKeyMappingTriggered event) {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;

        if (player == null || minecraft.screen != null) {
            return;
        }

        // DK 무기를 들고 있을 때만 스킬 활성화
        if (!ClickComboDetector.isHoldingDKWeapon(player)) {
            return;
        }

        // 우클릭 이벤트 (USE_ITEM)
        if (event.isUseItem()) {
            ClickComboDetector.recordRightClick();

            DungeonKemono.LOGGER.info("Right click detected with DK weapon");

            // Shift + 우클릭 = 스킬 3
            if (player.isShiftKeyDown()) {
                DungeonKemono.LOGGER.info("Using Skill 3 (Shift+RightClick)");
                PacketHandler.INSTANCE.sendToServer(new UseSkillPacket(3));
                event.setCanceled(true);
                event.setSwingHand(false);
                return;
            }

            // 좌+우 콤보 체크
            if (ClickComboDetector.isComboInput()) {
                DungeonKemono.LOGGER.info("Using Skill 2 (Combo: Left+Right)");
                PacketHandler.INSTANCE.sendToServer(new UseSkillPacket(2));
                ClickComboDetector.resetCombo();
                event.setCanceled(true);
                event.setSwingHand(false);
                return;
            }

            // 우클릭만 = 스킬 1
            DungeonKemono.LOGGER.info("Using Skill 1 (RightClick)");
            PacketHandler.INSTANCE.sendToServer(new UseSkillPacket(1));
            event.setCanceled(true);
            event.setSwingHand(false);
        }

        // 좌클릭 이벤트 (ATTACK)
        if (event.isAttack()) {
            ClickComboDetector.recordLeftClick();

            DungeonKemono.LOGGER.info("Left click detected");

            // 좌+우 콤보 체크
            if (ClickComboDetector.isComboInput()) {
                DungeonKemono.LOGGER.info("Using Skill 2 (Combo: Left+Right)");
                PacketHandler.INSTANCE.sendToServer(new UseSkillPacket(2));
                ClickComboDetector.resetCombo();
                event.setCanceled(true);
                event.setSwingHand(false);
                return;
            }

            // 좌클릭만 = 기본 공격 (바닐라 유지)
            DungeonKemono.LOGGER.info("Normal attack (vanilla)");
        }
    }
}