package io.github.mrlucky974.ironbark.item;

import io.github.mrlucky974.ironbark.Ironbark;
import io.github.mrlucky974.ironbark.init.ComponentInit;
import io.github.mrlucky974.ironbark.init.RecipeInit;
import io.github.mrlucky974.ironbark.recipe.TabletCraftingRecipe;
import io.github.mrlucky974.ironbark.screen.CraftingTabletScreenHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class AncientClayTabletItem extends Item implements NamedScreenHandlerFactory {
    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();
    public static final Text INVALID_RECIPE_TOOLTIP_KEY =
            Text.translatable("item." + Ironbark.MOD_ID + ".ancient_clay_tablet.tooltip.invalid_recipe")
                    .formatted(Formatting.RED);

    public AncientClayTabletItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (hand.equals(Hand.OFF_HAND)) return TypedActionResult.pass(user.getStackInHand(hand));

        ItemStack itemStack = user.getStackInHand(hand);

        if (!world.isClient && user instanceof ServerPlayerEntity player) {
            player.openHandledScreen(this);
        }

        return TypedActionResult.success(itemStack);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);

        if (type.isCreative()) {
            addRecipeTooltip(stack, context, tooltip, Formatting.GOLD);
        } else {
            addRecipeTooltip(stack, context, tooltip, Formatting.DARK_PURPLE, Formatting.OBFUSCATED);
        }
    }

    public static Optional<RecipeEntry<TabletCraftingRecipe>> getRecipeEntry(World world, ItemStack stack) {
        return Optional.ofNullable(stack.get(ComponentInit.RECIPE_REFERENCE_COMPONENT))
                .map(component -> component.getRecipeEntry(RecipeInit.TypeInit.TABLET_CRAFTING, world));
    }

    private void addRecipeTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, Formatting... formatting) {
        Optional<RecipeEntry<TabletCraftingRecipe>> optionalRecipeEntry = Optional.ofNullable(CLIENT.world)
                .flatMap(world -> getRecipeEntry(world, stack));

        if (optionalRecipeEntry.isPresent()) {
            RecipeEntry<TabletCraftingRecipe> entry = optionalRecipeEntry.get();
            TabletCraftingRecipe recipe = entry.value();
            ItemStack result = recipe.getResult(context.getRegistryLookup());
            tooltip.add(result.getName().copy().formatted(formatting));
        } else {
            tooltip.add(INVALID_RECIPE_TOOLTIP_KEY);
        }
    }

    @Override
    public Text getDisplayName() {
        return getName();
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        ScreenHandlerContext context = ScreenHandlerContext.create(player.getWorld(), player.getBlockPos());
        return new CraftingTabletScreenHandler(syncId, playerInventory, context);
    }
}
