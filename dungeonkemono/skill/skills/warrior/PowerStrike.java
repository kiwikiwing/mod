package com.kiwi.dungeonkemono.skill.skills.warrior;

import com.kiwi.dungeonkemono.player.PlayerDataProvider;
import com.kiwi.dungeonkemono.skill.Skill;
import com.kiwi.dungeonkemono.skill.SkillType;
import com.kiwi.dungeonkemono.stats.StatType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

/**
 * 전사 스킬: 강타
 * - 레벨 1부터 사용 가능
 * - 대상에게 강력한 물리 데미지
 * - 힘 스탯에 비례한 데미지
 */
public class PowerStrike implements Skill {

    @Override
    public String getId() {
        return "power_strike";
    }

    @Override
    public String getName() {
        return "강타";
    }

    @Override
    public String getDescription() {
        return "대상에게 강력한 물리 데미지를 가합니다. (힘 x 2)";
    }

    @Override
    public int getCooldown() {
        return 60; // 3초 (60틱)
    }

    @Override
    public int getManaCost() {
        return 10;
    }

    @Override
    public int getRequiredLevel() {
        return 1;
    }

    @Override
    public double getRange() {
        return 3.0; // 3블록
    }

    @Override
    public SkillType getSkillType() {
        return SkillType.POWER_STRIKE;
    }

    @Override
    public boolean canUse(ServerPlayer caster) {
        // 마나 체크 (나중에 구현)
        // 쿨타임 체크는 SkillExecutor에서 처리
        return true;
    }

    @Override
    public boolean execute(ServerPlayer caster, LivingEntity target) {
        if (target == null || !target.isAlive()) {
            caster.sendSystemMessage(
                    net.minecraft.network.chat.Component.literal("§c대상이 없습니다!")
            );
            return false;
        }

        // 거리 체크
        if (caster.distanceTo(target) > getRange()) {
            caster.sendSystemMessage(
                    net.minecraft.network.chat.Component.literal("§c대상이 너무 멉니다!")
            );
            return false;
        }

        // 플레이어 스탯 가져오기
        return caster.getCapability(PlayerDataProvider.PLAYER_DATA).map(data -> {
            int strength = data.getStats().getStat(StatType.STRENGTH);

            // 데미지 계산: 힘 x 2
            float damage = strength * 2.0f;

            // 데미지 적용
            target.hurt(caster.damageSources().playerAttack(caster), damage);

            // 파티클 효과
            if (caster.level() instanceof ServerLevel serverLevel) {
                Vec3 targetPos = target.position();
                serverLevel.sendParticles(
                        ParticleTypes.CRIT,
                        targetPos.x, targetPos.y + 1, targetPos.z,
                        20, // 파티클 개수
                        0.5, 0.5, 0.5, // 퍼짐 범위
                        0.1 // 속도
                );
            }

            // 사운드 효과
            caster.level().playSound(
                    null,
                    caster.blockPosition(),
                    SoundEvents.PLAYER_ATTACK_CRIT,
                    SoundSource.PLAYERS,
                    1.0f,
                    1.0f
            );

            // 성공 메시지
            caster.sendSystemMessage(
                    net.minecraft.network.chat.Component.literal(
                            "§6[강타] §f" + target.getName().getString() +
                                    "에게 §c" + String.format("%.1f", damage) + " §f데미지!"
                    )
            );

            return true;
        }).orElse(false);
    }
}