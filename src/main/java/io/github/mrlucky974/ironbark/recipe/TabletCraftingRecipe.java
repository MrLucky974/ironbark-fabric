package io.github.mrlucky974.ironbark.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.mrlucky974.ironbark.recipe.input.TabletCraftingRecipeInput;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.*;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public record TabletCraftingRecipe(RawTabletShapedRecipe raw, ItemStack result) implements Recipe<TabletCraftingRecipeInput> {
    @Override
    public boolean matches(TabletCraftingRecipeInput input, World world) {
        return this.raw.matches(input);
    }

    @Override
    public ItemStack craft(TabletCraftingRecipeInput input, RegistryWrapper.WrapperLookup lookup) {
        return this.getResult(lookup).copy();
    }

    @Override
    public boolean fits(int width, int height) {
        return width >= this.raw.getWidth() && height >= this.raw.getHeight();
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup registriesLookup) {
        return this.result;
    }

    public boolean isEmpty() {
        DefaultedList<Ingredient> defaultedList = this.getIngredients();
        return defaultedList.isEmpty() || defaultedList.stream().filter((ingredient) -> !ingredient.isEmpty()).anyMatch((ingredient) -> ingredient.getMatchingStacks().length == 0);
    }

    @Override
    public DefaultedList<Ingredient> getIngredients() {
        return this.raw.getIngredients();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<TabletCraftingRecipe> {
        private Type() {}
        public static final Type INSTANCE = new Type();
        public static final String ID = "tablet_crafting";
    }

    public static class Serializer implements RecipeSerializer<TabletCraftingRecipe> {
        public static final Serializer INSTANCE = new Serializer();

        public static final MapCodec<TabletCraftingRecipe> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
                RawTabletShapedRecipe.CODEC.forGetter((recipe) -> recipe.raw),
                ItemStack.VALIDATED_CODEC.fieldOf("result").forGetter((recipe) -> recipe.result)
        ).apply(instance, TabletCraftingRecipe::new));
        public static final PacketCodec<RegistryByteBuf, TabletCraftingRecipe> PACKET_CODEC = PacketCodec.ofStatic(TabletCraftingRecipe.Serializer::write, TabletCraftingRecipe.Serializer::read);

        public MapCodec<TabletCraftingRecipe> codec() {
            return CODEC;
        }

        public PacketCodec<RegistryByteBuf, TabletCraftingRecipe> packetCodec() {
            return PACKET_CODEC;
        }

        private static TabletCraftingRecipe read(RegistryByteBuf buf) {
            RawTabletShapedRecipe rawShapedRecipe = RawTabletShapedRecipe.PACKET_CODEC.decode(buf);
            ItemStack itemStack = ItemStack.PACKET_CODEC.decode(buf);
            return new TabletCraftingRecipe(rawShapedRecipe, itemStack);
        }

        private static void write(RegistryByteBuf buf, TabletCraftingRecipe recipe) {
            RawTabletShapedRecipe.PACKET_CODEC.encode(buf, recipe.raw);
            ItemStack.PACKET_CODEC.encode(buf, recipe.result);
        }
    }
}
