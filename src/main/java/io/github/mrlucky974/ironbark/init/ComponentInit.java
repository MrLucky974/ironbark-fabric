package io.github.mrlucky974.ironbark.init;

import io.github.mrlucky974.ironbark.Ironbark;
import io.github.mrlucky974.ironbark.component.RecipeReferenceComponent;
import io.github.mrlucky974.ironbark.component.SpiceContainerComponent;
import io.github.mrlucky974.ironbark.network.CoinContainerComponent;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ComponentInit {
    public static final ComponentType<SpiceContainerComponent> SPICES = register("spices",
            ComponentType.<SpiceContainerComponent>builder()
                    .codec(SpiceContainerComponent.CODEC)
                    .packetCodec(SpiceContainerComponent.PACKET_CODEC)
                    .build());

    public static final ComponentType<RecipeReferenceComponent> RECIPE_REFERENCE = register("recipe_reference",
            ComponentType.<RecipeReferenceComponent>builder()
                    .codec(RecipeReferenceComponent.CODEC)
                    .packetCodec(RecipeReferenceComponent.PACKET_CODEC)
                    .build());


    public static final ComponentType<CoinContainerComponent> COINS = register("coins",
            ComponentType.<CoinContainerComponent>builder()
                    .codec(CoinContainerComponent.CODEC)
                    .packetCodec(CoinContainerComponent.PACKET_CODEC)
                    .build());

    public static <T extends ComponentType<?>> T register(String name, T component) {
        return Registry.register(Registries.DATA_COMPONENT_TYPE, Ironbark.id(name), component);
    }

    public static void init() {
        Ironbark.LOGGER.info("Registering components...");
    }
}
