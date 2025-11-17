package com.kiwi.dungeonkemono.player;

import com.kiwi.dungeonkemono.job.JobData;
import com.kiwi.dungeonkemono.job.JobType;
import com.kiwi.dungeonkemono.stats.PlayerStats;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerPlayer;
import com.kiwi.dungeonkemono.network.PacketHandler;
import com.kiwi.dungeonkemono.network.SyncPlayerDataPacket;

import java.util.HashMap;
import java.util.Map;

/**
 * 플레이어 데이터 통합 관리 클래스
 * - 직업별 레벨/경험치
 * - 스탯
 * - 현재 선택된 직업
 */
public class PlayerData {
    
    private JobType currentJob = JobType.BEGINNER;  // 현재 선택된 직업
    private final Map<JobType, JobData> jobs = new HashMap<>();  // 직업별 데이터
    private final PlayerStats stats = new PlayerStats();  // 플레이어 스탯
    
    public PlayerData() {
        // 모든 직업 데이터 초기화
        for (JobType job : JobType.values()) {
            jobs.put(job, new JobData(job));
        }
        
        // 기본 스탯 설정 (각 10)
        stats.setStrength(10);
        stats.setDexterity(10);
        stats.setVitality(10);
        stats.setIntelligence(10);
        stats.setLuck(10);
    }
    
    // Getters and Setters
    public JobType getCurrentJob() {
        return currentJob;
    }
    
    public void setCurrentJob(JobType job) {
        this.currentJob = job;
    }
    
    public PlayerStats getStats() {
        return stats;
    }
    
    /**
     * 현재 직업의 레벨 반환
     */
    public int getCurrentLevel() {
        return jobs.get(currentJob).getLevel();
    }
    
    /**
     * 현재 직업의 경험치 반환
     */
    public int getCurrentExp() {
        return jobs.get(currentJob).getExperience();
    }
    
    /**
     * 현재 직업에 경험치 추가
     */
    public void addExperience(int amount) {
        JobData jobData = jobs.get(currentJob);
        int oldLevel = jobData.getLevel();
        
        jobData.addExperience(amount);
        
        // 레벨업 체크
        while (jobData.getExperience() >= getExpForNextLevel() && jobData.getLevel() < 60) {
            int nextLevelExp = getExpForNextLevel();
            jobData.addExperience(-nextLevelExp);
            jobData.setLevel(jobData.getLevel() + 1);
            onLevelUp();
        }
    }
    
    /**
     * 다음 레벨까지 필요한 경험치
     */
    public int getExpForNextLevel() {
        int level = getCurrentLevel();
        if (level >= 60) return Integer.MAX_VALUE;
        return (int) (100 * Math.pow(level, 1.5));
    }
    
    /**
     * 레벨업 시 처리
     */
    private void onLevelUp() {
        // 직업별 고정 스탯 증가
        stats.addStrength(currentJob.getStrPerLevel());
        stats.addDexterity(currentJob.getDexPerLevel());
        stats.addVitality(currentJob.getVitPerLevel());
        stats.addIntelligence(currentJob.getIntPerLevel());
        stats.addLuck(currentJob.getLukPerLevel());
        
        // 자유 포인트 추가
        stats.addFreePoints(5);
    }
    
    /**
     * NBT 저장
     */
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        
        // 현재 직업 저장
        nbt.putString("currentJob", currentJob.name());
        
        // 직업별 데이터 저장
        ListTag jobList = new ListTag();
        for (Map.Entry<JobType, JobData> entry : jobs.entrySet()) {
            CompoundTag jobTag = new CompoundTag();
            jobTag.putString("type", entry.getKey().name());
            jobTag.putInt("level", entry.getValue().getLevel());
            jobTag.putInt("exp", entry.getValue().getExperience());
            jobList.add(jobTag);
        }
        nbt.put("jobs", jobList);
        
        // 스탯 저장
        nbt.put("stats", stats.serializeNBT());
        
