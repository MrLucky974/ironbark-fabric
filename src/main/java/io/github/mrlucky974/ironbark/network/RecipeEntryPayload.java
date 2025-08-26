package io.github.mrlucky974.ironbark.network;

import io.github.mrlucky974.ironbark.Ironbark;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;

public abstract class RecipeEntryPayload<T extends Recipe<?>> implements CustomPayload {
    public static final Id<RecipeEntryPayload<?>> ID = new Id<>(Ironbark.id("recipe"));

    public final RecipeEntry<T> recipeEntry;

    public RecipeEntryPayload(RecipeEntry<T> recipeEntry) {
        this.recipeEntry = recipeEntry;
    }

    public RecipeEntry<T> getRecipeEntry() {
        return recipeEntry;
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    @SuppressWarnings("unchecked")
    protected static <T extends Recipe<?>> PacketCodec<RegistryByteBuf, RecipeEntry<T>> getTypedRecipeCodec() {
        return (PacketCodec<RegistryByteBuf, RecipeEntry<T>>) (PacketCodec<?, ?>) RecipeEntry.PACKET_CODEC;
    }
}
