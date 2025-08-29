package io.github.mrlucky974.ironbark.mixin;

import io.github.mrlucky974.ironbark.component.SpiceContainerComponent;
import io.github.mrlucky974.ironbark.init.ComponentInit;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.input.SingleStackRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractCookingRecipe.class)
public abstract class AbstractCookingRecipeMixin {
    @Final
    @Shadow
    protected ItemStack result;

    @Inject(method = "craft*", at = @At("RETURN"), cancellable = true)
    private void ironbark$onCraft(SingleStackRecipeInput singleStackRecipeInput, RegistryWrapper.WrapperLookup wrapperLookup, CallbackInfoReturnable<ItemStack> ci) {
        ItemStack input = singleStackRecipeInput.item();
        SpiceContainerComponent spiceContainerComponent = input.get(ComponentInit.SPICES);
        if (spiceContainerComponent != null) {
            ItemStack result = this.result.copy();
            result.set(ComponentInit.SPICES, spiceContainerComponent);
            ci.setReturnValue(result);
        }
    }
}