        return nbt;
    }
    
    /**
     * NBT 로드
     */
    public void deserializeNBT(CompoundTag nbt) {
        // 현재 직업 로드
        if (nbt.contains("currentJob")) {
            currentJob = JobType.valueOf(nbt.getString("currentJob"));
        }
        
        // 직업별 데이터 로드
        if (nbt.contains("jobs")) {
            ListTag jobList = nbt.getList("jobs", 10);
            for (int i = 0; i < jobList.size(); i++) {
                CompoundTag jobTag = jobList.getCompound(i);
                JobType type = JobType.valueOf(jobTag.getString("type"));
                JobData data = jobs.get(type);
                data.setLevel(jobTag.getInt("level"));
                data.setExperience(jobTag.getInt("exp"));
            }
        }
        
        // 스탯 로드
        if (nbt.contains("stats")) {
            stats.deserializeNBT(nbt.getCompound("stats"));
        }
    }
    
    /**
     * 현재 직업의 JobData 반환
     * @return 현재 직업의 JobData 객체
     */
    public JobData getCurrentJobData() {
        return jobs.get(currentJob);
    }
    
    // ========== 명령어 시스템을 위한 추가 메서드들 ==========
    
    /**
     * 특정 직업의 레벨 반환
     * @param jobType 조회할 직업
     * @return 해당 직업의 레벨
     */
    public int getJobLevel(JobType jobType) {
        return jobs.get(jobType).getLevel();
    }
    
    /**
     * 특정 직업의 경험치 반환
     * @param jobType 조회할 직업
     * @return 해당 직업의 현재 경험치
     */
    public int getJobExp(JobType jobType) {
        return jobs.get(jobType).getExperience();
    }
    
    /**
     * 특정 직업의 다음 레벨까지 필요한 경험치 반환
     * @param jobType 조회할 직업
     * @return 다음 레벨까지 필요한 경험치
     */
    public int getExpForNextLevel(JobType jobType) {
        int currentLevel = jobs.get(jobType).getLevel();
        if (currentLevel >= 60) return Integer.MAX_VALUE; // 만렙
        return (int) (100 * Math.pow(currentLevel, 1.5));
    }
    
    /**
     * 특정 직업의 경험치 추가
     * @param jobType 경험치를 추가할 직업
     * @param amount 추가할 경험치량
     */
    public void addJobExp(JobType jobType, int amount) {
        JobData jobData = jobs.get(jobType);
        int oldLevel = jobData.getLevel();
        
        // 경험치 추가
        jobData.addExperience(amount);
        
        // 레벨업 체크
        while (jobData.getExperience() >= getExpForNextLevel(jobType) && jobData.getLevel() < 60) {
            int nextLevelExp = getExpForNextLevel(jobType);
            jobData.addExperience(-nextLevelExp);
            jobData.setLevel(jobData.getLevel() + 1);
            
            // 스탯 증가 (직업별 고정 스탯)
            stats.addStrength(jobType.getStrPerLevel());
            stats.addDexterity(jobType.getDexPerLevel());
            stats.addVitality(jobType.getVitPerLevel());
            stats.addIntelligence(jobType.getIntPerLevel());
            stats.addLuck(jobType.getLukPerLevel());
            
            // 자유 포인트 추가
            stats.addFreePoints(5);
        }
    }
    
    /**
     * 특정 직업의 JobData 반환 (직접 수정용)
     * @param jobType 조회할 직업
     * @return JobData 객체
     */
    public JobData getJobData(JobType jobType) {
        return jobs.get(jobType);
    }
    
    /**
     * 클라이언트에 데이터 동기화
     * @param player 동기화할 플레이어
     */
    public void syncToClient(ServerPlayer player) {
        if (player != null && !player.level().isClientSide) {
            PacketHandler.sendToPlayer(new SyncPlayerDataPacket(this.serializeNBT()), player);
        }
    }
    
    /**
     * 다른 PlayerData로부터 데이터 복사 (사망 시 데이터 유지용)
     * @param other 복사할 원본 데이터
     */
    public void copyFrom(PlayerData other) {
        // 현재 직업 복사
        this.currentJob = other.currentJob;
        
        // 직업별 데이터 복사
        for (JobType job : JobType.values()) {
            JobData myJob = this.jobs.get(job);
            JobData otherJob = other.jobs.get(job);
            myJob.setLevel(otherJob.getLevel());
            myJob.setExperience(otherJob.getExperience());
        }
        
        // 스탯 복사
        this.stats.setStrength(other.stats.getStrength());
        this.stats.setDexterity(other.stats.getDexterity());
        this.stats.setVitality(other.stats.getVitality());
        this.stats.setIntelligence(other.stats.getIntelligence());
        this.stats.setLuck(other.stats.getLuck());
        this.stats.setFreePoints(other.stats.getFreePoints());
    }
}
