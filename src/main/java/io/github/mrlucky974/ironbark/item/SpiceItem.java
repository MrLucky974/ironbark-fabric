package io.github.mrlucky974.ironbark.item;

import io.github.mrlucky974.ironbark.component.SpiceEffectsComponent;
import io.github.mrlucky974.ironbark.util.StatusEffectMerger;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SpiceItem extends Item implements SpiceIngredient {
    private final SpiceEffectsComponent spiceEffects;

    public SpiceItem(RegistryEntry<StatusEffect> effect, float effectLengthInSeconds, Settings settings) {
        this(createSpiceEffectList(effect, effectLengthInSeconds), settings);
    }

    public SpiceItem(SpiceEffectsComponent spiceEffectsComponent, Settings settings) {
        super(settings);
        this.spiceEffects = spiceEffectsComponent;
    }

    protected static SpiceEffectsComponent createSpiceEffectList(RegistryEntry<StatusEffect> effect, float effectLengthInSeconds) {
        return new SpiceEffectsComponent(List.of(new SpiceEffectsComponent.SpiceEffect(effect, MathHelper.floor(effectLengthInSeconds * 20.0F))));
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);

        if (type.isCreative()) {
            List<StatusEffectInstance> list = new ArrayList<>();
            SpiceEffectsComponent spiceEffectsComponent = this.getSpiceEffect(stack);

            for (SpiceEffectsComponent.SpiceEffect spiceEffect : spiceEffectsComponent.effects()) {
                list.add(spiceEffect.createStatusEffectInstance());
            }

            Objects.requireNonNull(tooltip);
            PotionContentsComponent.buildTooltip(StatusEffectMerger.mergeEffects(list, StatusEffectMerger.DurationMergeStrategy.COMBINE_DURATION), tooltip::add, 1.0F, context.getUpdateTickRate());
        }
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        SpiceEffectsComponent spiceEffectsComponent = this.getSpiceEffect(stack);
        for (SpiceEffectsComponent.SpiceEffect spiceEffect : spiceEffectsComponent.effects()) {
            user.addStatusEffect(spiceEffect.createStatusEffectInstance());
        }

        return super.finishUsing(stack, world, user);
    }

    @Override
    public SpiceEffectsComponent getSpiceEffect(@Nullable ItemStack stack) {
        SpiceEffectsComponent spiceEffectComponent = SpiceIngredient.super.getSpiceEffect(stack);
        return spiceEffectComponent != null ? spiceEffectComponent : this.spiceEffects;
    }
}
