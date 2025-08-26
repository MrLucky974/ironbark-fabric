package io.github.mrlucky974.ironbark.mixin;

import io.github.mrlucky974.ironbark.event.FoodEatenCallback;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public abstract class ItemMixin {
    @Inject(method = "finishUsing", at = @At("HEAD"))
    private void ironbark$onFinishUsing(ItemStack stack, World world, LivingEntity user, CallbackInfoReturnable<ItemStack> cir) {
        FoodEatenCallback.EVENT.invoker().onFoodEaten(stack, world, user);
    }
}
