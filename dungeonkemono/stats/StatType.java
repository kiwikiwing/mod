package com.kiwi.dungeonkemono.stats;

/**
 * 5대 기본 스탯 타입
 * 
 * 각 스탯의 역할:
 * - STR (힘): 물리 공격력 증가
 * - DEX (민첩): 공격속도, 회피율 증가
 * - VIT (체력): HP, 방어력 증가
 * - INT (지능): 마법 공격력, 마나 증가
 * - LUK (행운): 크리티컬 확률 증가
 * 
 * @author Kiwi
 */
public enum StatType {
    /**
     * 힘 (Strength)
     * - 물리 공격력 증가
     * - 전사, 기사 계열에 중요
     */
    STRENGTH("힘", "STR"),
    
    /**
     * 민첩 (Dexterity)
     * - 공격속도 증가
     * - 회피율 증가
     * - 궁수, 도적 계열에 중요
     */
    DEXTERITY("민첩", "DEX"),
    
    /**
     * 체력 (Vitality)
     * - 최대 HP 증가
     * - 방어력 증가
     * - 모든 직업에 중요
     */
    VITALITY("체력", "VIT"),
    
    /**
     * 지능 (Intelligence)
     * - 마법 공격력 증가
     * - 최대 마나 증가
     * - 마법사 계열에 중요
     */
    INTELLIGENCE("지능", "INT"),
    
    /**
     * 행운 (Luck)
     * - 크리티컬 확률 증가
     * - 모든 직업에 유용
     */
    LUCK("행운", "LUK");
    
    private final String displayName;  // 한글 이름
    private final String shortName;    // 약어
    
    /**
     * StatType 생성자
     * 
     * @param displayName 표시용 한글 이름
     * @param shortName 약어 (STR, DEX 등)
     */
    StatType(String displayName, String shortName) {
        this.displayName = displayName;
        this.shortName = shortName;
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
     * 약어 반환
     * 
     * @return 약어 (STR, DEX 등)
     */
    public String getShortName() {
        return shortName;
    }
}
