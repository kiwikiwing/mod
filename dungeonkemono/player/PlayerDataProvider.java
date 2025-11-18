package com.kiwi.dungeonkemono.player;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * PlayerData Capability Provider
 * 
 * 역할:
 * - 플레이어 엔티티에 PlayerData를 첨부
 * - NBT 저장/로드 처리
 * - LazyOptional로 안전한 접근 제공
 * 
 * @author Kiwi
 */
public class PlayerDataProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    
    /**
     * PlayerData Capability
     * PlayerDataAttacher에서 등록됨
     */
    public static Capability<PlayerData> PLAYER_DATA = null;
    
    // 실제 PlayerData 인스턴스
    private PlayerData playerData = null;
    
    // LazyOptional 래퍼 (안전한 접근)
    private final LazyOptional<PlayerData> optional = LazyOptional.of(this::getOrCreatePlayerData);
    
    /**
     * PlayerData 생성 또는 반환
     * 
     * @return PlayerData 인스턴스
     */
    private PlayerData getOrCreatePlayerData() {
        if (this.playerData == null) {
            this.playerData = new PlayerData();
        }
        return this.playerData;
    }
    
    /**
     * Capability 제공
     * 
     * @param cap 요청된 Capability
     * @param side 방향 (사용 안 함)
     * @return LazyOptional로 래핑된 Capability
     */
    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == PLAYER_DATA) {
            return optional.cast();
        }
        return LazyOptional.empty();
    }
    
    /**
     * NBT에 저장
     * 
     * @return 저장된 CompoundTag
     */
    @Override
    public CompoundTag serializeNBT() {
        return getOrCreatePlayerData().serializeNBT();
    }
    
    /**
     * NBT에서 로드
     * 
     * @param nbt 로드할 CompoundTag
     */
    @Override
    public void deserializeNBT(CompoundTag nbt) {
        getOrCreatePlayerData().deserializeNBT(nbt);
    }
}
