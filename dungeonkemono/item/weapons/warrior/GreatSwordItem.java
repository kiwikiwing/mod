package com.kiwi.dungeonkemono.item.weapons.warrior;

import com.kiwi.dungeonkemono.item.weapons.DKWeaponItem;
import com.kiwi.dungeonkemono.job.JobType;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;

/**
 * 전사 전용 무기: 대검
 * - 높은 공격력
 * - 느린 공격 속도
 * - 광역 공격 특화
 */
public class GreatSwordItem extends DKWeaponItem {

    public GreatSwordItem() {
        super(
                Tiers.IRON,              // 티어 (철 = 공격력 +6)
                8,                        // 추가 공격력 (+8)
                -3.0f,                    // 공격 속도 (-3.0 = 매우 느림)
                JobType.WARRIOR,          // 전사 전용
                "greatsword",             // 무기 타입
                new Properties()
                        .stacksTo(1)          // 1개만 들 수 있음
                        .durability(1000)     // 내구도 1000
        );
    }
}