package io.github.mrlucky974.ironbark.item;

import io.github.mrlucky974.ironbark.Ironbark;
import io.github.mrlucky974.ironbark.component.RecipeReferenceComponent;
import io.github.mrlucky974.ironbark.init.ComponentInit;
import io.github.mrlucky974.ironbark.init.RecipeInit;
import io.github.mrlucky974.ironbark.network.TabletCraftingRecipeEntryPayload;
import io.github.mrlucky974.ironbark.recipe.TabletCraftingRecipe;
import io.github.mrlucky974.ironbark.screen.CraftingTabletScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class AncientClayTabletItem extends Item implements ExtendedScreenHandlerFactory<TabletCraftingRecipeEntryPayload> {
    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();
    public static final Text INVALID_RECIPE_TOOLTIP =
            Text.translatable("item." + Ironbark.MOD_ID + ".ancient_clay_tablet.tooltip.invalid_recipe")
                    .formatted(Formatting.RED);

    public AncientClayTabletItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (hand.equals(Hand.OFF_HAND)) return TypedActionResult.pass(user.getStackInHand(hand));

        ItemStack itemStack = user.getStackInHand(hand);

        RecipeEntry<TabletCraftingRecipe> recipeEntry = getRecipeEntry(world, itemStack).orElse(null);
        if (recipeEntry != null) {
            if (!world.isClient && user instanceof ServerPlayerEntity player) {
                player.openHandledScreen(this);
            }
        } else {
            AncientClayTabletItem.breakTablet(itemStack, user);
            return TypedActionResult.fail(itemStack);
        }

        return TypedActionResult.success(itemStack);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);

        if (type.isCreative()) {
            addRecipeTooltip(stack, context, tooltip, type, Formatting.GOLD);
        } else {
            addRecipeTooltip(stack, context, tooltip, type, Formatting.DARK_PURPLE, Formatting.OBFUSCATED);
        }
    }

    public static void breakTablet(ItemStack itemStack, PlayerEntity player) {
        World world = player.getWorld();
        if (!itemStack.isEmpty()) {
            // Play breaking sound
            if (!player.isSilent()) {
                world.playSound(null, player.getX(), player.getY(), player.getZ(),
                        itemStack.getBreakSound(), player.getSoundCategory(), 1.0F, 1.0F);
            }

            // Spawn breaking particles on client side
            if (world.isClient) {
                spawnItemParticles(itemStack, player, 10);
            }

            itemStack.decrement(1);
        }
    }

    private static void spawnItemParticles(ItemStack stack, PlayerEntity player, int count) {
        World world = player.getWorld();
        Random random = player.getRandom();
        for(int i = 0; i < count; ++i) {
            Vec3d vec3d = new Vec3d(((double)random.nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, 0.0);
            vec3d = vec3d.rotateX(-player.getPitch() * 0.017453292F);
            vec3d = vec3d.rotateY(-player.getYaw() * 0.017453292F);
            double d = (double)(-random.nextFloat()) * 0.6 - 0.3;
            Vec3d vec3d2 = new Vec3d(((double)random.nextFloat() - 0.5) * 0.3, d, 0.6);
            vec3d2 = vec3d2.rotateX(-player.getPitch() * 0.017453292F);
            vec3d2 = vec3d2.rotateY(-player.getYaw() * 0.017453292F);
            vec3d2 = vec3d2.add(player.getX(), player.getEyeY(), player.getZ());
            world.addParticle(new ItemStackParticleEffect(ParticleTypes.ITEM, stack), vec3d2.x, vec3d2.y, vec3d2.z, vec3d.x, vec3d.y + 0.05, vec3d.z);
        }
    }

    public static Optional<RecipeEntry<TabletCraftingRecipe>> getRecipeEntry(World world, ItemStack stack) {
        return Optional.ofNullable(stack.get(ComponentInit.RECIPE_REFERENCE))
                .map(component -> component.getRecipeEntry(RecipeInit.TypeInit.TABLET_CRAFTING, world));
    }

    private void addRecipeTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type, Formatting... formatting) {
        Optional<RecipeEntry<TabletCraftingRecipe>> optionalRecipeEntry = Optional.ofNullable(CLIENT.world)
                .flatMap(world -> getRecipeEntry(world, stack));

        if (optionalRecipeEntry.isPresent()) {
            RecipeEntry<TabletCraftingRecipe> entry = optionalRecipeEntry.get();
            TabletCraftingRecipe recipe = entry.value();
            ItemStack result = recipe.getResult(context.getRegistryLookup());
            tooltip.add(result.getName().copy().formatted(formatting));
        } else {
            tooltip.add(INVALID_RECIPE_TOOLTIP);
        }

        if (type.isAdvanced()) {
            RecipeReferenceComponent component = stack.getOrDefault(ComponentInit.RECIPE_REFERENCE, null);
            if (component != null) {
                tooltip.add(Text.literal("Recipe: %s".formatted(component.toString())).formatted(Formatting.DARK_GRAY));
            } else {
                tooltip.add(Text.literal("No recipe reference found!").formatted(Formatting.RED));
            }
        }
    }

    @Override
    public Text getDisplayName() {
        return getName();
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        World world = player.getWorld();
        ScreenHandlerContext context = ScreenHandlerContext.create(world, player.getBlockPos());
        RecipeEntry<TabletCraftingRecipe> recipeEntry = getRecipeEntry(world, player.getMainHandStack()).orElse(null);
        return new CraftingTabletScreenHandler(syncId, playerInventory, context, recipeEntry);
    }

    @Override
    public TabletCraftingRecipeEntryPayload getScreenOpeningData(ServerPlayerEntity player) {
        ItemStack itemStack = player.getMainHandStack();
        Optional<RecipeEntry<TabletCraftingRecipe>> recipeEntry = getRecipeEntry(player.getWorld(), itemStack);
        return new TabletCraftingRecipeEntryPayload(recipeEntry.orElse(null));
    }
}
