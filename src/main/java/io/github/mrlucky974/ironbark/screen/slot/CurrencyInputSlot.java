package io.github.mrlucky974.ironbark.screen.slot;

import io.github.mrlucky974.ironbark.Ironbark;
import io.github.mrlucky974.ironbark.item.CurrencyProvider;
import io.github.mrlucky974.ironbark.network.BankUpdatePayload;
import io.github.mrlucky974.ironbark.world.IronbarkPersistentState;
import io.github.mrlucky974.ironbark.world.PlayerData;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;

public class CurrencyInputSlot extends Slot {
    private final PlayerEntity player;

    public CurrencyInputSlot(Inventory inventory, int index, int x, int y, PlayerEntity player) {
        super(inventory, index, x, y);
        this.player = player;
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return stack.getItem() instanceof CurrencyProvider;
    }

    @Override
    public ItemStack insertStack(ItemStack stack, int count) {
        if (!stack.isEmpty() && stack.getItem() instanceof CurrencyProvider currencyProvider) {
            if (!player.getWorld().isClient()) {
                handleCurrencyDeposit(stack, currencyProvider);
            }

            stack.decrement(stack.getCount());
            return stack;
        } else {
            return stack;
        }
    }

    private void handleCurrencyDeposit(ItemStack stack, CurrencyProvider currencyProvider) {
        int amount = currencyProvider.getAmount(stack);

        // Get player state and add currency
        PlayerData playerState = IronbarkPersistentState.getPlayerState(player);
        playerState.coins += amount;

        // Send update packet to the player
        if (player instanceof ServerPlayerEntity serverPlayer) {
            ServerPlayNetworking.send(serverPlayer, new BankUpdatePayload(playerState.coins));
        }
    }
}
