package com.kiwi.dungeonkemono.skill;

import net.minecraft.nbt.CompoundTag;

import java.util.HashMap;
import java.util.Map;

/**
 * 스킬 쿨타임 관리 클래스
 * 각 플레이어의 스킬 쿨타임을 추적
 *
 * @author Kiwi
 */
public class CooldownManager {

    // 스킬 ID -> 쿨타임 종료 시간 (게임 틱)
    private final Map<String, Long> cooldowns = new HashMap<>();

    /**
     * 쿨타임 시작
     *
     * @param skillId 스킬 ID
     * @param cooldownTicks 쿨타임 (틱)
     * @param currentTick 현재 게임 틱
     */
    public void startCooldown(String skillId, int cooldownTicks, long currentTick) {
        cooldowns.put(skillId, currentTick + cooldownTicks);
    }

    /**
     * 쿨타임 중인지 체크
     *
     * @param skillId 스킬 ID
     * @param currentTick 현재 게임 틱
     * @return 쿨타임 중이면 true
     */
    public boolean isOnCooldown(String skillId, long currentTick) {
        if (!cooldowns.containsKey(skillId)) {
            return false;
        }
        return cooldowns.get(skillId) > currentTick;
    }

    /**
     * 남은 쿨타임 (초)
     *
     * @param skillId 스킬 ID
     * @param currentTick 현재 게임 틱
     * @return 남은 쿨타임 (초), 쿨타임 아니면 0
     */
    public float getRemainingCooldown(String skillId, long currentTick) {
        if (!isOnCooldown(skillId, currentTick)) {
            return 0;
        }
        long remainingTicks = cooldowns.get(skillId) - currentTick;
        return remainingTicks / 20.0f; // 틱을 초로 변환
    }

    /**
     * 남은 쿨타임 (틱)
     *
     * @param skillId 스킬 ID
     * @param currentTick 현재 게임 틱
     * @return 남은 쿨타임 (틱)
     */
    public long getRemainingTicks(String skillId, long currentTick) {
        if (!isOnCooldown(skillId, currentTick)) {
            return 0;
        }
        return cooldowns.get(skillId) - currentTick;
    }

    /**
     * 모든 쿨타임 초기화
     */
    public void clearAll() {
        cooldowns.clear();
    }

    /**
     * 특정 스킬 쿨타임 초기화
     *
     * @param skillId 스킬 ID
     */
    public void clearCooldown(String skillId) {
        cooldowns.remove(skillId);
    }

    /**
     * 쿨타임 즉시 완료 (치트/테스트용)
     *
     * @param skillId 스킬 ID
     * @param currentTick 현재 게임 틱
     */
    public void finishCooldown(String skillId, long currentTick) {
        cooldowns.put(skillId, currentTick);
    }

    /**
     * NBT 저장
     *
     * @return CompoundTag
     */
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        for (Map.Entry<String, Long> entry : cooldowns.entrySet()) {
            nbt.putLong(entry.getKey(), entry.getValue());
        }
        return nbt;
    }

    /**
     * NBT 로드
     *
     * @param nbt CompoundTag
     */
    public void deserializeNBT(CompoundTag nbt) {
        cooldowns.clear();
        for (String key : nbt.getAllKeys()) {
            cooldowns.put(key, nbt.getLong(key));
        }
    }

    /**
     * 디버그용: 모든 쿨타임 정보
     *
     * @return 쿨타임 맵 복사본
     */
    public Map<String, Long> getAllCooldowns() {
        return new HashMap<>(cooldowns);
    }

    /**
     * 쿨타임 개수
     *
     * @return 활성화된 쿨타임 개수
     */
    public int getCooldownCount() {
        return cooldowns.size();
    }
}