package com.kiwi.dungeonkemono.skill;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;

/**
 * 스킬 인터페이스
 * 모든 스킬이 구현해야 하는 기본 메서드 정의
 *
 * @author Kiwi
 */
public interface Skill {

    /**
     * 스킬 ID (고유 식별자)
     * 예: "power_strike", "fireball"
     *
     * @return 스킬 ID
     */
    String getId();

    /**
     * 스킬 이름 (표시용 한글)
     * 예: "강타", "화염구"
     *
     * @return 스킬 이름
     */
    String getName();

    /**
     * 스킬 설명
     *
     * @return 스킬 설명
     */
    String getDescription();

    /**
     * 스킬 쿨타임 (틱 단위, 20틱 = 1초)
     * 예: 60 = 3초, 100 = 5초
     *
     * @return 쿨타임 (틱)
     */
    int getCooldown();

    /**
     * 스킬 마나 소모량
     * (현재는 미사용, 추후 마나 시스템 구현 시 사용)
     *
     * @return 마나 소모량
     */
    int getManaCost();

    /**
     * 스킬 사용 가능 최소 레벨
     *
     * @return 필요 레벨
     */
    int getRequiredLevel();

    /**
     * 스킬 사정거리 (블록 단위)
     * 0 = 자기 자신, 3 = 3블록, 999 = 무제한
     *
     * @return 사정거리
     */
    double getRange();

    /**
     * 스킬 타입 반환
     *
     * @return SkillType enum
     */
    SkillType getSkillType();

    /**
     * 스킬 실행
     *
     * @param caster 스킬 사용자
     * @param target 대상 엔티티 (없을 수 있음, null 허용)
     * @return 성공 여부
     */
    boolean execute(ServerPlayer caster, LivingEntity target);

    /**
     * 스킬 사용 조건 체크
     * (마나, 특수 조건 등)
     *
     * @param caster 스킬 사용자
     * @return 사용 가능 여부
     */
    boolean canUse(ServerPlayer caster);
}