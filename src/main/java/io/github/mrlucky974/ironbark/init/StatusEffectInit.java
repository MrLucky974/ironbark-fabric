package io.github.mrlucky974.ironbark.init;

import io.github.mrlucky974.ironbark.Ironbark;
import io.github.mrlucky974.ironbark.entity.effect.IronGutsStatusEffect;
import io.github.mrlucky974.ironbark.entity.effect.SpelunkerStatusEffect;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;

public class StatusEffectInit {
    public static final RegistryEntry<StatusEffect> SPELUNKER = register("spelunker", new SpelunkerStatusEffect());
    public static final RegistryEntry<StatusEffect> IRON_GUTS = register("iron_guts", new IronGutsStatusEffect());

    public static <T extends StatusEffect> RegistryEntry.Reference<StatusEffect> register(String name, T statusEffect) {
        return Registry.registerReference(Registries.STATUS_EFFECT, Ironbark.id(name), statusEffect);
    }

    public static void init() {
        Ironbark.LOGGER.info("Registering status effects...");
    }
}
