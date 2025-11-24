package com.kiwi.dungeonkemono.skill.skills.warrior;

import com.kiwi.dungeonkemono.skill.Skill;
import com.kiwi.dungeonkemono.skill.SkillType;
import com.kiwi.dungeonkemono.stats.StatType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * 회오리 베기 (Whirlwind)
 * - 주변 모든 적에게 광역 데미지
 * - 힘 스탯 x 1.5 (3블록 범위)
 * - 쿨타임: 10초
 * - 필요 레벨: 15
 */
public class Whirlwind implements Skill {

    @Override
    public String getId() {
        return "whirlwind";
    }

    @Override
    public String getName() {
        return "회오리 베기";
    }

    @Override
    public String getDescription() {
        return "주변 모든 적에게 광역 데미지를 입힙니다.";
    }

    @Override
    public int getCooldown() {
        return 200; // 10초 (200틱)
    }

    @Override
    public int getManaCost() {
        return 30;
    }

    @Override
    public int getRequiredLevel() {
        return 15;
    }

    @Override
    public double getRange() {
        return 3.0; // 3블록 범위
    }

    @Override
    public SkillType getSkillType() {  // ✅ 추가!
        return SkillType.WHIRLWIND;
    }

    @Override
    public boolean canUse(ServerPlayer caster) {  // ✅ 매개변수 이름 통일
        return true; // 추가 조건 없음
    }

    @Override
    public boolean execute(ServerPlayer caster, LivingEntity target) {  // ✅ 매개변수 이름 통일
        ServerLevel level = (ServerLevel) caster.level();

        // 플레이어 위치 중심으로 광역 검색
        Vec3 playerPos = caster.position();
        AABB searchBox = new AABB(
                playerPos.x - getRange(),
                playerPos.y - 1,
                playerPos.z - getRange(),
                playerPos.x + getRange(),
                playerPos.y + 3,
                playerPos.z + getRange()
        );

        // 범위 내 모든 적 찾기
        List<LivingEntity> targets = level.getEntitiesOfClass(
                LivingEntity.class,
                searchBox,
                entity -> entity != caster && !entity.isDeadOrDying()
        );

        if (targets.isEmpty()) {
            caster.sendSystemMessage(
                    net.minecraft.network.chat.Component.literal("§c주변에 적이 없습니다!")
            );
            return false;
        }

        // 힘 스탯 가져오기
        int strength = caster.getCapability(
                com.kiwi.dungeonkemono.player.PlayerDataProvider.PLAYER_DATA
        ).map(data -> data.getStats().getStat(StatType.STRENGTH)).orElse(10);

        // 데미지 계산 (힘 x 1.5)
        float damage = strength * 1.5f;

        int hitCount = 0;

        // 모든 적에게 데미지
        for (LivingEntity entity : targets) {
            entity.hurt(
                    level.damageSources().playerAttack(caster),
                    damage
            );

            // 각 적 위치에 파티클
            Vec3 entityPos = entity.position();
            level.sendParticles(
                    ParticleTypes.SWEEP_ATTACK,
                    entityPos.x,
                    entityPos.y + entity.getBbHeight() / 2,
                    entityPos.z,
                    3, 0.3, 0.3, 0.3, 0.1
            );

            hitCount++;
        }

        // 중앙 회오리 파티클 (많이)
        for (int i = 0; i < 50; i++) {
            double angle = (i / 50.0) * Math.PI * 4; // 2바퀴
            double radius = getRange() * (i / 50.0);

            double offsetX = Math.cos(angle) * radius;
            double offsetZ = Math.sin(angle) * radius;

            level.sendParticles(
                    ParticleTypes.SWEEP_ATTACK,
                    playerPos.x + offsetX,
                    playerPos.y + 1,
                    playerPos.z + offsetZ,
                    1, 0, 0, 0, 0
            );
        }

        // 사운드
        level.playSound(
                null,
                caster.blockPosition(),
                SoundEvents.PLAYER_ATTACK_SWEEP,
                SoundSource.PLAYERS,
                1.0f,
                0.8f
        );

        // 메시지
        caster.sendSystemMessage(
                net.minecraft.network.chat.Component.literal(
                        String.format("§e[회오리 베기] §f%d명의 적에게 %.1f 데미지!",
                                hitCount, damage)
                )
        );

        return true;
    }
}