package io.github.mrlucky974.ironbark.spice;

import com.mojang.serialization.Codec;
import io.github.mrlucky974.ironbark.list.RegistryList;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Spice {
    public static final Codec<Spice> CODEC = RegistryList.SPICES.getCodec();

    public static final PacketCodec<RegistryByteBuf, Spice> PACKET_CODEC = PacketCodecs
            .unlimitedRegistryCodec(CODEC);

    private final List<SpiceEffect> effects;

    @Nullable
    private String translationKey;

    public Spice(Settings settings) {
        this.effects = settings.getEffects();
    }

    public List<SpiceEffect> getEffects() {
        return this.effects;
    }

    public Text getName() {
        return Text.translatable(this.getTranslationKey());
    }

    protected String getOrCreateTranslationKey() {
        if (this.translationKey == null) {
            this.translationKey = Util.createTranslationKey("spice", RegistryList.SPICES.getId(this));
        }
        return this.translationKey;
    }

    public String getTranslationKey() {
        return this.getOrCreateTranslationKey();
    }

    @Override
    public String toString() {
        Identifier identifier = Objects.requireNonNull(RegistryList.SPICES.getId(this));
        return identifier.toString();
    }

    public static class Settings {
        private final List<SpiceEffect> effects = new ArrayList<>();

        public Settings withEffects(List<SpiceEffect> effects) {
            this.effects.addAll(effects);
            return this;
        }

        public Settings withEffect(SpiceEffect effect) {
            effects.add(effect);
            return this;
        }

        public Settings withEffect(RegistryEntry<StatusEffect> effect) {
            SpiceEffect spiceEffect = SpiceEffect.of(effect);
            effects.add(spiceEffect);
            return this;
        }

        public Settings withEffect(RegistryEntry<StatusEffect> effect, int duration) {
            SpiceEffect spiceEffect = SpiceEffect.of(effect, duration);
            effects.add(spiceEffect);
            return this;
        }

        private List<SpiceEffect> getEffects() {
            return this.effects;
        }
    }
}
