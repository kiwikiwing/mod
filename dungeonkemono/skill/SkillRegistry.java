package com.kiwi.dungeonkemono.skill;

import com.kiwi.dungeonkemono.DungeonKemono;
import com.kiwi.dungeonkemono.skill.skills.warrior.PowerStrike;
import com.kiwi.dungeonkemono.skill.skills.warrior.Whirlwind;

import java.util.HashMap;
import java.util.Map;

/**
 * 스킬 레지스트리
 * 모든 스킬을 등록하고 관리
 */
public class SkillRegistry {

    private static final Map<String, Skill> SKILLS = new HashMap<>();

    /**
     * 모든 스킬 등록
     */
    public static void registerSkills() {
        DungeonKemono.LOGGER.info("Registering skills...");

        // === 전사 스킬 ===
        register(new PowerStrike());
        register(new Whirlwind());
        // TODO: 나머지 전사 스킬 추가
        // register(new ShieldBash());
        // register(new Berserk());
        // register(new Whirlwind());

        // === 궁수 스킬 ===
        // TODO: 궁수 스킬 추가
        // register(new MultiShot());

        // === 마법사 스킬 ===
        // TODO: 마법사 스킬 추가
        // register(new Fireball());

        // === 도적 스킬 ===
        // TODO: 도적 스킬 추가
        // register(new Backstab());

        DungeonKemono.LOGGER.info("Registered {} skills", SKILLS.size());
    }

    /**
     * 스킬 등록
     */
    private static void register(Skill skill) {
        if (skill == null) {
            DungeonKemono.LOGGER.warn("Attempted to register null skill!");
            return;
        }

        String id = skill.getId();
        if (SKILLS.containsKey(id)) {
            DungeonKemono.LOGGER.warn("Duplicate skill ID: {}", id);
            return;
        }

        SKILLS.put(id, skill);
        DungeonKemono.LOGGER.debug("Registered skill: {} ({})", skill.getName(), id);
    }

    /**
     * 스킬 ID로 스킬 가져오기
     *
     * @param id 스킬 ID
     * @return 스킬 (없으면 null)
     */
    public static Skill getSkill(String id) {
        return SKILLS.get(id);
    }

    /**
     * SkillType으로 스킬 가져오기
     *
     * @param type 스킬 타입
     * @return 스킬 (없으면 null)
     */
    public static Skill getSkill(SkillType type) {
        if (type == null) {
            return null;
        }
        return SKILLS.get(type.getId());
    }

    /**
     * 등록된 모든 스킬 반환
     *
     * @return 스킬 맵 복사본
     */
    public static Map<String, Skill> getAllSkills() {
        return new HashMap<>(SKILLS);
    }

    /**
     * 등록된 스킬 개수
     *
     * @return 스킬 개수
     */
    public static int getSkillCount() {
        return SKILLS.size();
    }

    /**
     * 스킬이 등록되어 있는지 확인
     *
     * @param id 스킬 ID
     * @return 등록 여부
     */
    public static boolean hasSkill(String id) {
        return SKILLS.containsKey(id);
    }

    /**
     * 모든 스킬 초기화 (테스트용)
     */
    public static void clear() {
        SKILLS.clear();
        DungeonKemono.LOGGER.info("Cleared all skills");
    }
}