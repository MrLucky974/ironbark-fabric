package io.github.mrlucky974.ironbark.screen;

import io.github.mrlucky974.ironbark.block.entity.BankBlockEntity;
import io.github.mrlucky974.ironbark.init.BlockInit;
import io.github.mrlucky974.ironbark.init.ScreenHandlerTypeInit;
import io.github.mrlucky974.ironbark.item.CurrencyProvider;
import io.github.mrlucky974.ironbark.network.BankUpdatePayload;
import io.github.mrlucky974.ironbark.network.BlockPosPayload;
import io.github.mrlucky974.ironbark.screen.slot.CurrencyInputSlot;
import io.github.mrlucky974.ironbark.world.IronbarkPersistentState;
import io.github.mrlucky974.ironbark.world.PlayerData;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;

public class BankScreenHandler extends ScreenHandler {
    private final BankBlockEntity blockEntity;
    private final PlayerEntity player;
    private final ScreenHandlerContext context;
    private final Inventory inventory = new SimpleInventory(1);

    public BankScreenHandler(int syncId, PlayerInventory playerInventory, BlockPosPayload payload) {
        this(syncId, playerInventory, (BankBlockEntity)playerInventory.player.getWorld().getBlockEntity(payload.pos()));
    }

    public BankScreenHandler(int syncId, PlayerInventory playerInventory, BankBlockEntity blockEntity) {
        super(ScreenHandlerTypeInit.BANK, syncId);

        this.blockEntity = blockEntity;
        this.player = playerInventory.player;
        this.context = ScreenHandlerContext.create(this.blockEntity.getWorld(), this.blockEntity.getPos());

        this.addSlot(new CurrencyInputSlot(this.inventory, 0, 12, 20, this.player));

        int i;
        int j;
        for(i = 0; i < 3; ++i) {
            for(j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 66 + i * 18));
            }
        }

        for(i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 124));
        }
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slotIndex) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotIndex);

        if (slot != null && slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();

            // If clicking on currency slot (slot 0)
            if (slotIndex == 0) {
                // Try to move currency item back to player inventory
                if (!this.insertItem(originalStack, 1, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            }
            // If clicking on player inventory and item is currency
            else if (originalStack.getItem() instanceof CurrencyProvider currencyProvider) {
                // Handle currency deposit directly instead of using insertItem
                if (!player.getWorld().isClient()) {
                    handleCurrencyDeposit(originalStack, currencyProvider);
                }
                // Consume the entire stack
                originalStack.setCount(0);
                slot.setStack(ItemStack.EMPTY);
                return newStack;
            }
            // If clicking on non-currency item in player inventory
            else {
                // Don't allow moving non-currency items to currency slot
                return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }

            if (originalStack.getCount() == newStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTakeItem(player, originalStack);
        }

        return newStack;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return canUse(this.context, player, BlockInit.BANK);
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
