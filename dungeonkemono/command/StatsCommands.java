package com.kiwi.dungeonkemono.command;

import com.kiwi.dungeonkemono.player.PlayerDataProvider;
import com.kiwi.dungeonkemono.stats.StatType;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
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
 * 스탯 관련 명령어
 * /dkstats reset - 스탯 초기화
 * /dkstats add <스탯> <값> - 스탯 강제 추가 (디버그)
 * /dkstats show - 현재 스탯 표시
 */
public class StatsCommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        // /dkstats 명령어 빌더
        LiteralArgumentBuilder<CommandSourceStack> statsCommand = Commands.literal("dkstats")
                .requires(source -> source.hasPermission(0)); // 기본 권한

        // /dkstats show - 현재 스탯 표시 (모든 플레이어)
        statsCommand.then(Commands.literal("show")
                .executes(context -> {
                    ServerPlayer player = context.getSource().getPlayerOrException();
                    return showStats(player);
                }));

        // /dkstats reset - 스탯 초기화 (OP 권한)
        statsCommand.then(Commands.literal("reset")
                .requires(source -> source.hasPermission(2))
                .executes(context -> {
                    ServerPlayer player = context.getSource().getPlayerOrException();
                    return resetStats(player);
                }));

        // /dkstats add <스탯> <값> - 스탯 강제 추가 (OP 권한)
        statsCommand.then(Commands.literal("add")
                .requires(source -> source.hasPermission(2))
                .then(Commands.argument("stat", StringArgumentType.word())
                        .suggests((context, builder) -> {
                            // 자동완성: 모든 스탯 이름 제안
                            Arrays.stream(StatType.values())
                                    .map(stat -> stat.name().toLowerCase())
                                    .forEach(builder::suggest);
                            builder.suggest("free"); // 자유 포인트도 추가
                            return builder.buildFuture();
                        })
                        .then(Commands.argument("amount", IntegerArgumentType.integer(-100, 100))
                                .executes(context -> {
                                    ServerPlayer player = context.getSource().getPlayerOrException();
                                    String statName = StringArgumentType.getString(context, "stat");
                                    int amount = IntegerArgumentType.getInteger(context, "amount");
                                    return addStat(player, statName, amount);
                                }))));

        // 명령어 등록
        dispatcher.register(statsCommand);
    }

    /**
     * 현재 스탯 표시
     */
    private static int showStats(ServerPlayer player) {
        player.getCapability(PlayerDataProvider.PLAYER_DATA).ifPresent(data -> {
            player.sendSystemMessage(Component.literal(""));
            player.sendSystemMessage(Component.literal("===== 스탯 정보 =====").withStyle(ChatFormatting.GOLD));

            // 기본 스탯
            player.sendSystemMessage(Component.literal("기본 스탯:").withStyle(ChatFormatting.YELLOW));
            player.sendSystemMessage(Component.literal(String.format(
                    "  STR: %d | DEX: %d | VIT: %d",
                    data.getStats().getStrength(),
                    data.getStats().getDexterity(),
                    data.getStats().getVitality()
            )).withStyle(ChatFormatting.WHITE));
            player.sendSystemMessage(Component.literal(String.format(
                    "  INT: %d | LUK: %d",
                    data.getStats().getIntelligence(),
                    data.getStats().getLuck()
            )).withStyle(ChatFormatting.WHITE));

            // 자유 포인트
            player.sendSystemMessage(Component.literal(""));
            player.sendSystemMessage(Component.literal("자유 포인트: ")
                    .withStyle(ChatFormatting.YELLOW)
                    .append(Component.literal(String.valueOf(data.getStats().getFreePoints()))
                            .withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD)));

            // 총 스탯 합계
            int totalStats = data.getStats().getStrength() +
                    data.getStats().getDexterity() +
                    data.getStats().getVitality() +
                    data.getStats().getIntelligence() +
                    data.getStats().getLuck();
            player.sendSystemMessage(Component.literal("총 스탯 포인트: ")
                    .withStyle(ChatFormatting.GRAY)
                    .append(Component.literal(String.valueOf(totalStats))
                            .withStyle(ChatFormatting.WHITE)));

            player.sendSystemMessage(Component.literal("==================").withStyle(ChatFormatting.GOLD));
        });

        return 1;
    }

    /**
     * 스탯 초기화
     */
    private static int resetStats(ServerPlayer player) {
        player.getCapability(PlayerDataProvider.PLAYER_DATA).ifPresent(data -> {
            // 현재 스탯 저장
            int totalStr = data.getStats().getStrength();
            int totalDex = data.getStats().getDexterity();
            int totalVit = data.getStats().getVitality();
            int totalInt = data.getStats().getIntelligence();
            int totalLuk = data.getStats().getLuck();

            // 기본 스탯으로 초기화 (레벨 1 기준)
            data.getStats().setStrength(10);
            data.getStats().setDexterity(10);
            data.getStats().setVitality(10);
            data.getStats().setIntelligence(10);
            data.getStats().setLuck(10);

            // 사용했던 포인트 계산
            int usedPoints = (totalStr - 10) + (totalDex - 10) +
                    (totalVit - 10) + (totalInt - 10) + (totalLuk - 10);

            // 자유 포인트로 환급
            int currentFreePoints = data.getStats().getFreePoints();
            data.getStats().setFreePoints(currentFreePoints + usedPoints);

            // 메시지
            player.sendSystemMessage(Component.literal(""));
            player.sendSystemMessage(Component.literal("스탯이 초기화되었습니다!")
                    .withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD));
            player.sendSystemMessage(Component.literal("모든 스탯이 10으로 리셋되었습니다.")
                    .withStyle(ChatFormatting.YELLOW));
            player.sendSystemMessage(Component.literal("환급된 포인트: ")
                    .withStyle(ChatFormatting.AQUA)
                    .append(Component.literal("+" + usedPoints)
                            .withStyle(ChatFormatting.GREEN)));
            player.sendSystemMessage(Component.literal("총 자유 포인트: ")
                    .withStyle(ChatFormatting.YELLOW)
                    .append(Component.literal(String.valueOf(data.getStats().getFreePoints()))
                            .withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD)));

            // 클라이언트 동기화
            data.syncToClient(player);
        });

        return 1;
    }

    /**
     * 스탯 강제 추가
     */
    private static int addStat(ServerPlayer player, String statName, int amount) {
        player.getCapability(PlayerDataProvider.PLAYER_DATA).ifPresent(data -> {
            boolean success = false;
            String statDisplayName = "";

            // 자유 포인트 처리
            if (statName.equalsIgnoreCase("free")) {
                data.getStats().addFreePoints(amount);
                statDisplayName = "자유 포인트";
                success = true;
            } else {
                // 일반 스탯 처리
                try {
                    StatType statType = StatType.valueOf(statName.toUpperCase());

                    switch (statType) {
                        case STRENGTH -> {
                            data.getStats().addStrength(amount);
                            statDisplayName = "STR";
                        }
                        case DEXTERITY -> {
                            data.getStats().addDexterity(amount);
                            statDisplayName = "DEX";
                        }
                        case VITALITY -> {
                            data.getStats().addVitality(amount);
                            statDisplayName = "VIT";
                        }
                        case INTELLIGENCE -> {
                            data.getStats().addIntelligence(amount);
                            statDisplayName = "INT";
                        }
                        case LUCK -> {
                            data.getStats().addLuck(amount);
                            statDisplayName = "LUK";
                        }
                    }
                    success = true;

                } catch (IllegalArgumentException e) {
                    // 잘못된 스탯 이름
                    player.sendSystemMessage(Component.literal("올바르지 않은 스탯 이름: " + statName)
                            .withStyle(ChatFormatting.RED));
                    player.sendSystemMessage(Component.literal("사용 가능: " +
                                    Arrays.stream(StatType.values())
                                            .map(stat -> stat.name().toLowerCase())
                                            .collect(Collectors.joining(", ")) + ", free")
                            .withStyle(ChatFormatting.YELLOW));
                }
            }

            if (success) {
                // 성공 메시지
                ChatFormatting color = amount > 0 ? ChatFormatting.GREEN : ChatFormatting.RED;
                String sign = amount > 0 ? "+" : "";

                player.sendSystemMessage(Component.literal(statDisplayName + " " + sign + amount)
                        .withStyle(color, ChatFormatting.BOLD));

                // 현재 값 표시
                showStats(player);

                // 클라이언트 동기화
                data.syncToClient(player);
            }
        });

        return 1;
    }
}