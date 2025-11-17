package com.kiwi.dungeonkemono.command;

import com.kiwi.dungeonkemono.job.JobType;
import com.kiwi.dungeonkemono.player.PlayerData;
import com.kiwi.dungeonkemono.player.PlayerDataProvider;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * 직업 관련 명령어
 * /dkjob info - 현재 직업 정보
 * /dkjob list - 직업 목록
 * /dkjob change <직업> - 직업 변경
 */
public class JobCommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        // /dkjob 명령어 빌더
        LiteralArgumentBuilder<CommandSourceStack> jobCommand = Commands.literal("dkjob")
                .requires(source -> source.hasPermission(0)); // 모든 플레이어 사용 가능

        // /dkjob info - 현재 직업 정보 표시
        jobCommand.then(Commands.literal("info")
                .executes(context -> {
                    ServerPlayer player = context.getSource().getPlayerOrException();
                    return showJobInfo(player);
                }));

        // /dkjob list - 모든 직업 목록 표시
        jobCommand.then(Commands.literal("list")
                .executes(context -> {
                    ServerPlayer player = context.getSource().getPlayerOrException();
                    return showJobList(player);
                }));

        // /dkjob change <직업> - 직업 변경 (OP 권한 필요)
        jobCommand.then(Commands.literal("change")
                .requires(source -> source.hasPermission(2)) // OP 권한 필요
                .then(Commands.argument("job", StringArgumentType.word())
                        .suggests((context, builder) -> {
                            // 자동완성: 모든 직업 이름 제안
                            Arrays.stream(JobType.values())
                                    .map(job -> job.name().toLowerCase())
                                    .forEach(builder::suggest);
                            return builder.buildFuture();
                        })
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayerOrException();
                            String jobName = StringArgumentType.getString(context, "job");
                            return changeJob(player, jobName);
                        })));

        // 명령어 등록
        dispatcher.register(jobCommand);
    }

    /**
     * 현재 직업 정보 표시
     */
    private static int showJobInfo(ServerPlayer player) {
        player.getCapability(PlayerDataProvider.PLAYER_DATA).ifPresent(data -> {
            JobType currentJob = data.getCurrentJob();
            int level = data.getJobLevel(currentJob);
            int exp = data.getJobExp(currentJob);
            int nextExp = data.getExpForNextLevel(currentJob);

            // 정보 메시지 생성
            player.sendSystemMessage(Component.literal(""));
            player.sendSystemMessage(Component.literal("===== 직업 정보 =====").withStyle(ChatFormatting.GOLD));
            player.sendSystemMessage(Component.literal("현재 직업: ")
                    .withStyle(ChatFormatting.YELLOW)
                    .append(Component.literal(currentJob.getDisplayName())
                            .withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD)));
            player.sendSystemMessage(Component.literal("레벨: ")
                    .withStyle(ChatFormatting.YELLOW)
                    .append(Component.literal("Lv." + level)
                            .withStyle(ChatFormatting.AQUA)));
            player.sendSystemMessage(Component.literal("경험치: ")
                    .withStyle(ChatFormatting.YELLOW)
                    .append(Component.literal(exp + " / " + nextExp)
                            .withStyle(ChatFormatting.WHITE)));

            // 경험치 바 표시
            int percentage = (int) ((float) exp / nextExp * 100);
            player.sendSystemMessage(Component.literal("진행도: ")
                    .withStyle(ChatFormatting.YELLOW)
                    .append(Component.literal(percentage + "%")
                            .withStyle(ChatFormatting.GREEN)));

            // 스탯 표시
            player.sendSystemMessage(Component.literal(""));
            player.sendSystemMessage(Component.literal("--- 스탯 ---").withStyle(ChatFormatting.GOLD));
            player.sendSystemMessage(Component.literal(String.format(
                    "STR: %d | DEX: %d | VIT: %d | INT: %d | LUK: %d",
                    data.getStats().getStrength(),
                    data.getStats().getDexterity(),
                    data.getStats().getVitality(),
                    data.getStats().getIntelligence(),
                    data.getStats().getLuck()
            )).withStyle(ChatFormatting.WHITE));
            player.sendSystemMessage(Component.literal("자유 포인트: ")
                    .withStyle(ChatFormatting.YELLOW)
                    .append(Component.literal(String.valueOf(data.getStats().getFreePoints()))
                            .withStyle(ChatFormatting.GREEN)));
            player.sendSystemMessage(Component.literal("==================").withStyle(ChatFormatting.GOLD));
        });

        return 1; // 성공
    }

    /**
     * 직업 목록 표시
     */
    private static int showJobList(ServerPlayer player) {
        player.sendSystemMessage(Component.literal(""));
        player.sendSystemMessage(Component.literal("===== 직업 목록 =====").withStyle(ChatFormatting.GOLD));

        for (JobType job : JobType.values()) {
            // 직업별 색상 설정
            ChatFormatting color = switch (job) {
                case BEGINNER -> ChatFormatting.WHITE;
                case WARRIOR -> ChatFormatting.RED;
                case ARCHER -> ChatFormatting.GREEN;
                case MAGE -> ChatFormatting.BLUE;
                case ROGUE -> ChatFormatting.DARK_PURPLE;
            };

            // 직업 이름과 설명
            player.sendSystemMessage(Component.literal("• ")
                    .withStyle(ChatFormatting.GRAY)
                    .append(Component.literal(job.getDisplayName())
                            .withStyle(color, ChatFormatting.BOLD))
                    .append(Component.literal(" (" + job.name().toLowerCase() + ")")
                            .withStyle(ChatFormatting.GRAY)));

            // 레벨업 스탯 표시
            player.sendSystemMessage(Component.literal("  레벨업 보너스: ")
                    .withStyle(ChatFormatting.DARK_GRAY)
                    .append(Component.literal(job.getStatDescription())
                            .withStyle(ChatFormatting.GRAY)));
        }

        player.sendSystemMessage(Component.literal("==================").withStyle(ChatFormatting.GOLD));
        player.sendSystemMessage(Component.literal("Tip: /dkjob change <직업명>으로 직업 변경 (OP 권한 필요)")
                .withStyle(ChatFormatting.YELLOW));

        return 1;
    }

    /**
     * 직업 변경
     */
    private static int changeJob(ServerPlayer player, String jobName) {
        try {
            // 직업 이름으로 JobType 찾기
            JobType newJob = JobType.valueOf(jobName.toUpperCase());

            player.getCapability(PlayerDataProvider.PLAYER_DATA).ifPresent(data -> {
                JobType oldJob = data.getCurrentJob();

                // 같은 직업으로 변경 시도 시
                if (oldJob == newJob) {
                    player.sendSystemMessage(Component.literal("이미 " + newJob.getDisplayName() + " 직업입니다!")
                            .withStyle(ChatFormatting.RED));
                    return;
                }

                // 직업 변경
                data.setCurrentJob(newJob);
                data.syncToClient(player); // 클라이언트 동기화

                // 성공 메시지
                player.sendSystemMessage(Component.literal(""));
                player.sendSystemMessage(Component.literal("직업이 변경되었습니다!").withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD));
                player.sendSystemMessage(Component.literal(oldJob.getDisplayName())
                        .withStyle(ChatFormatting.GRAY)
                        .append(Component.literal(" → ")
                                .withStyle(ChatFormatting.WHITE))
                        .append(Component.literal(newJob.getDisplayName())
                                .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD)));

                // 새 직업 레벨 표시
                int level = data.getJobLevel(newJob);
                player.sendSystemMessage(Component.literal("현재 레벨: Lv." + level)
                        .withStyle(ChatFormatting.YELLOW));
            });

            return 1;

        } catch (IllegalArgumentException e) {
            // 잘못된 직업 이름
            player.sendSystemMessage(Component.literal("올바르지 않은 직업 이름입니다: " + jobName)
                    .withStyle(ChatFormatting.RED));
            player.sendSystemMessage(Component.literal("사용 가능한 직업: " +
                            Arrays.stream(JobType.values())
                                    .map(job -> job.name().toLowerCase())
                                    .collect(Collectors.joining(", ")))
                    .withStyle(ChatFormatting.YELLOW));
            return 0;
        }
    }
}