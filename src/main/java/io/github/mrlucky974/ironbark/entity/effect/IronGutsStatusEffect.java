package io.github.mrlucky974.ironbark.entity.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;

public class IronGutsStatusEffect extends StatusEffect {
    public IronGutsStatusEffect() {
        super(StatusEffectCategory.BENEFICIAL, 0x2D2F35);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    @Override
    public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (entity instanceof PlayerEntity) {
            if (entity.hasStatusEffect(StatusEffects.HUNGER)) {
                entity.removeStatusEffect(StatusEffects.HUNGER);
            }

            if (entity.hasStatusEffect(StatusEffects.POISON)) {
                entity.removeStatusEffect(StatusEffects.POISON);
            }

            if (entity.hasStatusEffect(StatusEffects.NAUSEA)) {
                entity.removeStatusEffect(StatusEffects.NAUSEA);
            }

            if (entity.hasStatusEffect(StatusEffects.WEAKNESS)) {
                entity.removeStatusEffect(StatusEffects.WEAKNESS);
            }
        }

        return true;
    }
}
