package io.github.mrlucky974.ironbark.data.provider;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.mrlucky974.ironbark.Ironbark;
import io.github.mrlucky974.ironbark.init.BlockInit;
import io.github.mrlucky974.ironbark.init.ItemInit;
import io.github.mrlucky974.ironbark.item.CoinItem;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.*;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class IronbarkModelProvider extends FabricModelProvider {
    public IronbarkModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        blockStateModelGenerator.registerSimpleCubeAll(BlockInit.DEEPSLATE_ANTHRACITE_COAL_ORE);
        blockStateModelGenerator.registerSimpleCubeAll(BlockInit.ANTHRACITE_COAL_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(BlockInit.CHARCOAL_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(BlockInit.NETHERIUM_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(BlockInit.STEEL_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(BlockInit.ROSE_GOLD_BLOCK);
        blockStateModelGenerator.registerSingleton(BlockInit.INDUSTRIAL_NETHERIUM_BLOCK, TexturedModel.END_FOR_TOP_CUBE_COLUMN);
        blockStateModelGenerator.registerSingleton(BlockInit.BANK, (block) ->
                TexturedModel.CUBE_BOTTOM_TOP.get(block)
                .textures(tex -> tex
                        .put(TextureKey.BOTTOM, Identifier.ofVanilla("block/smooth_stone"))
                        .put(TextureKey.TOP,    Ironbark.id("block/bank_top"))
                        .put(TextureKey.SIDE,  Ironbark.id("block/bank_side"))
                )
        );
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
        itemModelGenerator.register(ItemInit.ROSE_GOLD_INGOT, Models.GENERATED);
        itemModelGenerator.register(ItemInit.CHARRED_BONE, Models.GENERATED);
        itemModelGenerator.register(ItemInit.DIAMOND_FRAGMENT, Models.GENERATED);
        itemModelGenerator.register(ItemInit.SPICE_ASHTHORN, Models.GENERATED);
        itemModelGenerator.register(ItemInit.SPICE_SMOLDERROOT, Models.GENERATED);
        itemModelGenerator.register(ItemInit.SPICE_BRIGHTBURST, Models.GENERATED);
        itemModelGenerator.register(ItemInit.SPICE_GOLDENBLOOM, Models.GENERATED);
        itemModelGenerator.register(ItemInit.SPICE_SKYPEPPER, Models.GENERATED);
        itemModelGenerator.register(ItemInit.SPICE_MIX, Models.GENERATED);
        itemModelGenerator.register(ItemInit.ANCIENT_CLAY_TABLET, Models.GENERATED);
        itemModelGenerator.register(ItemInit.MORTAR, Models.GENERATED);
        itemModelGenerator.register(ItemInit.END_STAR, Models.GENERATED);
        itemModelGenerator.register(ItemInit.WEAK_REPAIR_GEM, Models.GENERATED);
        itemModelGenerator.register(ItemInit.BASIC_REPAIR_GEM, Models.GENERATED);
        itemModelGenerator.register(ItemInit.BETTER_REPAIR_GEM, Models.GENERATED);
        itemModelGenerator.register(ItemInit.STRONG_REPAIR_GEM, Models.GENERATED);
        itemModelGenerator.register(ItemInit.ULTIMATE_REPAIR_GEM, Models.GENERATED);

        Registries.ITEM.getIds()
                .stream()
                .filter(key -> key.getNamespace().equals(Ironbark.MOD_ID))
                .map(Registries.ITEM::getOrEmpty)
                .map(Optional::orElseThrow)
                .filter(item -> item instanceof CoinItem)
                .forEach(item -> {
                    // Predicate model (the variants)
                    Model predicateModel = new Model(Optional.of(ModelIds.getItemModelId(item)), Optional.empty(), TextureKey.LAYER0);

                    // Ordered mapping: predicate → model suffix
                    LinkedHashMap<Map<Identifier, Float>, String> predicateToValueToSuffixes = new LinkedHashMap<>();

                    // Example: different coin stack stages
                    Map<Identifier, Float> fewCoins = new HashMap<>();
                    fewCoins.put(Identifier.ofVanilla("stack"), 0.25f); // up to 25% full
                    predicateToValueToSuffixes.put(fewCoins, "_1");

                    Map<Identifier, Float> halfCoins = new HashMap<>();
                    halfCoins.put(Identifier.ofVanilla("stack"), 0.5f); // up to 50% full
                    predicateToValueToSuffixes.put(halfCoins, "_2");

                    Map<Identifier, Float> manyCoins = new HashMap<>();
                    manyCoins.put(Identifier.ofVanilla("stack"), 0.75f); // up to 75% full
                    predicateToValueToSuffixes.put(manyCoins, "_3");

                    Map<Identifier, Float> fullCoins = new HashMap<>();
                    fullCoins.put(Identifier.ofVanilla("stack"), 1.0f); // full stack
                    predicateToValueToSuffixes.put(fullCoins, "_4");

                    // Register the models with your helper
                    registerItemWithPredicate(
                            itemModelGenerator,
                            item,
                            Models.GENERATED,
                            Map::of, // no extra fields
                            predicateToValueToSuffixes,
                            predicateModel
                    );
                });

        LinkedHashMap<Map<Identifier, Float>, String> coinSackPredicates = new LinkedHashMap<>();
        Map<Identifier, Float> filledCoins = new HashMap<>();
        filledCoins.put(Identifier.ofVanilla("amount"), 1.0F);
        coinSackPredicates.put(filledCoins, "_filled");
        registerItemWithPredicate(itemModelGenerator,
                ItemInit.COIN_SACK,
                Models.GENERATED,
                Map::of,
                coinSackPredicates,
                new Model(Optional.of(ModelIds.getItemModelId(ItemInit.COIN_SACK)), Optional.empty(), TextureKey.LAYER0));
    }

    private void registerItemWithPredicate(
            ItemModelGenerator itemModelGenerator,
            Item item,
            Model model,
            Supplier<Map<String, JsonElement>> getAdditionalModelFields,
            LinkedHashMap<Map<Identifier, Float>, String> predicateToValueToSuffixes, // 🔹 ordered
            Model predicateModel) {

        Identifier modelId = ModelIds.getItemModelId(item);

        // Preserve suffix order
        for (String suffix : predicateToValueToSuffixes.values()) {
            predicateModel.upload(
                    modelId.withSuffixedPath(suffix),
                    TextureMap.layer0(modelId.withSuffixedPath(suffix)),
                    itemModelGenerator.writer
            );
        }

        model.upload(
                modelId,
                TextureMap.layer0(item),
                itemModelGenerator.writer,
                (Identifier id, Map<TextureKey, Identifier> textures) ->
                        createPredicateJson(id, textures, model, getAdditionalModelFields, predicateToValueToSuffixes)
        );
    }

    public JsonObject createPredicateJson(
            Identifier id,
            Map<TextureKey, Identifier> textures,
            Model model,
            Supplier<Map<String, JsonElement>> getAdditionalModelFields,
            LinkedHashMap<Map<Identifier, Float>, String> predicateToValueToSuffixes) {

        // Start with base model JSON
        JsonObject modelJson = model.createJson(id, textures);

        // Add any extra fields
        for (Map.Entry<String, JsonElement> propertyToValue : getAdditionalModelFields.get().entrySet()) {
            modelJson.add(propertyToValue.getKey(), propertyToValue.getValue());
        }

        // Build overrides
        JsonArray overrides = new JsonArray();

        for (Map.Entry<Map<Identifier, Float>, String> entry : predicateToValueToSuffixes.entrySet()) {
            JsonObject override = new JsonObject();
            JsonObject predicateJson = new JsonObject();

            for (Map.Entry<Identifier, Float> predicateEntry : entry.getKey().entrySet()) {
                predicateJson.addProperty(predicateEntry.getKey().toString(), predicateEntry.getValue());
            }

            override.addProperty("model", id.withSuffixedPath(entry.getValue()).toString());
            override.add("predicate", predicateJson);

            overrides.add(override);
        }

        modelJson.add("overrides", overrides);

        return modelJson;
    }
}
