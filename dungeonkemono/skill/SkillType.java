package com.kiwi.dungeonkemono.skill;

import com.kiwi.dungeonkemono.job.JobType;

/**
 * 스킬 타입 (직업별 10개씩, 총 40개)
 *
 * 스킬 슬롯:
 * 1~4: 기본 스킬 (마우스)
 * 5~10: 확장 스킬 (키보드)
 */
public enum SkillType {

    // ========== 전사 스킬 (10개) ==========

    // 기본 스킬 (1~4)
    POWER_STRIKE("강타", "power_strike", JobType.WARRIOR, 1, 1),
    SHIELD_BASH("방패 강타", "shield_bash", JobType.WARRIOR, 5, 2),
    BERSERK("광폭화", "berserk", JobType.WARRIOR, 10, 3),
    WHIRLWIND("회오리 베기", "whirlwind", JobType.WARRIOR, 15, 4),

    // 확장 스킬 (5~10)
    IRON_SKIN("강철 피부", "iron_skin", JobType.WARRIOR, 20, 5),
    GROUND_SLAM("대지 강타", "ground_slam", JobType.WARRIOR, 25, 6),
    WAR_CRY("전쟁의 함성", "war_cry", JobType.WARRIOR, 30, 7),
    CHARGING_STRIKE("돌진 강타", "charging_strike", JobType.WARRIOR, 35, 8),
    TITAN_FORCE("타이탄의 힘", "titan_force", JobType.WARRIOR, 40, 9),
    FINAL_SLASH("최후의 일격", "final_slash", JobType.WARRIOR, 50, 10),


    // ========== 궁수 스킬 (10개) ==========

    // 기본 스킬 (1~4)
    MULTI_SHOT("다중 사격", "multi_shot", JobType.ARCHER, 1, 1),
    PIERCING_ARROW("관통 화살", "piercing_arrow", JobType.ARCHER, 5, 2),
    RAIN_OF_ARROWS("화살 비", "rain_of_arrows", JobType.ARCHER, 10, 3),
    SNIPE("저격", "snipe", JobType.ARCHER, 15, 4),

    // 확장 스킬 (5~10)
    EXPLOSIVE_ARROW("폭발 화살", "explosive_arrow", JobType.ARCHER, 20, 5),
    EAGLES_EYE("독수리의 눈", "eagles_eye", JobType.ARCHER, 25, 6),
    RAPID_FIRE("속사", "rapid_fire", JobType.ARCHER, 30, 7),
    POISON_ARROW("독 화살", "poison_arrow", JobType.ARCHER, 35, 8),
    PHOENIX_SHOT("불사조의 화살", "phoenix_shot", JobType.ARCHER, 40, 9),
    ARROW_STORM("화살 폭풍", "arrow_storm", JobType.ARCHER, 50, 10),


    // ========== 마법사 스킬 (10개) ==========

    // 기본 스킬 (1~4)
    FIREBALL("화염구", "fireball", JobType.MAGE, 1, 1),
    ICE_LANCE("얼음 창", "ice_lance", JobType.MAGE, 5, 2),
    LIGHTNING_BOLT("번개", "lightning_bolt", JobType.MAGE, 10, 3),
    METEOR("메테오", "meteor", JobType.MAGE, 15, 4),

    // 확장 스킬 (5~10)
    FLAME_WAVE("화염 파동", "flame_wave", JobType.MAGE, 20, 5),
    FROST_NOVA("냉기 폭발", "frost_nova", JobType.MAGE, 25, 6),
    CHAIN_LIGHTNING("연쇄 번개", "chain_lightning", JobType.MAGE, 30, 7),
    BLIZZARD("눈보라", "blizzard", JobType.MAGE, 35, 8),
    INFERNO("지옥불", "inferno", JobType.MAGE, 40, 9),
    ARCANE_BURST("비전 폭발", "arcane_burst", JobType.MAGE, 50, 10),


    // ========== 도적 스킬 (10개) ==========

    // 기본 스킬 (1~4)
    BACKSTAB("배후 습격", "backstab", JobType.ROGUE, 1, 1),
    SHADOW_STEP("그림자 이동", "shadow_step", JobType.ROGUE, 5, 2),
    POISON_BLADE("독 칼날", "poison_blade", JobType.ROGUE, 10, 3),
    ASSASSINATION("암살", "assassination", JobType.ROGUE, 15, 4),

    // 확장 스킬 (5~10)
    SMOKE_BOMB("연막탄", "smoke_bomb", JobType.ROGUE, 20, 5),
    BLADE_DANCE("검무", "blade_dance", JobType.ROGUE, 25, 6),
    SILENT_KILL("무음 살해", "silent_kill", JobType.ROGUE, 30, 7),
    SHADOW_CLONE("그림자 분신", "shadow_clone", JobType.ROGUE, 35, 8),
    DEATH_MARK("죽음의 표식", "death_mark", JobType.ROGUE, 40, 9),
    PHANTOM_STRIKE("환영 강타", "phantom_strike", JobType.ROGUE, 50, 10);


    // ========== 필드 & 생성자 ==========

    private final String displayName;  // 한글 이름
    private final String id;           // 고유 ID
    private final JobType requiredJob; // 필요 직업
    private final int unlockLevel;     // 해금 레벨
    private final int skillSlot;       // 스킬 슬롯 (1~10)

    /**
     * SkillType 생성자
     *
     * @param displayName 표시용 한글 이름
     * @param id 고유 식별자
     * @param requiredJob 필요 직업
     * @param unlockLevel 해금 레벨
     * @param skillSlot 스킬 슬롯 (1~10)
     */
    SkillType(String displayName, String id, JobType requiredJob,
              int unlockLevel, int skillSlot) {
        this.displayName = displayName;
        this.id = id;
        this.requiredJob = requiredJob;
        this.unlockLevel = unlockLevel;
        this.skillSlot = skillSlot;
    }


    // ========== Getters ==========

    /**
     * 한글 이름 반환
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * 고유 ID 반환
     */
    public String getId() {
        return id;
    }

    /**
     * 필요 직업 반환
     */
    public JobType getRequiredJob() {
        return requiredJob;
    }

    /**
     * 해금 레벨 반환
     */
    public int getUnlockLevel() {
        return unlockLevel;
    }

    /**
     * 스킬 슬롯 반환 (1~10)
     */
    public int getSkillSlot() {
        return skillSlot;
    }


    // ========== 유틸리티 메서드 ==========

    /**
     * 플레이어가 이 스킬을 사용할 수 있는지 체크
     *
     * @param currentJob 현재 직업
     * @param currentLevel 현재 레벨
     * @return 사용 가능 여부
     */
    public boolean canPlayerUse(JobType currentJob, int currentLevel) {
        return currentJob == requiredJob && currentLevel >= unlockLevel;
    }

    /**
     * 직업별 스킬 목록 가져오기
     *
     * @param job 직업
     * @return 해당 직업의 모든 스킬
     */
    public static SkillType[] getSkillsByJob(JobType job) {
        return java.util.Arrays.stream(values())
                .filter(skill -> skill.requiredJob == job)
                .toArray(SkillType[]::new);
    }

    /**
     * 스킬 슬롯으로 스킬 찾기
     *
     * @param job 직업
     * @param slot 슬롯 번호 (1~10)
     * @return 해당 슬롯의 스킬 (없으면 null)
     */
    public static SkillType getSkillBySlot(JobType job, int slot) {
        return java.util.Arrays.stream(values())
                .filter(skill -> skill.requiredJob == job && skill.skillSlot == slot)
                .findFirst()
                .orElse(null);
    }
}