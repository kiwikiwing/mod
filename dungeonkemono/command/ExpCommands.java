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
 * 경험치 관련 명령어 (디버그용)
 * /dkexp add <값> - 경험치 추가
 * /dkexp set <값> - 경험치 설정
 */
public class ExpCommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        // /dkexp 명령어 빌더
        LiteralArgumentBuilder<CommandSourceStack> expCommand = Commands.literal("dkexp")
                .requires(source -> source.hasPermission(2)); // OP 권한 필요

        // /dkexp add <값> - 경험치 추가
        expCommand.then(Commands.literal("add")
                .then(Commands.argument("amount", IntegerArgumentType.integer(1, 100000))
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayerOrException();
                            int amount = IntegerArgumentType.getInteger(context, "amount");
                            return addExp(player, amount);
                        })));

        // /dkexp set <값> - 경험치 설정
        expCommand.then(Commands.literal("set")
                .then(Commands.argument("amount", IntegerArgumentType.integer(0, 1000000))
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayerOrException();
                            int amount = IntegerArgumentType.getInteger(context, "amount");
                            return setExp(player, amount);
                        })));

        // 명령어 등록
        dispatcher.register(expCommand);
    }

    /**
     * 경험치 추가
     */
    private static int addExp(ServerPlayer player, int amount) {
        player.getCapability(PlayerDataProvider.PLAYER_DATA).ifPresent(data -> {
            JobType currentJob = data.getCurrentJob();
            int oldLevel = data.getJobLevel(currentJob);
            int oldExp = data.getJobExp(currentJob);

            // 경험치 추가
            data.addJobExp(currentJob, amount);

            int newLevel = data.getJobLevel(currentJob);
            int newExp = data.getJobExp(currentJob);

            // 결과 메시지
            player.sendSystemMessage(Component.literal("경험치 +" + amount)
                    .withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD));

            // 현재 상태 표시
            player.sendSystemMessage(Component.literal(String.format(
                    "%s: Lv.%d (%d/%d exp)",
                    currentJob.getDisplayName(),
                    newLevel,
                    newExp,
                    data.getExpForNextLevel(currentJob)
            )).withStyle(ChatFormatting.YELLOW));

            // 레벨업 했다면
            if (newLevel > oldLevel) {
                player.sendSystemMessage(Component.literal("레벨업! ")
                        .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD)
                        .append(Component.literal("Lv." + oldLevel + " → Lv." + newLevel)
                                .withStyle(ChatFormatting.YELLOW)));
            }

            // 클라이언트 동기화
            data.syncToClient(player);
        });

        return 1;
    }

    /**
     * 경험치 설정
     */
    private static int setExp(ServerPlayer player, int amount) {
        player.getCapability(PlayerDataProvider.PLAYER_DATA).ifPresent(data -> {
            JobType currentJob = data.getCurrentJob();
            int currentLevel = data.getJobLevel(currentJob);

            // 경험치 직접 설정
            data.getJobData(currentJob).setExperience(amount);

            int newLevel = data.getJobLevel(currentJob);

            // 결과 메시지
            player.sendSystemMessage(Component.literal("경험치 설정: " + amount)
                    .withStyle(ChatFormatting.YELLOW));

            // 레벨 변경 확인
            if (newLevel != currentLevel) {
                player.sendSystemMessage(Component.literal("레벨 변경: ")
                        .withStyle(ChatFormatting.GOLD)
                        .append(Component.literal("Lv." + currentLevel + " → Lv." + newLevel)
                                .withStyle(ChatFormatting.YELLOW)));
            }

            // 현재 상태 표시
            player.sendSystemMessage(Component.literal(String.format(
                    "%s: Lv.%d (%d/%d exp)",
                    currentJob.getDisplayName(),
                    newLevel,
                    data.getJobExp(currentJob),
                    data.getExpForNextLevel(currentJob)
            )).withStyle(ChatFormatting.AQUA));

            // 클라이언트 동기화
            data.syncToClient(player);
        });

        return 1;
    }
}