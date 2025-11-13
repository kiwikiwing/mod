package com.kiwi.dungeonkemono.command;

import com.kiwi.dungeonkemono.job.JobType;
import com.kiwi.dungeonkemono.player.PlayerDataProvider;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

/**
 * 레벨 관련 명령어 (디버그용)
 * /dklevel set <값> - 레벨 설정
 */
public class LevelCommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        // /dklevel 명령어 빌더
        LiteralArgumentBuilder<CommandSourceStack> levelCommand = Commands.literal("dklevel")
                .requires(source -> source.hasPermission(2)); // OP 권한 필요

        // /dklevel set <값> - 레벨 설정
        levelCommand.then(Commands.literal("set")
                .then(Commands.argument("level", IntegerArgumentType.integer(1, 60))
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayerOrException();
                            int level = IntegerArgumentType.getInteger(context, "level");
                            return setLevel(player, level);
                        })));

        // 명령어 등록
        dispatcher.register(levelCommand);
    }

    /**
     * 레벨 설정
     */
    private static int setLevel(ServerPlayer player, int targetLevel) {
        player.getCapability(PlayerDataProvider.PLAYER_DATA).ifPresent(data -> {
            JobType currentJob = data.getCurrentJob();
            int oldLevel = data.getJobLevel(currentJob);

            // 현재 레벨과 같으면
            if (oldLevel == targetLevel) {
                player.sendSystemMessage(Component.literal("이미 Lv." + targetLevel + " 입니다!")
                        .withStyle(ChatFormatting.YELLOW));
                return;
            }

            // 레벨 설정
            data.getJobData(currentJob).setLevel(targetLevel);

            // 레벨 차이 계산
            int levelDiff = targetLevel - oldLevel;

            if (levelDiff > 0) {
                // 레벨업 한 경우 - 스탯 증가
                for (int i = 0; i < levelDiff; i++) {
                    // 직업별 고정 스탯 증가
                    data.getStats().addStrength(currentJob.getStrPerLevel());
                    data.getStats().addDexterity(currentJob.getDexPerLevel());
                    data.getStats().addVitality(currentJob.getVitPerLevel());
                    data.getStats().addIntelligence(currentJob.getIntPerLevel());
                    data.getStats().addLuck(currentJob.getLukPerLevel());

                    // 자유 포인트 추가
                    data.getStats().addFreePoints(5);
                }

                player.sendSystemMessage(Component.literal("레벨업!").withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD));
                player.sendSystemMessage(Component.literal("스탯이 증가했습니다! (+" + (levelDiff * 5) + " 자유 포인트)")
                        .withStyle(ChatFormatting.YELLOW));
            } else {
                // 레벨다운 한 경우 - 스탯 감소
                for (int i = 0; i < Math.abs(levelDiff); i++) {
                    data.getStats().addStrength(-currentJob.getStrPerLevel());
                    data.getStats().addDexterity(-currentJob.getDexPerLevel());
                    data.getStats().addVitality(-currentJob.getVitPerLevel());
                    data.getStats().addIntelligence(-currentJob.getIntPerLevel());
                    data.getStats().addLuck(-currentJob.getLukPerLevel());

                    // 자유 포인트 감소 (최소 0)
                    int currentFreePoints = data.getStats().getFreePoints();
                    data.getStats().setFreePoints(Math.max(0, currentFreePoints - 5));
                }

                player.sendSystemMessage(Component.literal("레벨다운!").withStyle(ChatFormatting.RED));
                player.sendSystemMessage(Component.literal("스탯이 감소했습니다.")
                        .withStyle(ChatFormatting.YELLOW));
            }

            // 결과 표시
            player.sendSystemMessage(Component.literal(String.format(
                    "%s: Lv.%d → Lv.%d",
                    currentJob.getDisplayName(),
                    oldLevel,
                    targetLevel
            )).withStyle(ChatFormatting.GOLD));

            // 경험치 초기화 (해당 레벨의 0 경험치로)
            data.getJobData(currentJob).setExperience(0);

            // 전직 가능 레벨 확인
            if (targetLevel == 10 || targetLevel == 20 || targetLevel == 30 || targetLevel == 40) {
                player.sendSystemMessage(Component.literal("★ 전직 가능 레벨입니다! ★")
                        .withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD));
            }

            // 클라이언트 동기화
            data.syncToClient(player);
        });

        return 1;
    }
}