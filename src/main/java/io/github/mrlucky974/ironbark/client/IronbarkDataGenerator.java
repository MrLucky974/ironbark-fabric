package io.github.mrlucky974.ironbark.client;

import io.github.mrlucky974.ironbark.data.provider.IronbarkBlockTagProvider;
import io.github.mrlucky974.ironbark.data.provider.IronbarkItemTagProvider;
import io.github.mrlucky974.ironbark.data.provider.IronbarkModelProvider;
import io.github.mrlucky974.ironbark.data.provider.IronbarkRecipeProvider;
import io.github.mrlucky974.ironbark.data.provider.lang.IronbarkEnglishLanguageProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class IronbarkDataGenerator implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

        pack.addProvider(IronbarkModelProvider::new);
        pack.addProvider(IronbarkEnglishLanguageProvider::new);
        pack.addProvider(IronbarkRecipeProvider::new);
        IronbarkBlockTagProvider blockTagProvider = pack.addProvider(IronbarkBlockTagProvider::new);
        pack.addProvider((output, completableFuture) ->
                new IronbarkItemTagProvider(output, completableFuture, blockTagProvider));
    }
}
