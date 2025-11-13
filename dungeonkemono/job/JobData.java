package com.kiwi.dungeonkemono.job;

/**
 * 단일 직업의 레벨과 경험치 정보를 저장하는 클래스
 */
public class JobData {
    private JobType jobType;
    private int level = 1;
    private int experience = 0;

    /**
     * 기본 생성자 (BEGINNER로 초기화)
     */
    public JobData() {
        this(JobType.BEGINNER);
    }

    /**
     * JobType을 지정하는 생성자
     * @param jobType 직업 타입
     */
    public JobData(JobType jobType) {
        this.jobType = jobType;
        this.level = 1;
        this.experience = 0;
    }

    // Getters and Setters
    public JobType getJobType() {
        return jobType;
    }

    public void setJobType(JobType jobType) {
        this.jobType = jobType;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = Math.max(1, Math.min(60, level)); // 1~60 제한
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = Math.max(0, experience); // 음수 방지
    }

    /**
     * 경험치 추가
     * @param amount 추가할 경험치
     */
    public void addExperience(int amount) {
        this.experience = Math.max(0, this.experience + amount);
    }

    /**
     * 레벨 증가
     * @param amount 증가할 레벨
     */
    public void addLevel(int amount) {
        setLevel(this.level + amount);
    }

    /**
     * 다음 레벨까지 필요한 경험치 계산
     * @return 다음 레벨까지 필요한 총 경험치
     */
    public int getExpForNextLevel() {
        if (level >= 60) return Integer.MAX_VALUE; // 만렙
        return (int) (100 * Math.pow(level, 1.5));
    }
}