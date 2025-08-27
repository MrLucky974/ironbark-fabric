package io.github.mrlucky974.ironbark.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import io.github.mrlucky974.ironbark.network.BankUpdatePayload;
import io.github.mrlucky974.ironbark.world.IronbarkPersistentState;
import io.github.mrlucky974.ironbark.world.PlayerData;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class CoinsCommand {
    private static String formatBits(int amount) {
        return amount + " " + (amount == 1 ? "bit" : "bits");
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("coins")
                .requires(source -> source.hasPermissionLevel(2)) // OP only
                .then(CommandManager.literal("add")
                        .then(CommandManager.argument("player", net.minecraft.command.argument.EntityArgumentType.player())
                                .then(CommandManager.argument("amount", IntegerArgumentType.integer(1))
                                        .executes(ctx -> {
                                            ServerPlayerEntity player = net.minecraft.command.argument.EntityArgumentType.getPlayer(ctx, "player");
                                            int amount = IntegerArgumentType.getInteger(ctx, "amount");
                                            PlayerData playerState = IronbarkPersistentState.getPlayerState(player);
                                            playerState.coins += amount;

                                            ServerPlayNetworking.send(player, new BankUpdatePayload(playerState.coins));

                                            ctx.getSource().sendFeedback(() ->
                                                    net.minecraft.text.Text.literal("Added " + formatBits(amount) + " to " + player.getName().getString()), true);
                                            return 1;
                                        }))))
                .then(CommandManager.literal("remove")
                        .then(CommandManager.argument("player", net.minecraft.command.argument.EntityArgumentType.player())
                                .then(CommandManager.argument("amount", IntegerArgumentType.integer(1))
                                        .executes(ctx -> {
                                            ServerPlayerEntity player = net.minecraft.command.argument.EntityArgumentType.getPlayer(ctx, "player");
                                            int amount = IntegerArgumentType.getInteger(ctx, "amount");
                                            PlayerData playerState = IronbarkPersistentState.getPlayerState(player);

                                            if (playerState.coins < amount) {
                                                // Not enough coins, error out
                                                ctx.getSource().sendError(net.minecraft.text.Text.literal(
                                                        player.getName().getString() + " only has " + formatBits(playerState.coins) + " (cannot remove " + formatBits(amount) + ")."
                                                ));
                                                return 0; // signal failure
                                            }

                                            playerState.coins -= amount;
                                            ServerPlayNetworking.send(player, new BankUpdatePayload(playerState.coins));

                                            ctx.getSource().sendFeedback(() ->
                                                    net.minecraft.text.Text.literal("Removed " + formatBits(amount) + " from " + player.getName().getString()), true);

                                            return 1;
                                        }))))
                .then(CommandManager.literal("clear")
                        .then(CommandManager.argument("player", net.minecraft.command.argument.EntityArgumentType.player())
                                .executes(ctx -> {
                                    ServerPlayerEntity player = net.minecraft.command.argument.EntityArgumentType.getPlayer(ctx, "player");
                                    PlayerData playerState = IronbarkPersistentState.getPlayerState(player);
                                    int amount = playerState.coins;
                                    playerState.coins = 0;

                                    ServerPlayNetworking.send(player, new BankUpdatePayload(playerState.coins));

                                    ctx.getSource().sendFeedback(() ->
                                            net.minecraft.text.Text.literal("Cleared " + formatBits(amount) + " from " + player.getName().getString()), true);
                                    return 1;
                                })))
                .then(CommandManager.literal("get")
                        .then(CommandManager.argument("player", net.minecraft.command.argument.EntityArgumentType.player())
                                .executes(ctx -> {
                                    ServerPlayerEntity player = net.minecraft.command.argument.EntityArgumentType.getPlayer(ctx, "player");
                                    PlayerData playerState = IronbarkPersistentState.getPlayerState(player);
                                    int balance = playerState.coins;
                                    ctx.getSource().sendFeedback(() ->
                                            net.minecraft.text.Text.literal(player.getName().getString() + " has " + formatBits(balance) + "."), false);
                                    return balance;
                                })))
        );
    }
}
