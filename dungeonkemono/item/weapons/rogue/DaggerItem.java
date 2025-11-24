package com.kiwi.dungeonkemono.item.weapons.rogue;

import com.kiwi.dungeonkemono.item.weapons.DKWeaponItem;
import com.kiwi.dungeonkemono.job.JobType;
import net.minecraft.world.item.Tiers;

/**
 * 도적 전용 무기: 단검
 * - 낮은 공격력
 * - 매우 빠른 공격 속도
 * - 크리티컬 특화
 */
public class DaggerItem extends DKWeaponItem {

    public DaggerItem() {
        super(
                Tiers.IRON,              // 티어
                3,                        // 낮은 공격력 (+3)
                -1.0f,                    // 빠른 공격 속도
                JobType.ROGUE,            // 도적 전용
                "dagger",                 // 무기 타입
                new Properties()
                        .stacksTo(1)
                        .durability(400)
        );
    }
}