package io.github.mrlucky974.ironbark.list;

import io.github.mrlucky974.ironbark.spice.Spice;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.registry.*;

import static io.github.mrlucky974.ironbark.list.RegistryKeyList.SPICE;

public class RegistryList {


    public static final Registry<Spice> SPICES = FabricRegistryBuilder
            .createSimple(SPICE)
            .attribute(RegistryAttribute.SYNCED)
            .buildAndRegister();
}
