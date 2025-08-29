package io.github.mrlucky974.ironbark.item;

import io.github.mrlucky974.ironbark.init.ComponentInit;
import io.github.mrlucky974.ironbark.init.ItemInit;
import io.github.mrlucky974.ironbark.network.BankUpdatePayload;
import io.github.mrlucky974.ironbark.network.CoinContainerComponent;
import io.github.mrlucky974.ironbark.world.IronbarkPersistentState;
import io.github.mrlucky974.ironbark.world.PlayerData;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ClickType;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;
import java.util.Objects;

public class CoinSackItem extends Item implements CurrencyProvider {
    public static final String COIN_SACK_TOOLTIP_KEY = "item.ironbark.coin_sack.tooltip";

    private static final List<CoinItem> coinTypes = List.of(
            ItemInit.ROSE_GOLD_COIN,
            ItemInit.GOLD_COIN,
            ItemInit.IRON_COIN,
            ItemInit.COPPER_COIN
    );

    public CoinSackItem(Settings settings) {
        super(settings);
    }

    @Override
    public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference) {
        World world = player.getWorld();
        if (world == null || world.isClient) return false;
        if (clickType != ClickType.RIGHT) return false;

        CoinContainerComponent component = stack.getOrDefault(ComponentInit.COINS, CoinContainerComponent.create(0));
        int totalCoins = component.getAmount();

        if (otherStack.isEmpty()) {
            for (CoinItem coin : coinTypes) {
                int coinValue = coin.getValue();
                int count = totalCoins / coinValue;
                if (count > 0) {
                    int stackCount = Math.min(count, coin.getMaxCount());
                    ItemStack coinStack = new ItemStack(coin, stackCount);

                    // Place coin stack on the cursor
                    cursorStackReference.set(coinStack);

                    totalCoins -= stackCount * coinValue;
                    break; // only give one stack per click
                }
            }

            // Update remaining coins in the sack
            component = CoinContainerComponent.create(totalCoins);
            stack.set(ComponentInit.COINS, component);
        } else {
            // Deposit coins into the sack
            if (!(otherStack.getItem() instanceof CoinItem coinItem)) return false;

            int amount = coinItem.getAmount(otherStack);
            component.add(amount);
            stack.set(ComponentInit.COINS, component);
            otherStack.decrement(otherStack.getCount());
        }

        return true;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stackInHand = user.getStackInHand(hand);

        if (user.isCreative()) {
            if (!world.isClient) {
                MinecraftServer server = world.getServer();
                PlayerData playerState = IronbarkPersistentState.getPlayerState(user);
                playerState.coins += getAmount(stackInHand);

                PlayerManager playerManager = Objects.requireNonNull(server).getPlayerManager();
                ServerPlayerEntity playerEntity = playerManager.getPlayer(user.getUuid());
                ServerPlayNetworking.send(Objects.requireNonNull(playerEntity), new BankUpdatePayload(playerState.coins));

                return TypedActionResult.success(stackInHand);
            }
        }

        return TypedActionResult.pass(stackInHand);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);

        tooltip.add(Text.translatable(COIN_SACK_TOOLTIP_KEY, getAmount(stack))
                .formatted(Formatting.GOLD, Formatting.ITALIC));
    }

    @Override
    public int getAmount(ItemStack stack) {
        CoinContainerComponent component = stack.getOrDefault(ComponentInit.COINS, CoinContainerComponent.create(0));
        return component.getAmount();
    }
}
