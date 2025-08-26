package io.github.mrlucky974.ironbark.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public record RecipeReferenceComponent(Identifier recipeId) {
    public static final Codec<RecipeReferenceComponent> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Identifier.CODEC.fieldOf("recipe_id").forGetter(RecipeReferenceComponent::recipeId)
            ).apply(instance, RecipeReferenceComponent::new)
    );

    public static final PacketCodec<RegistryByteBuf, RecipeReferenceComponent> PACKET_CODEC =
            PacketCodec.tuple(
                    Identifier.PACKET_CODEC, RecipeReferenceComponent::recipeId,
                    RecipeReferenceComponent::new
            );

    public <I extends RecipeInput, T extends Recipe<I>> RecipeEntry<T> getRecipeEntry(RecipeType<T> type, World world) {
        return world.getRecipeManager()
                .listAllOfType(type)
                .stream()
                .filter(entry -> entry.id().equals(this.recipeId))
                .findFirst()
                .orElse(null);
    }
}
