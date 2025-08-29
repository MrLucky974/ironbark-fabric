package io.github.mrlucky974.ironbark.item;

import io.github.mrlucky974.ironbark.Ironbark;
import io.github.mrlucky974.ironbark.component.SpiceContainerComponent;
import io.github.mrlucky974.ironbark.init.ComponentInit;
import io.github.mrlucky974.ironbark.list.FoodList;
import io.github.mrlucky974.ironbark.recipe.SpicyFoodRecipe;
import io.github.mrlucky974.ironbark.spice.Spice;
import io.github.mrlucky974.ironbark.spice.SpiceEffect;
import io.github.mrlucky974.ironbark.util.StatusEffectMerger;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class SpiceItem extends Item implements SpiceIngredient {
    private final SpiceContainerComponent spiceContainer;

    public SpiceItem(Spice spice) {
        super(new Item.Settings().food(FoodList.SPICE_FOOD_COMPONENT));
        this.spiceContainer = createContainer(spice);
    }

    protected static SpiceContainerComponent createContainer(Spice... spices) {
        return new SpiceContainerComponent(Arrays.stream(spices).toList());
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);

        SpiceContainerComponent spices = new SpiceContainerComponent(this.getSpices(stack));
        if (type.isCreative()) {
            PotionContentsComponent.buildTooltip(spices.createStatusEffectInstances(), tooltip::add, 1.0F, context.getUpdateTickRate());
        }
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        SpiceIngredient.apply(stack, user);
        return super.finishUsing(stack, world, user);
    }

    @Override
    public List<Spice> getSpices(@Nullable ItemStack stack) {
        if (stack == null || stack.isEmpty())
            return null;

        return this.spiceContainer.spices();
    }
}
