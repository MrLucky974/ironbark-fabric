package io.github.mrlucky974.ironbark;

import io.github.mrlucky974.ironbark.component.SpiceEffectsComponent;
import io.github.mrlucky974.ironbark.config.IronbarkConfig;
import io.github.mrlucky974.ironbark.event.FoodEatenCallback;
import io.github.mrlucky974.ironbark.init.*;
import io.github.mrlucky974.ironbark.item.SpiceIngredient;
import io.github.mrlucky974.ironbark.network.ConfigPayload;
import io.github.mrlucky974.ironbark.network.OreChunksPayload;
import io.github.mrlucky974.ironbark.recipe.SpicyFoodRecipe;
import io.github.mrlucky974.ironbark.util.StatusEffectMerger;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Ironbark implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("Ironbark");
    public static final String MOD_ID = "ironbark";

    public static final Text SPICY_TOOLTIP_KEY = Text.translatable("tooltip." + MOD_ID + ".spicy")
            .formatted(Formatting.DARK_PURPLE, Formatting.BOLD);

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing mod...");

        try {
            IronbarkConfig.createDefaultConfig();
            IronbarkConfig.loadConfig();
        } catch (IOException e) {
            Ironbark.LOGGER.error("Failed to load config file", e);
        }

        PayloadTypeRegistry.playS2C().register(ConfigPayload.ID, ConfigPayload.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(OreChunksPayload.ID, OreChunksPayload.PACKET_CODEC);

        ServerLifecycleEvents.SERVER_STARTED.register(server -> IronbarkConfig.initBlockHighlightConfig());

        ComponentInit.init();
        BlockInit.init();
        ItemInit.init();
        ItemGroupInit.init();
        RecipeInit.init();
        StatusEffectInit.init();
        PotionInit.init();

        FoodEatenCallback.EVENT.register(((stack, world, user) -> {
            if (SpicyFoodRecipe.isEdible(stack) && SpiceIngredient.of(stack.getItem()) == null) {
                SpiceEffectsComponent spiceEffectsComponent = stack.get(ComponentInit.SPICE_EFFECTS_COMPONENT);
                if (spiceEffectsComponent != null) {
                    List<StatusEffectInstance> statusEffectInstances = new ArrayList<>();
                    for (SpiceEffectsComponent.SpiceEffect effect : spiceEffectsComponent.effects()) {
                        statusEffectInstances.add(effect.createStatusEffectInstance());
                    }

                    List<StatusEffectInstance> mergedStatusEffectInstances = StatusEffectMerger.mergeEffects(statusEffectInstances, StatusEffectMerger.DurationMergeStrategy.COMBINE_DURATION);
                    mergedStatusEffectInstances.forEach(user::addStatusEffect);
                }
            }
        }));

        ItemTooltipCallback.EVENT.register(((stack, tooltipContext, tooltipType, list) ->  {
            if (SpicyFoodRecipe.isEdible(stack) && SpiceIngredient.of(stack.getItem()) == null) {
                SpiceEffectsComponent spiceEffectsComponent = stack.get(ComponentInit.SPICE_EFFECTS_COMPONENT);
                if (spiceEffectsComponent != null) {
                    list.add(SPICY_TOOLTIP_KEY);

                    if (tooltipType.isCreative()) {
                        List<StatusEffectInstance> effects = new ArrayList<>();
                        for (SpiceEffectsComponent.SpiceEffect spiceEffect : spiceEffectsComponent.effects()) {
                            effects.add(spiceEffect.createStatusEffectInstance());
                        }

                        PotionContentsComponent.buildTooltip(StatusEffectMerger.mergeEffects(effects, StatusEffectMerger.DurationMergeStrategy.COMBINE_DURATION), list::add, 1.0F, tooltipContext.getUpdateTickRate());
                    }
                }
            }
        }));
    }

    public static Identifier id(String name) {
        return Identifier.of(Ironbark.MOD_ID, name);
    }
}
