package com.kiwi.dungeonkemono.job;

import com.kiwi.dungeonkemono.stats.StatType;
import java.util.HashMap;
import java.util.Map;

/**
 * 4대 전투 직업 타입
 *
 * 각 직업은 레벨업 시 고정 스탯과 자유 포인트를 획득
 *
 * @author Kiwi
 */
public enum JobType {
    /**
     * 초보자
     * - 1~9레벨
     * - 10레벨에 1차 전직 가능
     */
    BEGINNER("초보자", 0,
            createStatMap(0, 0, 0, 0, 0), 5),

    /**
     * 전사
     * - 1차 전직 (Lv 10)
     * - 고정 스탯: 힘 +3, 체력 +2
     * - 자유 포인트: 5
     */
    WARRIOR("전사", 1,
            createStatMap(3, 0, 2, 0, 0), 5),

    /**
     * 궁수
     * - 1차 전직 (Lv 10)
     * - 고정 스탯: 민첩 +3, 행운 +1, 체력 +1
     * - 자유 포인트: 5
     */
    ARCHER("궁수", 1,
            createStatMap(0, 3, 1, 0, 1), 5),

    /**
     * 마법사
     * - 1차 전직 (Lv 10)
     * - 고정 스탯: 지능 +3, 체력 +1, 민첩 +1
     * - 자유 포인트: 5
     */
    MAGE("마법사", 1,
            createStatMap(0, 1, 1, 3, 0), 5),

    /**
     * 도적
     * - 1차 전직 (Lv 10)
     * - 고정 스탯: 민첩 +2, 행운 +2, 체력 +1
     * - 자유 포인트: 5
     */
    ROGUE("도적", 1,
            createStatMap(0, 2, 1, 0, 2), 5);

    // TODO: 2차, 3차, 4차 전직 추가 예정
    // 예시:
    // KNIGHT("기사", 2, ...), BERSERKER("버서커", 2, ...),
    // SNIPER("스나이퍼", 2, ...), ELEMENTALIST("원소술사", 2, ...),
    // ASSASSIN("암살자", 2, ...) 등

    private final String displayName;           // 한글 이름
    private final int tier;                     // 직업 등급 (0=초보자, 1=1차, 2=2차...)
    private final Map<StatType, Integer> fixedStats;  // 레벨업 시 자동 증가 스탯
    private final int freePoints;               // 레벨업 시 획득하는 자유 포인트

    /**
     * JobType 생성자
     *
     * @param displayName 표시용 한글 이름
     * @param tier 직업 등급 (0=초보자, 1=1차, 2=2차, 3=3차, 4=4차)
     * @param fixedStats 레벨업 시 자동 증가하는 스탯
     * @param freePoints 레벨업 시 획득하는 자유 포인트
     */
    JobType(String displayName, int tier, Map<StatType, Integer> fixedStats, int freePoints) {
        this.displayName = displayName;
        this.tier = tier;
        this.fixedStats = fixedStats;
        this.freePoints = freePoints;
    }

    /**
     * 한글 이름 반환
     *
     * @return 표시용 한글 이름
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * 직업 등급 반환
     *
     * @return 직업 등급 (0=초보자, 1=1차, 2=2차, 3=3차, 4=4차)
     */
    public int getTier() {
        return tier;
    }

    /**
     * 레벨업 시 자동 증가하는 스탯 반환
     *
     * @return 스탯 타입별 증가량 맵
     */
    public Map<StatType, Integer> getFixedStats() {
        return new HashMap<>(fixedStats);
    }

    /**
     * 레벨업 시 획득하는 자유 포인트 반환
     *
     * @return 자유 포인트 수
     */
    public int getFreePoints() {
        return freePoints;
    }

    /**
     * 특정 스탯의 레벨업 시 증가량 반환
     *
     * @param statType 조회할 스탯 타입
     * @return 해당 스탯의 증가량 (없으면 0)
     */
    public int getFixedStatValue(StatType statType) {
        return fixedStats.getOrDefault(statType, 0);
    }

    /**
     * 스탯 맵 생성 헬퍼 메서드
     *
     * @param str 힘 증가량
     * @param dex 민첩 증가량
     * @param vit 체력 증가량
     * @param intel 지능 증가량
     * @param luk 행운 증가량
     * @return 스탯 타입별 증가량 맵
     */
    private static Map<StatType, Integer> createStatMap(int str, int dex, int vit, int intel, int luk) {
        Map<StatType, Integer> map = new HashMap<>();
        map.put(StatType.STRENGTH, str);
        map.put(StatType.DEXTERITY, dex);
        map.put(StatType.VITALITY, vit);
        map.put(StatType.INTELLIGENCE, intel);
        map.put(StatType.LUCK, luk);
        return map;
    }

    /**
     * 스탯 설명 문자열 반환
     * 레벨업 시 증가하는 스탯을 설명 문자열로 반환
     */
    public String getStatDescription() {
        StringBuilder desc = new StringBuilder();
        if (getFixedStatValue(StatType.STRENGTH) > 0)
            desc.append("STR +").append(getFixedStatValue(StatType.STRENGTH)).append(" ");
        if (getFixedStatValue(StatType.DEXTERITY) > 0)
            desc.append("DEX +").append(getFixedStatValue(StatType.DEXTERITY)).append(" ");
        if (getFixedStatValue(StatType.VITALITY) > 0)
            desc.append("VIT +").append(getFixedStatValue(StatType.VITALITY)).append(" ");
        if (getFixedStatValue(StatType.INTELLIGENCE) > 0)
            desc.append("INT +").append(getFixedStatValue(StatType.INTELLIGENCE)).append(" ");
        if (getFixedStatValue(StatType.LUCK) > 0)
            desc.append("LUK +").append(getFixedStatValue(StatType.LUCK)).append(" ");
        desc.append("(자유 +").append(freePoints).append(")");
        return desc.toString();
    }

    // 명령어 시스템 호환을 위한 추가 메서드들 (이전 버전과의 호환성)
    public int getStrPerLevel() {
        return getFixedStatValue(StatType.STRENGTH);
    }

    public int getDexPerLevel() {
        return getFixedStatValue(StatType.DEXTERITY);
    }

    public int getVitPerLevel() {
        return getFixedStatValue(StatType.VITALITY);
    }

    public int getIntPerLevel() {
        return getFixedStatValue(StatType.INTELLIGENCE);
    }

    public int getLukPerLevel() {
        return getFixedStatValue(StatType.LUCK);
    }
}