package io.github.mrlucky974.ironbark.data.provider.lang;

import dev.emi.emi.api.recipe.EmiRecipeCategory;
import io.github.mrlucky974.ironbark.Ironbark;
import io.github.mrlucky974.ironbark.emi.IronbarkEmiPlugin;
import io.github.mrlucky974.ironbark.init.BlockInit;
import io.github.mrlucky974.ironbark.init.ItemGroupInit;
import io.github.mrlucky974.ironbark.init.ItemInit;
import io.github.mrlucky974.ironbark.item.AncientClayTabletItem;
import io.github.mrlucky974.ironbark.item.CoinItem;
import io.github.mrlucky974.ironbark.item.CoinSackItem;
import io.github.mrlucky974.ironbark.list.TagList;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class IronbarkEnglishLanguageProvider extends FabricLanguageProvider {
    public IronbarkEnglishLanguageProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    private static void addText(@NotNull TranslationBuilder translationBuilder, @NotNull Text text, @NotNull String value) {
        if (text.getContent() instanceof TranslatableTextContent translatableTextContent) {
            translationBuilder.add(translatableTextContent.getKey(), value);
        } else {
            Ironbark.LOGGER.warn("Failed to add translation for text: {}", text.getString());
        }
    }

    private static void addEmiRecipeCategory(@NotNull TranslationBuilder translationBuilder, @NotNull EmiRecipeCategory category, @NotNull String value) {
        translationBuilder.add(translateId("emi.category.", category.getId()), value);
    }

    private static String translateId(String prefix, Identifier id) {
        return prefix + id.getNamespace() + "." + id.getPath().replace('/', '.');
    }

    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup wrapperLookup, TranslationBuilder translationBuilder) {
        translationBuilder.add(ItemInit.STEEL_INGOT, "Steel Ingot");
        translationBuilder.add(ItemInit.FLOUR, "Flour");
        translationBuilder.add(ItemInit.DOUGH, "Dough");
        translationBuilder.add(ItemInit.BARK, "Bark");
        translationBuilder.add(ItemInit.JUNGLE_SAP, "Jungle Sap");
        translationBuilder.add(ItemInit.SPRUCE_SAP, "Spruce Sap");
        translationBuilder.add(ItemInit.ANTHRACITE_COAL, "Anthracite Coal");
        translationBuilder.add(ItemInit.MULCH, "Mulch");
        translationBuilder.add(ItemInit.NETHERIUM_INGOT, "Netherium Ingot");
        translationBuilder.add(ItemInit.NETHERIUM_PLATE, "Netherium Plate");
        translationBuilder.add(ItemInit.ROSE_GOLD_INGOT, "Rose Gold Ingot");
        translationBuilder.add(ItemInit.SPICE_ASHTHORN, "Ashthorn");
        translationBuilder.add(ItemInit.SPICE_SMOLDERROOT, "Smolderroot");
        translationBuilder.add(ItemInit.SPICE_BRIGHTBURST, "Brightburst");
        translationBuilder.add(ItemInit.SPICE_GOLDENBLOOM, "Goldenbloom");
        translationBuilder.add(ItemInit.SPICE_SKYPEPPER, "Skypepper");
        translationBuilder.add(ItemInit.SPICE_MIX, "Spice Mix");
        translationBuilder.add(ItemInit.ANCIENT_CLAY_TABLET, "Ancient Clay Tablet");
        translationBuilder.add(ItemInit.CHARRED_BONE, "Charred Bone");
        translationBuilder.add(BlockInit.DEEPSLATE_ANTHRACITE_COAL_ORE, "Deepslate Anthracite Coal Ore");
        translationBuilder.add(BlockInit.ANTHRACITE_COAL_BLOCK, "Block of Anthracite Coal");
        translationBuilder.add(ItemInit.MORTAR, "Mortar");
        translationBuilder.add(TagList.Items.SPICY_ITEM_BLACKLIST, "Spicy Items Blacklist");
        translationBuilder.add(TagList.Items.REPAIR_GEMS_BLACKLIST, "Repair Gems Blacklist");
        translationBuilder.add(ItemInit.END_STAR, "End Star");
        translationBuilder.add(BlockInit.NETHERIUM_BLOCK, "Block of Netherium");
        translationBuilder.add(BlockInit.INDUSTRIAL_NETHERIUM_BLOCK, "Block of Industrial Netherium");
        translationBuilder.add(BlockInit.STEEL_BLOCK, "Block of Steel");
        translationBuilder.add(BlockInit.CHARCOAL_BLOCK, "Block of Charcoal");
        translationBuilder.add(ItemInit.COPPER_COIN, "Copper Coin");
        translationBuilder.add(ItemInit.IRON_COIN, "Iron Coin");
        translationBuilder.add(ItemInit.GOLD_COIN, "Gold Coin");
        translationBuilder.add(ItemInit.ROSE_GOLD_COIN, "Rose Gold Coin");
        translationBuilder.add(ItemInit.COIN_SACK, "Coin Sack");
        translationBuilder.add(ItemInit.WEAK_REPAIR_GEM, "Weak Repair Gem");
        translationBuilder.add(ItemInit.BASIC_REPAIR_GEM, "Basic Repair Gem");
        translationBuilder.add(ItemInit.STRONG_REPAIR_GEM, "Strong Repair Gem");
        translationBuilder.add(ItemInit.BETTER_REPAIR_GEM, "Better Repair Gem");
        translationBuilder.add(ItemInit.ULTIMATE_REPAIR_GEM, "Ultimate Repair Gem");
        translationBuilder.add(BlockInit.BANK, "Bank");
        translationBuilder.add(BlockInit.ROSE_GOLD_BLOCK, "Block of Rose Gold");

        addEmiRecipeCategory(translationBuilder, IronbarkEmiPlugin.MORTAR_RECIPE_CATEGORY, "Mortar");

        addText(translationBuilder, ItemGroupInit.MAIN_GROUP_KEY, "Ironbark");
        addText(translationBuilder, ItemGroupInit.ARTIFACTS_GROUP_KEY, "Ironbark - Artifacts");

        addText(translationBuilder, Ironbark.SPICY_TOOLTIP, "Spicy");
        addText(translationBuilder, AncientClayTabletItem.INVALID_RECIPE_TOOLTIP, "Invalid Recipe");
        translationBuilder.add(CoinItem.COIN_TOOLTIP_KEY, "Value: %d");
        translationBuilder.add(CoinSackItem.COIN_SACK_TOOLTIP_KEY, "Amount: %d");
    }
}
