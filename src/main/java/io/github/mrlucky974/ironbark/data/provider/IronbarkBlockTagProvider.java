package io.github.mrlucky974.ironbark.data.provider;

import io.github.mrlucky974.ironbark.init.BlockInit;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagEntry;

import java.util.concurrent.CompletableFuture;

public class IronbarkBlockTagProvider extends FabricTagProvider.BlockTagProvider {
    public IronbarkBlockTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getOrCreateTagBuilder(BlockTags.NEEDS_DIAMOND_TOOL)
                .add(BlockInit.NETHERIUM_BLOCK)
                .add(BlockInit.INDUSTRIAL_NETHERIUM_BLOCK);

        getOrCreateTagBuilder(BlockTags.NEEDS_IRON_TOOL)
                .add(BlockInit.STEEL_BLOCK);

        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE)
                .add(BlockInit.STEEL_BLOCK)
                .add(BlockInit.NETHERIUM_BLOCK)
                .add(BlockInit.INDUSTRIAL_NETHERIUM_BLOCK)
                .add(BlockInit.ANTHRACITE_COAL_BLOCK)
                .add(BlockInit.DEEPSLATE_ANTHRACITE_COAL_ORE);
    }
}
