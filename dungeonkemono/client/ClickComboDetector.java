package com.kiwi.dungeonkemono.client;

import com.kiwi.dungeonkemono.item.weapons.DKWeaponItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;

/**
 * 마우스 클릭 콤보 감지기
 * DK 커스텀 무기만 체크
 */
public class ClickComboDetector {

    private static long lastLeftClickTime = 0;
    private static long lastRightClickTime = 0;
    private static final long COMBO_WINDOW = 100; // 0.1초

    public static void recordLeftClick() {
        lastLeftClickTime = System.currentTimeMillis();
    }

    public static void recordRightClick() {
        lastRightClickTime = System.currentTimeMillis();
    }

    public static boolean isComboInput() {
        long currentTime = System.currentTimeMillis();
        long leftDiff = currentTime - lastLeftClickTime;
        long rightDiff = currentTime - lastRightClickTime;

        return leftDiff < COMBO_WINDOW && rightDiff < COMBO_WINDOW;
    }

    public static void resetCombo() {
        lastLeftClickTime = 0;
        lastRightClickTime = 0;
    }

    /**
     * DK 커스텀 무기를 들고 있는지 체크
     */
    public static boolean isHoldingDKWeapon(Player player) {
        if (player == null) return false;

        ItemStack mainHand = player.getMainHandItem();
        Item item = mainHand.getItem();

        // DKWeaponItem 체크
        if (item instanceof DKWeaponItem) {
            return true;
        }

        // 커스텀 활 체크 (LongBowItem 등)
        String className = item.getClass().getSimpleName();
        if (className.equals("LongBowItem") ||
                className.equals("ShortBowItem") ||
                className.equals("HeavyCrossbowItem")) {
            return true;
        }

        // 마법사 무기 체크 (StaffItem 등)
        if (className.equals("StaffItem") ||
                className.equals("GrimoireItem")) {
            return true;
        }

        return false;
    }
}