package io.github.mrlucky974.ironbark.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface FoodEatenCallback {
    Event<FoodEatenCallback> EVENT = EventFactory.createArrayBacked(FoodEatenCallback.class,
            (listeners) -> (stack, world, user) -> {
                for (FoodEatenCallback listener : listeners) {
                    listener.onFoodEaten(stack, world, user);
                }
            });

    void onFoodEaten(ItemStack stack, World world, LivingEntity user);
}
