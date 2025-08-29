package io.github.mrlucky974.ironbark.init;

import io.github.mrlucky974.ironbark.Ironbark;
import io.github.mrlucky974.ironbark.component.RecipeReferenceComponent;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;

import java.util.Comparator;
import java.util.Optional;

public class ItemGroupInit {
    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();
    public static final Text MAIN_GROUP_KEY = Text.translatable("itemGroup."+ Ironbark.MOD_ID +".main_group");
    public static final Text ARTIFACTS_GROUP_KEY = Text.translatable("itemGroup."+ Ironbark.MOD_ID +".artifacts_group");

    public static final ItemGroup MAIN_GROUP = register("main_group", FabricItemGroup.builder()
            .displayName(MAIN_GROUP_KEY)
            .icon(ItemInit.BARK::getDefaultStack)
            .entries((displayContext, entries) -> {
                Registries.ITEM.getIds()
                        .stream()
                        .filter(key -> key.getNamespace().equals(Ironbark.MOD_ID))
                        .map(Registries.ITEM::getOrEmpty)
                        .map(Optional::orElseThrow)
                        .filter(item -> !ItemInit.BLACKLIST.contains(item))
                        .sorted(Comparator.<Item, String>comparing(item -> item.getClass().getSimpleName())
                                .thenComparing(item -> Registries.ITEM.getId(item).getPath())
                        )
                        .forEach(entries::add);
            })
            .build());

    public static final ItemGroup ARTIFACTS_GROUP = register("artifacts_group", FabricItemGroup.builder()
            .displayName(ARTIFACTS_GROUP_KEY)
            .icon(ItemInit.ANCIENT_CLAY_TABLET::getDefaultStack)
            .entries((displayContext, entries) -> {
                Optional.ofNullable(CLIENT.world).ifPresent(world -> {
                    RecipeManager recipeManager = world.getRecipeManager();
                    recipeManager.listAllOfType(RecipeInit.TypeInit.TABLET_CRAFTING).stream().map(entry -> {
                        Ironbark.LOGGER.info("Found recipe: {}", entry.id());
                        ItemStack stack = new ItemStack(ItemInit.ANCIENT_CLAY_TABLET, 1);
                        RecipeReferenceComponent component = new RecipeReferenceComponent(entry.id());
                        stack.set(ComponentInit.RECIPE_REFERENCE, component);
                        return stack;
                    }).forEach(entries::add);
                });
            })
            .build());

    public static <T extends ItemGroup> T register(String name, T itemGroup) {
        return Registry.register(Registries.ITEM_GROUP, Ironbark.id(name), itemGroup);
    }

    public static void init() {
        Ironbark.LOGGER.info("Registering item groups...");
    }
}
