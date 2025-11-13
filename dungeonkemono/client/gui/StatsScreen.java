package com.kiwi.dungeonkemono.client.gui;

import com.kiwi.dungeonkemono.network.ApplyStatsPacket;
import com.kiwi.dungeonkemono.network.PacketHandler;
import com.kiwi.dungeonkemono.player.PlayerData;
import com.kiwi.dungeonkemono.player.PlayerDataProvider;
import com.kiwi.dungeonkemono.stats.StatType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class StatsScreen extends Screen {
    private final int GUI_WIDTH = 250;
    private final int GUI_HEIGHT = 220;
    private int leftPos;
    private int topPos;

    private PlayerData playerData;

    // 임시 스탯 (변경 중인 스탯)
    private int tempStr;
    private int tempDex;
    private int tempVit;
    private int tempInt;
    private int tempLuk;

    // 원본 스탯 (취소/초기화용)
    private int originalStr;
    private int originalDex;
    private int originalVit;
    private int originalInt;
    private int originalLuk;
    private int originalFreePoints;

    public StatsScreen() {
        super(Component.literal("스탯 분배"));
    }

    @Override
    protected void init() {
        super.init();

        this.leftPos = (this.width - GUI_WIDTH) / 2;
        this.topPos = (this.height - GUI_HEIGHT) / 2;

        // 플레이어 데이터 가져오기
        if (minecraft != null && minecraft.player != null) {
            minecraft.player.getCapability(PlayerDataProvider.PLAYER_DATA).ifPresent(data -> {
                this.playerData = data;

                // 원본 스탯 저장
                this.originalStr = data.getStats().getStat(StatType.STRENGTH);
                this.originalDex = data.getStats().getStat(StatType.DEXTERITY);
                this.originalVit = data.getStats().getStat(StatType.VITALITY);
                this.originalInt = data.getStats().getStat(StatType.INTELLIGENCE);
                this.originalLuk = data.getStats().getStat(StatType.LUCK);
                this.originalFreePoints = data.getStats().getFreePoints();

                // 임시 스탯 초기화
                this.tempStr = originalStr;
                this.tempDex = originalDex;
                this.tempVit = originalVit;
                this.tempInt = originalInt;
                this.tempLuk = originalLuk;
            });
        }

        createButtons();
    }

    private void createButtons() {
        int buttonY = topPos + 50;
        int spacing = 18;

        // 힘 (STR)
        createStatButtons(leftPos + 150, buttonY, () -> tempStr, v -> tempStr = v);
        buttonY += spacing;

        // 민첩 (DEX)
        createStatButtons(leftPos + 150, buttonY, () -> tempDex, v -> tempDex = v);
        buttonY += spacing;

        // 체력 (VIT)
        createStatButtons(leftPos + 150, buttonY, () -> tempVit, v -> tempVit = v);
        buttonY += spacing;

        // 지능 (INT)
        createStatButtons(leftPos + 150, buttonY, () -> tempInt, v -> tempInt = v);
        buttonY += spacing;

        // 행운 (LUK)
        createStatButtons(leftPos + 150, buttonY, () -> tempLuk, v -> tempLuk = v);

        // 하단 버튼들
        int bottomY = topPos + GUI_HEIGHT - 30;

        // 확인 버튼
        this.addRenderableWidget(
                Button.builder(Component.literal("확인"), button -> applyStats())
                        .bounds(leftPos + 10, bottomY, 70, 20)
                        .build()
        );

        // 취소 버튼
        this.addRenderableWidget(
                Button.builder(Component.literal("취소"), button -> this.onClose())
                        .bounds(leftPos + 90, bottomY, 70, 20)
                        .build()
        );

        // 초기화 버튼
        this.addRenderableWidget(
                Button.builder(Component.literal("초기화"), button -> resetStats())
                        .bounds(leftPos + 170, bottomY, 70, 20)
                        .build()
        );
    }

    private void createStatButtons(int x, int y, java.util.function.Supplier<Integer> getter, java.util.function.Consumer<Integer> setter) {
        // + 버튼
        this.addRenderableWidget(
                Button.builder(Component.literal("+"), button -> {
                            if (getUsedPoints() < originalFreePoints) {
                                setter.accept(getter.get() + 1);
                            }
                        })
                        .bounds(x, y, 20, 16)
                        .build()
        );

        // - 버튼
        this.addRenderableWidget(
                Button.builder(Component.literal("-"), button -> {
                            int current = getter.get();
                            int original = getOriginalStat(getter);
                            if (current > original) {
                                setter.accept(current - 1);
                            }
                        })
                        .bounds(x + 25, y, 20, 16)
                        .build()
        );
    }

    private int getOriginalStat(java.util.function.Supplier<Integer> getter) {
        if (getter.get() == tempStr) return originalStr;
        if (getter.get() == tempDex) return originalDex;
        if (getter.get() == tempVit) return originalVit;
        if (getter.get() == tempInt) return originalInt;
        if (getter.get() == tempLuk) return originalLuk;
        return 0;
    }

    private int getUsedPoints() {
        return (tempStr - originalStr) + (tempDex - originalDex) +
                (tempVit - originalVit) + (tempInt - originalInt) +
                (tempLuk - originalLuk);
    }

    private void resetStats() {
        this.tempStr = originalStr;
        this.tempDex = originalDex;
        this.tempVit = originalVit;
        this.tempInt = originalInt;
        this.tempLuk = originalLuk;
    }

    private void applyStats() {
        // 서버로 패킷 전송
        ApplyStatsPacket packet = new ApplyStatsPacket(tempStr, tempDex, tempVit, tempInt, tempLuk);
        PacketHandler.INSTANCE.sendToServer(packet);

        this.onClose();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);

        // GUI 배경
        guiGraphics.fill(leftPos, topPos, leftPos + GUI_WIDTH, topPos + GUI_HEIGHT, 0xC0101010);

        // 테두리
        guiGraphics.fill(leftPos, topPos, leftPos + GUI_WIDTH, topPos + 2, 0xFFFFFFFF);
        guiGraphics.fill(leftPos, topPos + GUI_HEIGHT - 2, leftPos + GUI_WIDTH, topPos + GUI_HEIGHT, 0xFFFFFFFF);
        guiGraphics.fill(leftPos, topPos, leftPos + 2, topPos + GUI_HEIGHT, 0xFFFFFFFF);
        guiGraphics.fill(leftPos + GUI_WIDTH - 2, topPos, leftPos + GUI_WIDTH, topPos + GUI_HEIGHT, 0xFFFFFFFF);

        if (playerData != null) {
            int yOffset = topPos + 10;

            // 제목
            guiGraphics.drawCenteredString(this.font, "§l§e스탯 분배", leftPos + GUI_WIDTH / 2, yOffset, 0xFFFFFF);
            yOffset += 20;

            // 자유 포인트
            int remainingPoints = originalFreePoints - getUsedPoints();
            guiGraphics.drawString(this.font, "§6자유 포인트: §f" + remainingPoints, leftPos + 10, yOffset, 0xFFFFFF);
            yOffset += 20;

            // 스탯 표시
            guiGraphics.drawString(this.font, "§c힘 (STR): §f" + tempStr, leftPos + 10, yOffset, 0xFFFFFF);
            yOffset += 18;

            guiGraphics.drawString(this.font, "§a민첩 (DEX): §f" + tempDex, leftPos + 10, yOffset, 0xFFFFFF);
            yOffset += 18;

            guiGraphics.drawString(this.font, "§9체력 (VIT): §f" + tempVit, leftPos + 10, yOffset, 0xFFFFFF);
            yOffset += 18;

            guiGraphics.drawString(this.font, "§d지능 (INT): §f" + tempInt, leftPos + 10, yOffset, 0xFFFFFF);
            yOffset += 18;

            guiGraphics.drawString(this.font, "§e행운 (LUK): §f" + tempLuk, leftPos + 10, yOffset, 0xFFFFFF);
            yOffset += 20;

            // 사용한 포인트
            int usedPoints = getUsedPoints();
            guiGraphics.drawString(this.font, "§7사용한 포인트: §f" + usedPoints, leftPos + 10, yOffset, 0xFFFFFF);
        }

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}