package io.github.mrlucky974.ironbark.init;

import io.github.mrlucky974.ironbark.Ironbark;
import io.github.mrlucky974.ironbark.item.*;
import io.github.mrlucky974.ironbark.list.FoodList;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Rarity;

import java.util.ArrayList;
import java.util.List;


public class ItemInit {
    public static final List<ItemConvertible> BLACKLIST = new ArrayList<>();
    public static final float DEFAULT_SPICE_DURATION = 3f;

    public static final Item STEEL_INGOT = register("steel_ingot", new Item(new Item.Settings()));
    public static final Item FLOUR = register("flour", new Item(new Item.Settings()));
    public static final Item DOUGH = register("dough", new Item(new Item.Settings()));
    public static final Item BARK = register("bark", new Item(new Item.Settings()));
    public static final Item JUNGLE_SAP = register("jungle_sap", new Item(new Item.Settings()));
    public static final Item SPRUCE_SAP = register("spruce_sap", new Item(new Item.Settings()));
    public static final Item ANTHRACITE_COAL = register("anthracite_coal", new Item(new Item.Settings()));
    public static final Item MULCH = register("mulch", new Item(new Item.Settings()));
    public static final Item NETHERIUM_INGOT = register("netherium_ingot", new Item(new Item.Settings()));
    public static final Item NETHERIUM_PLATE = register("netherium_plate", new Item(new Item.Settings()));
    public static final Item ROSE_GOLD_INGOT = register("rose_gold_ingot", new Item(new Item.Settings()));
    public static final Item CHARRED_BONE = register("charred_bone", new Item(new Item.Settings()));
    public static final Item DIAMOND_FRAGMENT = register("diamond_fragment", new Item(new Item.Settings()));

    public static final Item SPICE_ASHTHORN = register("spice_ashthorn",
            new SpiceItem(StatusEffects.WITHER, DEFAULT_SPICE_DURATION,
                    new Item.Settings().food(FoodList.SPICE_FOOD_COMPONENT)));

    public static final Item SPICE_SMOLDERROOT = register("spice_smolderroot",
            new SpiceItem(StatusEffects.FIRE_RESISTANCE, DEFAULT_SPICE_DURATION,
                    new Item.Settings().food(FoodList.SPICE_FOOD_COMPONENT)));

    public static final Item SPICE_BRIGHTBURST = register("spice_brightburst",
            new SpiceItem(StatusEffects.HASTE, DEFAULT_SPICE_DURATION,
                    new Item.Settings().food(FoodList.SPICE_FOOD_COMPONENT)));

    public static final Item SPICE_GOLDENBLOOM = register("spice_goldenbloom",
            new SpiceItem(StatusEffects.REGENERATION, DEFAULT_SPICE_DURATION,
                    new Item.Settings().food(FoodList.SPICE_FOOD_COMPONENT)));

    public static final Item SPICE_SKYPEPPER = register("spice_skypepper",
            new SpiceItem(StatusEffects.SPEED, DEFAULT_SPICE_DURATION,
                    new Item.Settings().food(FoodList.SPICE_FOOD_COMPONENT)));

    public static final Item SPICE_MIX = register("spice_mix",
            new SpiceMixItem(new Item.Settings().maxCount(1).recipeRemainder(Items.BUNDLE)));

    public static final Item ANCIENT_CLAY_TABLET = register("ancient_clay_tablet",
            new AncientClayTabletItem(new Item.Settings().maxCount(1).rarity(ExtendedRarity.ANCIENT)));

    public static final Item MORTAR = register("mortar", new MortarItem(new Item.Settings().maxCount(1)));

