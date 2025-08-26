package io.github.mrlucky974.ironbark.init;

import io.github.mrlucky974.ironbark.Ironbark;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class PotionInit {
    public static final Potion SPELUNKER = register("spelunker", new Potion(new StatusEffectInstance(StatusEffectInit.SPELUNKER, 20 * 45)));
    public static final Potion LONG_SPELUNKER = register("long_spelunker", new Potion(new StatusEffectInstance(StatusEffectInit.SPELUNKER, 20 * 90)));

    public static <T extends Potion> T register(String name, T potion) {
        return Registry.register(Registries.POTION, Ironbark.id(name), potion);
    }

    public static void init() {
        Ironbark.LOGGER.info("Registering potions...");
    }
}
