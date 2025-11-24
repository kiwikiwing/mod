package com.kiwi.dungeonkemono.item.weapons;

import com.kiwi.dungeonkemono.job.JobType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.SwordItem;

/**
 * Dungeon & Kemono 커스텀 무기 기본 클래스
 * 이 무기를 들고 있을 때만 스킬 사용 가능
 */
public class DKWeaponItem extends SwordItem {

    private final JobType requiredJob;
    private final String weaponType; // "greatsword", "bow", "staff" 등

    /**
     * @param tier 무기 티어 (공격력, 내구도)
     * @param attackDamage 추가 공격력
     * @param attackSpeed 공격 속도
     * @param requiredJob 필요 직업 (null = 제한 없음)
     * @param weaponType 무기 타입
     */
    public DKWeaponItem(Tier tier, int attackDamage, float attackSpeed,
                        JobType requiredJob, String weaponType, Properties properties) {
        super(tier, attackDamage, attackSpeed, properties);
        this.requiredJob = requiredJob;
        this.weaponType = weaponType;
    }

    /**
     * 필요 직업 반환
     */
    public JobType getRequiredJob() {
        return requiredJob;
    }

    /**
     * 무기 타입 반환
     */
    public String getWeaponType() {
        return weaponType;
    }

    /**
     * 이 무기가 스킬 사용 가능한지
     */
    public boolean canUseSkills() {
        return true;
    }

    /**
     * 툴팁에 직업 제한 표시
     */
    @Override
    public void appendHoverText(ItemStack stack, net.minecraft.world.level.Level level,
                                java.util.List<net.minecraft.network.chat.Component> tooltip,
                                net.minecraft.world.item.TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        if (requiredJob != null) {
            tooltip.add(net.minecraft.network.chat.Component.literal(
                    "§6필요 직업: §f" + requiredJob.getDisplayName()
            ));
        }

        tooltip.add(net.minecraft.network.chat.Component.literal(
                "§7이 무기로 스킬 사용 가능"
        ));
    }
}