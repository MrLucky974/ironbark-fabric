package io.github.mrlucky974.ironbark.data.provider;

import io.github.mrlucky974.ironbark.init.BlockInit;
import io.github.mrlucky974.ironbark.init.ItemInit;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;
import net.minecraft.data.client.TexturedModel;

public class IronbarkModelProvider extends FabricModelProvider {
    public IronbarkModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        blockStateModelGenerator.registerSimpleCubeAll(BlockInit.DEEPSLATE_ANTHRACITE_COAL_ORE);
        blockStateModelGenerator.registerSimpleCubeAll(BlockInit.ANTHRACITE_COAL_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(BlockInit.NETHERIUM_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(BlockInit.STEEL_BLOCK);
        blockStateModelGenerator.registerSingleton(BlockInit.INDUSTRIAL_NETHERIUM_BLOCK, TexturedModel.END_FOR_TOP_CUBE_COLUMN);
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.register(ItemInit.STEEL_INGOT, Models.GENERATED);
        itemModelGenerator.register(ItemInit.FLOUR, Models.GENERATED);
        itemModelGenerator.register(ItemInit.DOUGH, Models.GENERATED);
        itemModelGenerator.register(ItemInit.BARK, Models.GENERATED);
        itemModelGenerator.register(ItemInit.JUNGLE_SAP, Models.GENERATED);
        itemModelGenerator.register(ItemInit.SPRUCE_SAP, Models.GENERATED);
        itemModelGenerator.register(ItemInit.MULCH, Models.GENERATED);
        itemModelGenerator.register(ItemInit.ANTHRACITE_COAL, Models.GENERATED);
        itemModelGenerator.register(ItemInit.NETHERIUM_INGOT, Models.GENERATED);
        itemModelGenerator.register(ItemInit.NETHERIUM_PLATE, Models.GENERATED);
        itemModelGenerator.register(ItemInit.SPICE_ASHTHORN, Models.GENERATED);
        itemModelGenerator.register(ItemInit.SPICE_SMOLDERROOT, Models.GENERATED);
        itemModelGenerator.register(ItemInit.SPICE_BRIGHTBURST, Models.GENERATED);
        itemModelGenerator.register(ItemInit.SPICE_GOLDENBLOOM, Models.GENERATED);
        itemModelGenerator.register(ItemInit.SPICE_SKYPEPPER, Models.GENERATED);
        itemModelGenerator.register(ItemInit.SPICE_MIX, Models.GENERATED);
        itemModelGenerator.register(ItemInit.ANCIENT_CLAY_TABLET, Models.GENERATED);
        itemModelGenerator.register(ItemInit.MORTAR, Models.GENERATED);
        itemModelGenerator.register(ItemInit.END_STAR, Models.GENERATED);
    }
}
