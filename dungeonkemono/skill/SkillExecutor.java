package com.kiwi.dungeonkemono.skill;

import com.kiwi.dungeonkemono.DungeonKemono;
import com.kiwi.dungeonkemono.job.JobType;
import com.kiwi.dungeonkemono.player.PlayerData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * 스킬 실행 시스템
 * 스킬 사용 가능 여부 체크 및 실행
 */
public class SkillExecutor {

    /**
     * 스킬 실행
     *
     * @param player 플레이어
     * @param data 플레이어 데이터
     * @param skillSlot 스킬 슬롯 (1~10)
     * @return 성공 여부
     */
    public static boolean executeSkill(ServerPlayer player, PlayerData data, int skillSlot) {
        // 현재 직업
        JobType currentJob = data.getCurrentJob();
        int currentLevel = data.getCurrentLevel();

        // 슬롯으로 스킬 찾기
        SkillType skillType = SkillType.getSkillBySlot(currentJob, skillSlot);

        if (skillType == null) {
            player.sendSystemMessage(
                    net.minecraft.network.chat.Component.literal("§c해당 슬롯에 스킬이 없습니다!")
            );
            return false;
        }

        // 스킬 가져오기
        Skill skill = SkillRegistry.getSkill(skillType);

        if (skill == null) {
            DungeonKemono.LOGGER.error("Skill not registered: {}", skillType.getId());
            player.sendSystemMessage(
                    net.minecraft.network.chat.Component.literal("§c스킬을 찾을 수 없습니다!")
            );
            return false;
        }

        // 레벨 체크
        if (currentLevel < skill.getRequiredLevel()) {
            player.sendSystemMessage(
                    net.minecraft.network.chat.Component.literal(
                            "§c" + skill.getName() + " 사용 불가! §7(필요 레벨: " +
                                    skill.getRequiredLevel() + ")"
                    )
            );
            return false;
        }

        // 쿨타임 체크
        long currentTick = player.level().getGameTime();
        CooldownManager cooldownManager = data.getCooldownManager();

        if (cooldownManager.isOnCooldown(skill.getId(), currentTick)) {
            float remaining = cooldownManager.getRemainingCooldown(skill.getId(), currentTick);
            player.sendSystemMessage(
                    net.minecraft.network.chat.Component.literal(
                            "§c" + skill.getName() + " 쿨타임: §e" +
                                    String.format("%.1f", remaining) + "초"
                    )
            );
            return false;
        }

        // 스킬 사용 가능 체크
        if (!skill.canUse(player)) {
            return false;
        }

        // 대상 찾기
        LivingEntity target = findTarget(player, skill.getRange());

        // 스킬 실행
        boolean success = skill.execute(player, target);

        if (success) {
            // 쿨타임 시작
            cooldownManager.startCooldown(skill.getId(), skill.getCooldown(), currentTick);

            DungeonKemono.LOGGER.debug("{} used skill: {} (slot {})",
                    player.getName().getString(),
                    skill.getName(),
                    skillSlot);
        }

        return success;
    }

    /**
     * 플레이어가 보고 있는 엔티티 찾기
     *
     * @param player 플레이어
     * @param range 사정거리
     * @return 가장 가까운 적대 엔티티 (없으면 null)
     */
    private static LivingEntity findTarget(ServerPlayer player, double range) {
        Vec3 eyePos = player.getEyePosition(1.0f);
        Vec3 lookVec = player.getLookAngle();
        Vec3 endPos = eyePos.add(lookVec.scale(range));

        // 범위 내 모든 엔티티 검색
        AABB searchBox = new AABB(eyePos, endPos).inflate(2.0);
        List<LivingEntity> entities = player.level().getEntitiesOfClass(
                LivingEntity.class,
                searchBox,
                entity -> entity != player && entity.isAlive()
        );

        if (entities.isEmpty()) {
            return null;
        }

        // 가장 가까운 엔티티 찾기
        LivingEntity closest = null;
        double closestDist = range;

        for (LivingEntity entity : entities) {
            double dist = player.distanceTo(entity);
            if (dist < closestDist) {
                // 시야각 체크 (플레이어가 보고 있는 방향)
                Vec3 toEntity = entity.position().subtract(eyePos).normalize();
                double dot = lookVec.dot(toEntity);

                if (dot > 0.5) { // 약 60도 시야각
                    closest = entity;
                    closestDist = dist;
                }
            }
        }

        return closest;
    }
}