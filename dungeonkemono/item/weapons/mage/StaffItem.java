package com.kiwi.dungeonkemono.item.weapons.mage;

import com.kiwi.dungeonkemono.job.JobType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * 마법사 전용 무기: 지팡이
 * - 마법 공격력 증가
 * - 마나 재생
 * - 원거리 마법 시전
 */
public class StaffItem extends Item {

    private final JobType requiredJob = JobType.MAGE;

    public StaffItem() {
        super(new Properties()
                .stacksTo(1)
                .durability(600));
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
                "§d마법 무기 - 스킬 사용 가능"
        ));
    }
}