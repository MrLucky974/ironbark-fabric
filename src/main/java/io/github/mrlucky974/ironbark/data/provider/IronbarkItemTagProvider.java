package io.github.mrlucky974.ironbark.data.provider;

import io.github.mrlucky974.ironbark.list.TagList;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryWrapper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class IronbarkItemTagProvider extends FabricTagProvider.ItemTagProvider {
    public IronbarkItemTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture, @Nullable BlockTagProvider blockTagProvider) {
        super(output, completableFuture, blockTagProvider);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getOrCreateTagBuilder(TagList.Items.SPICY_ITEM_BLACKLIST)
                .add(Items.GOLDEN_APPLE)
                .add(Items.ENCHANTED_GOLDEN_APPLE)
                .add(Items.SUSPICIOUS_STEW)
                .add(Items.MUSHROOM_STEW)
                .add(Items.HONEY_BOTTLE)
                .add(Items.MILK_BUCKET)
                .add(Items.POISONOUS_POTATO)
                .setReplace(true);
    }
}
