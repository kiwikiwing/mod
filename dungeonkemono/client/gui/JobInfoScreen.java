package com.kiwi.dungeonkemono.client.gui;

import com.kiwi.dungeonkemono.DungeonKemono;
import com.kiwi.dungeonkemono.player.PlayerData;
import com.kiwi.dungeonkemono.player.PlayerDataProvider;
import com.kiwi.dungeonkemono.stats.StatType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

/**
 * 직업 정보 GUI 화면
 * K키를 누르면 열리는 화면
 */
public class JobInfoScreen extends Screen {

    private final Player player;
    private PlayerData playerData;

    // 화면 크기
    private static final int GUI_WIDTH = 256;
    private static final int GUI_HEIGHT = 200;

    public JobInfoScreen(Player player) {
        super(Component.translatable("gui.dungeonkemono.job_info"));
        this.player = player;
    }

    @Override
    protected void init() {
        super.init();

        // 플레이어 데이터 가져오기
        player.getCapability(PlayerDataProvider.PLAYER_DATA).ifPresent(data -> {
            this.playerData = data;
        });

        // 데이터가 없으면 화면 닫기
        if (playerData == null) {
            this.onClose();
            return;
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // 배경 렌더링
        this.renderBackground(guiGraphics);

        // GUI 위치 계산 (중앙 정렬)
        int leftPos = (this.width - GUI_WIDTH) / 2;
        int topPos = (this.height - GUI_HEIGHT) / 2;

        // 반투명 검정 배경
        guiGraphics.fill(leftPos, topPos, leftPos + GUI_WIDTH, topPos + GUI_HEIGHT, 0xDD000000);

        // 테두리
        guiGraphics.fill(leftPos - 2, topPos - 2, leftPos + GUI_WIDTH + 2, topPos, 0xFF2B2B2B);
        guiGraphics.fill(leftPos - 2, topPos + GUI_HEIGHT, leftPos + GUI_WIDTH + 2, topPos + GUI_HEIGHT + 2, 0xFF2B2B2B);
        guiGraphics.fill(leftPos - 2, topPos, leftPos, topPos + GUI_HEIGHT, 0xFF2B2B2B);
        guiGraphics.fill(leftPos + GUI_WIDTH, topPos, leftPos + GUI_WIDTH + 2, topPos + GUI_HEIGHT, 0xFF2B2B2B);

        // 제목
        Component title = Component.literal("§6§l직업 정보");
        int titleWidth = this.font.width(title);
        guiGraphics.drawString(this.font, title, leftPos + (GUI_WIDTH - titleWidth) / 2, topPos + 10, 0xFFFFFF);

        int yOffset = topPos + 35;

        // 현재 직업 정보
        guiGraphics.drawString(this.font, "§e현재 직업: §f" + playerData.getCurrentJob().getDisplayName(), leftPos + 10, yOffset, 0xFFFFFF);
        yOffset += 15;

        guiGraphics.drawString(this.font, "§6레벨: §f" + playerData.getCurrentJobData().getLevel(), leftPos + 10, yOffset, 0xFFFFFF);
        yOffset += 15;

        // 경험치 바
        int currentExp = playerData.getCurrentJobData().getExperience();
        int maxExp = playerData.getCurrentJobData().getExpForNextLevel();
        float expProgress = (float) currentExp / maxExp;

        guiGraphics.drawString(this.font, "§a경험치: §f" + currentExp + " / " + maxExp, leftPos + 10, yOffset, 0xFFFFFF);
        yOffset += 12;

        // 경험치 바 그리기
        int barWidth = GUI_WIDTH - 20;
        int barHeight = 10;

        // 바 배경 (어두운 회색)
        guiGraphics.fill(leftPos + 10, yOffset, leftPos + 10 + barWidth, yOffset + barHeight, 0xFF2B2B2B);

        // 바 채우기 (초록색)
        int fillWidth = (int) (barWidth * expProgress);
        guiGraphics.fill(leftPos + 10, yOffset, leftPos + 10 + fillWidth, yOffset + barHeight, 0xFF00FF00);

        // 바 테두리
        guiGraphics.fill(leftPos + 9, yOffset - 1, leftPos + 11 + barWidth, yOffset, 0xFF000000);
        guiGraphics.fill(leftPos + 9, yOffset + barHeight, leftPos + 11 + barWidth, yOffset + barHeight + 1, 0xFF000000);
        guiGraphics.fill(leftPos + 9, yOffset, leftPos + 10, yOffset + barHeight, 0xFF000000);
        guiGraphics.fill(leftPos + 10 + barWidth, yOffset, leftPos + 11 + barWidth, yOffset + barHeight, 0xFF000000);

        yOffset += barHeight + 15;

        // 스탯 정보
        guiGraphics.drawString(this.font, "§e=== 스탯 ===", leftPos + 10, yOffset, 0xFFFFFF);
        yOffset += 12;

        guiGraphics.drawString(this.font, "§c힘 (STR): §f" + playerData.getStats().getStatValue(StatType.STRENGTH), leftPos + 15, yOffset, 0xFFFFFF);
        guiGraphics.drawString(this.font, "§b민첩 (DEX): §f" + playerData.getStats().getStatValue(StatType.DEXTERITY), leftPos + 130, yOffset, 0xFFFFFF);
        yOffset += 12;

        guiGraphics.drawString(this.font, "§a체력 (VIT): §f" + playerData.getStats().getStatValue(StatType.VITALITY), leftPos + 15, yOffset, 0xFFFFFF);
        guiGraphics.drawString(this.font, "§d지능 (INT): §f" + playerData.getStats().getStatValue(StatType.INTELLIGENCE), leftPos + 130, yOffset, 0xFFFFFF);
        yOffset += 12;

        guiGraphics.drawString(this.font, "§e행운 (LUK): §f" + playerData.getStats().getStatValue(StatType.LUCK), leftPos + 15, yOffset, 0xFFFFFF);
        guiGraphics.drawString(this.font, "§6자유 포인트: §f" + playerData.getStats().getFreePoints(), leftPos + 130, yOffset, 0xFFFFFF);

        // 하단 안내 메시지
        Component closeMsg = Component.literal("§7ESC로 닫기");
        int msgWidth = this.font.width(closeMsg);
        guiGraphics.drawString(this.font, closeMsg, leftPos + (GUI_WIDTH - msgWidth) / 2, topPos + GUI_HEIGHT - 15, 0xAAAAAA);

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false; // 게임을 일시정지하지 않음
    }
}