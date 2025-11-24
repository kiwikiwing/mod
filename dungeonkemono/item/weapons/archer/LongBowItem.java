package com.kiwi.dungeonkemono.item.weapons.archer;

import com.kiwi.dungeonkemono.job.JobType;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;

/**
 * 궁수 전용 무기: 장궁
 * - 긴 사거리
 * - 높은 데미지
 * - 느린 장전
 */
public class LongBowItem extends BowItem {

    private final JobType requiredJob = JobType.ARCHER;

    public LongBowItem() {
        super(new Properties()
                .stacksTo(1)
                .durability(800));
    }

    public JobType getRequiredJob() {
        return requiredJob;
    }

    public boolean canUseSkills() {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, net.minecraft.world.level.Level level,
                                java.util.List<net.minecraft.network.chat.Component> tooltip,
                                net.minecraft.world.item.TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        tooltip.add(net.minecraft.network.chat.Component.literal(
                "§6필요 직업: §f" + requiredJob.getDisplayName()
        ));

        tooltip.add(net.minecraft.network.chat.Component.literal(
                "§7이 무기로 스킬 사용 가능"
        ));
    }
}