    public static final Item END_STAR = register("end_star",
            new Item(new Item.Settings()
                    .rarity(ExtendedRarity.LEGENDARY)
                    .component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)
            )
    );

    public static final CoinItem COPPER_COIN = register("copper_coin",
            new CoinItem(1, new Item.Settings()));

    public static final CoinItem IRON_COIN = register("iron_coin",
            new CoinItem(64, new Item.Settings().rarity(Rarity.UNCOMMON)));

    public static final CoinItem GOLD_COIN = register("gold_coin",
            new CoinItem(64 * 8, new Item.Settings().rarity(Rarity.RARE)));

    public static final CoinItem ROSE_GOLD_COIN = register("rose_gold_coin",
            new CoinItem(64 * 64, new Item.Settings().rarity(Rarity.RARE)));

    public static final Item COIN_SACK = register("coin_sack",
            new CoinSackItem(new Item.Settings().maxCount(1)));

    public static final Item WEAK_REPAIR_GEM = register("weak_repair_gem",
            new RepairGemItem(200, new Item.Settings().maxDamage(100).rarity(Rarity.COMMON)));

    public static final Item BASIC_REPAIR_GEM = register("basic_repair_gem",
            new RepairGemItem(150, new Item.Settings().maxDamage(240).rarity(Rarity.COMMON)));

    public static final Item STRONG_REPAIR_GEM = register("strong_repair_gem",
            new RepairGemItem(100, new Item.Settings().maxDamage(460).rarity(Rarity.UNCOMMON)));

    public static final Item BETTER_REPAIR_GEM = register("better_repair_gem",
            new RepairGemItem(25, new Item.Settings().maxDamage(750).rarity(Rarity.RARE)));

    public static final Item ULTIMATE_REPAIR_GEM = register("ultimate_repair_gem",
            new RepairGemItem(2, new Item.Settings().maxDamage(975).rarity(Rarity.EPIC)));

    public static final Item IRON_SCYTHE = register("iron_scythe",
            new ScytheItem(ToolMaterials.IRON,
                    new Item.Settings()
                            .attributeModifiers(ScytheItem.createAttributeModifiers(ToolMaterials.IRON, 6.0F, -2.4F))
            )
    );

    public static final Item SOUL_SCYTHE = register("soul_scythe",
            new ScytheItem(ToolMaterials.NETHERITE,
                    new Item.Settings()
                            .attributeModifiers(ScytheItem.createAttributeModifiers(ToolMaterials.NETHERITE, 5.0F, -2.4F))
            )
    );

    public static <T extends Item> T register(String name, T item) {
        return Registry.register(Registries.ITEM, Ironbark.id(name), item);
    }

    public static void init() {
        Ironbark.LOGGER.info("Registering items...");
        ItemInit.BLACKLIST.add(ItemInit.SPICE_MIX);
        ItemInit.BLACKLIST.add(ItemInit.ANCIENT_CLAY_TABLET);

        FilterInit.addItem(ItemInit.ANTHRACITE_COAL, FilterInit.MATERIALS);
        FilterInit.addItem(ItemInit.STEEL_INGOT, FilterInit.MATERIALS);
        FilterInit.addItem(ItemInit.ROSE_GOLD_INGOT, FilterInit.MATERIALS);
        FilterInit.addItem(ItemInit.NETHERIUM_INGOT, FilterInit.MATERIALS);
        FilterInit.addItem(ItemInit.NETHERIUM_PLATE, FilterInit.MATERIALS);
        FilterInit.addItem(ItemInit.MULCH, FilterInit.MATERIALS);
        FilterInit.addItem(ItemInit.SPRUCE_SAP, FilterInit.MATERIALS);
        FilterInit.addItem(ItemInit.JUNGLE_SAP, FilterInit.MATERIALS);
        FilterInit.addItem(ItemInit.BARK, FilterInit.MATERIALS);
        FilterInit.addItem(ItemInit.CHARRED_BONE, FilterInit.MATERIALS);
        FilterInit.addItem(ItemInit.DIAMOND_FRAGMENT, FilterInit.MATERIALS);
        FilterInit.addItem(ItemInit.FLOUR, FilterInit.MATERIALS);
        FilterInit.addItem(ItemInit.DOUGH, FilterInit.MATERIALS);
        FilterInit.addItem(ItemInit.END_STAR, FilterInit.MATERIALS);

        FilterInit.addItem(ItemInit.WEAK_REPAIR_GEM, FilterInit.REPAIR_GEMS);
        FilterInit.addItem(ItemInit.BASIC_REPAIR_GEM, FilterInit.REPAIR_GEMS);
        FilterInit.addItem(ItemInit.STRONG_REPAIR_GEM, FilterInit.REPAIR_GEMS);
        FilterInit.addItem(ItemInit.BETTER_REPAIR_GEM, FilterInit.REPAIR_GEMS);
        FilterInit.addItem(ItemInit.ULTIMATE_REPAIR_GEM, FilterInit.REPAIR_GEMS);

        FilterInit.addItem(ItemInit.MORTAR, FilterInit.UTILITIES);
        FilterInit.addItem(ItemInit.IRON_SCYTHE, FilterInit.UTILITIES);
        FilterInit.addItem(ItemInit.SOUL_SCYTHE, FilterInit.UTILITIES);

        FilterInit.addItem(ItemInit.COPPER_COIN, FilterInit.CURRENCY);
        FilterInit.addItem(ItemInit.IRON_COIN, FilterInit.CURRENCY);
        FilterInit.addItem(ItemInit.GOLD_COIN, FilterInit.CURRENCY);
        FilterInit.addItem(ItemInit.ROSE_GOLD_COIN, FilterInit.CURRENCY);
        FilterInit.addItem(BlockInit.BANK.asItem(), FilterInit.CURRENCY);
        FilterInit.addItem(ItemInit.COIN_SACK, FilterInit.CURRENCY);

        FilterInit.addItem(BlockInit.STEEL_BLOCK.asItem(), FilterInit.BLOCKS);
        FilterInit.addItem(BlockInit.ROSE_GOLD_BLOCK.asItem(), FilterInit.BLOCKS);
        FilterInit.addItem(BlockInit.CHARCOAL_BLOCK.asItem(), FilterInit.BLOCKS);
        FilterInit.addItem(BlockInit.DEEPSLATE_ANTHRACITE_COAL_ORE.asItem(), FilterInit.BLOCKS);
        FilterInit.addItem(BlockInit.ANTHRACITE_COAL_BLOCK.asItem(), FilterInit.BLOCKS);
        FilterInit.addItem(BlockInit.NETHERIUM_BLOCK.asItem(), FilterInit.BLOCKS);
        FilterInit.addItem(BlockInit.INDUSTRIAL_NETHERIUM_BLOCK.asItem(), FilterInit.BLOCKS);

        FilterInit.addItem(ItemInit.SPICE_SKYPEPPER, FilterInit.FOOD);
        FilterInit.addItem(ItemInit.SPICE_GOLDENBLOOM, FilterInit.FOOD);
        FilterInit.addItem(ItemInit.SPICE_BRIGHTBURST, FilterInit.FOOD);
        FilterInit.addItem(ItemInit.SPICE_SMOLDERROOT, FilterInit.FOOD);
        FilterInit.addItem(ItemInit.SPICE_ASHTHORN, FilterInit.FOOD);

        FuelRegistry.INSTANCE.add(ItemInit.ANTHRACITE_COAL, 3200);
        FuelRegistry.INSTANCE.add(ItemInit.MULCH, 800);
        FuelRegistry.INSTANCE.add(BlockInit.ANTHRACITE_COAL_BLOCK.asItem(), 32000);
        FuelRegistry.INSTANCE.add(BlockInit.CHARCOAL_BLOCK.asItem(), 16000);
    }
}
