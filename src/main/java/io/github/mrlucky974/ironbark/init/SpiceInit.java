package io.github.mrlucky974.ironbark.init;

import io.github.mrlucky974.ironbark.Ironbark;
import io.github.mrlucky974.ironbark.list.RegistryList;
import io.github.mrlucky974.ironbark.spice.Spice;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.Registry;

public class SpiceInit {
    public static final Spice SMOLDERROOT = register("smolderroot",
            new Spice(new Spice.Settings()
                    .withEffect(StatusEffects.FIRE_RESISTANCE))
    );

    public static final Spice ASHTHORN = register("ashthorn",
            new Spice(new Spice.Settings()
                    .withEffect(StatusEffects.WITHER))
    );

    public static final Spice BRIGHTBURST = register("brightburst",
            new Spice(new Spice.Settings()
                    .withEffect(StatusEffects.HASTE))
    );

    public static final Spice GOLDENBLOOM = register("goldenbloom",
            new Spice(new Spice.Settings()
                    .withEffect(StatusEffects.REGENERATION))
    );

    public static final Spice SKYPEPPER = register("skypepper",
            new Spice(new Spice.Settings()
                    .withEffect(StatusEffects.SPEED))
    );

    public static Spice register(String name, Spice spice) {
        return Registry.register(RegistryList.SPICES, Ironbark.id(name), spice);
    }

    public static void init() {
        Ironbark.LOGGER.info("Registering spices...");

        RegistryList.SPICES.forEach(spice -> {
            Ironbark.LOGGER.info("Added spice: {}", spice);
        });
    }
}
