package io.github.mrlucky974.ironbark.list;

import io.github.mrlucky974.ironbark.Ironbark;
import io.github.mrlucky974.ironbark.spice.Spice;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

public class RegistryKeyList {
    public static final RegistryKey<Registry<Spice>> SPICE = RegistryKey.ofRegistry(Ironbark.id("spice"));
}
