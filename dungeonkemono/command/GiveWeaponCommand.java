package com.kiwi.dungeonkemono.command;

import com.kiwi.dungeonkemono.DungeonKemono;
import com.kiwi.dungeonkemono.item.DKItems;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

/**
 * 무기 지급 명령어 (테스트용)
 * /dkgive weapon <무기>
 */
public class GiveWeaponCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("dkgive")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("weapon")
                        .then(Commands.argument("weapon", StringArgumentType.word())
                                .suggests((context, builder) -> {
                                    builder.suggest("greatsword");
                                    builder.suggest("longbow");
                                    builder.suggest("staff");
                                    builder.suggest("dagger");
                                    return builder.buildFuture();
                                })
                                .executes(context -> {
                                    ServerPlayer player = context.getSource().getPlayerOrException();
                                    String weaponName = StringArgumentType.getString(context, "weapon");
                                    return giveWeapon(player, weaponName);
                                }))));
    }

    private static int giveWeapon(ServerPlayer player, String weaponName) {
        ItemStack weapon = switch (weaponName.toLowerCase()) {
            case "greatsword" -> new ItemStack(DKItems.GREATSWORD.get());
            case "longbow" -> new ItemStack(DKItems.LONGBOW.get());
            case "staff" -> new ItemStack(DKItems.STAFF.get());
            case "dagger" -> new ItemStack(DKItems.DAGGER.get());
            default -> ItemStack.EMPTY;
        };

        if (weapon.isEmpty()) {
            player.sendSystemMessage(Component.literal("§c알 수 없는 무기: " + weaponName)
                    .withStyle(ChatFormatting.RED));
            return 0;
        }

        player.getInventory().add(weapon);
        player.sendSystemMessage(Component.literal("§a" + weapon.getHoverName().getString() + " 지급!")
                .withStyle(ChatFormatting.GREEN));

        DungeonKemono.LOGGER.info("Gave {} to {}", weaponName, player.getName().getString());
        return 1;
    }
}