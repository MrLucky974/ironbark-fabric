package io.github.mrlucky974.ironbark.item;

import io.github.mrlucky974.ironbark.network.BankUpdatePayload;
import io.github.mrlucky974.ironbark.world.IronbarkPersistentState;
import io.github.mrlucky974.ironbark.world.PlayerData;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;
import java.util.Objects;

public class CoinItem extends Item implements CurrencyProvider {
    public static final String COIN_TOOLTIP_KEY = "item.ironbark.coin.tooltip";
    private final int value;

    public CoinItem(int value, Settings settings) {
        super(settings);
        this.value = value;
    }

    public int getValue() {
        return this.value;
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

        tooltip.add(Text.translatable(COIN_TOOLTIP_KEY, getAmount(stack))
                .formatted(Formatting.GOLD, Formatting.ITALIC));
    }

    @Override
    public int getAmount(ItemStack stack) {
        return this.value * stack.getCount();
    }
}
