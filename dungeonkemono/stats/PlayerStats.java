package com.kiwi.dungeonkemono.stats;

import net.minecraft.nbt.CompoundTag;

/**
 * 플레이어 스탯 관리 클래스
 * 5대 기본 스탯과 자유 포인트를 관리
 */
public class PlayerStats {
    private int strength = 10;      // 힘
    private int dexterity = 10;     // 민첩
    private int vitality = 10;      // 체력
    private int intelligence = 10;  // 지능
    private int luck = 10;          // 행운
    private int freePoints = 0;     // 자유 포인트

    // ========== Getters ==========

    public int getStrength() {
        return strength;
    }

    public int getDexterity() {
        return dexterity;
    }

    public int getVitality() {
        return vitality;
    }

    public int getIntelligence() {
        return intelligence;
    }

    public int getLuck() {
        return luck;
    }

    public int getFreePoints() {
        return freePoints;
    }

    // ========== Setters ==========

    public void setStrength(int strength) {
        this.strength = Math.max(1, strength); // 최소값 1
    }

    public void setDexterity(int dexterity) {
        this.dexterity = Math.max(1, dexterity);
    }

    public void setVitality(int vitality) {
        this.vitality = Math.max(1, vitality);
    }

    public void setIntelligence(int intelligence) {
        this.intelligence = Math.max(1, intelligence);
    }

    public void setLuck(int luck) {
        this.luck = Math.max(1, luck);
    }

    public void setFreePoints(int freePoints) {
        this.freePoints = Math.max(0, freePoints); // 최소값 0
    }

    // ========== Add Methods (증가/감소) ==========

    public void addStrength(int amount) {
        setStrength(this.strength + amount);
    }

    public void addDexterity(int amount) {
        setDexterity(this.dexterity + amount);
    }

    public void addVitality(int amount) {
        setVitality(this.vitality + amount);
    }

    public void addIntelligence(int amount) {
        setIntelligence(this.intelligence + amount);
    }

    public void addLuck(int amount) {
        setLuck(this.luck + amount);
    }

    public void addFreePoints(int amount) {
        setFreePoints(this.freePoints + amount);
    }

    // ========== Utility Methods ==========

    /**
     * 특정 스탯 타입의 값 반환
     */
    public int getStatValue(StatType type) {
        return switch (type) {
            case STRENGTH -> strength;
            case DEXTERITY -> dexterity;
            case VITALITY -> vitality;
            case INTELLIGENCE -> intelligence;
            case LUCK -> luck;
        };
    }

    /**
     * 특정 스탯 타입의 값 반환 (명령어 시스템 호환용)
     * getStatValue()와 동일한 기능
     */
    public int getStat(StatType type) {
        return getStatValue(type);
    }

    /**
     * 특정 스탯 타입의 값 설정
     */
    public void setStatValue(StatType type, int value) {
        switch (type) {
            case STRENGTH -> setStrength(value);
            case DEXTERITY -> setDexterity(value);
            case VITALITY -> setVitality(value);
            case INTELLIGENCE -> setIntelligence(value);
            case LUCK -> setLuck(value);
        }
    }

    /**
     * 특정 스탯 타입의 값 설정 (명령어 시스템 호환용)
     * setStatValue()와 동일한 기능
     */
    public void setStat(StatType type, int value) {
        setStatValue(type, value);
    }

    /**
     * 특정 스탯 타입의 값 증가
     */
    public void addStatValue(StatType type, int amount) {
        switch (type) {
            case STRENGTH -> addStrength(amount);
            case DEXTERITY -> addDexterity(amount);
            case VITALITY -> addVitality(amount);
            case INTELLIGENCE -> addIntelligence(amount);
            case LUCK -> addLuck(amount);
        }
    }

    /**
     * 모든 스탯 초기화
     */
    public void reset() {
        strength = 10;
        dexterity = 10;
        vitality = 10;
        intelligence = 10;
        luck = 10;
        freePoints = 0;
    }

    /**
     * 총 스탯 포인트 계산 (자유 포인트 제외)
     */
    public int getTotalStats() {
        return strength + dexterity + vitality + intelligence + luck;
    }

    // ========== NBT Serialization ==========

    /**
     * NBT로 저장
     */
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("strength", strength);
        nbt.putInt("dexterity", dexterity);
        nbt.putInt("vitality", vitality);
        nbt.putInt("intelligence", intelligence);
        nbt.putInt("luck", luck);
        nbt.putInt("freePoints", freePoints);
        return nbt;
    }

    /**
     * NBT에서 로드
     */
    public void deserializeNBT(CompoundTag nbt) {
        strength = nbt.getInt("strength");
        dexterity = nbt.getInt("dexterity");
        vitality = nbt.getInt("vitality");
        intelligence = nbt.getInt("intelligence");
        luck = nbt.getInt("luck");
        freePoints = nbt.getInt("freePoints");

        // 기본값 보정 (0이면 10으로)
        if (strength == 0) strength = 10;
        if (dexterity == 0) dexterity = 10;
        if (vitality == 0) vitality = 10;
        if (intelligence == 0) intelligence = 10;
        if (luck == 0) luck = 10;
    }
}
