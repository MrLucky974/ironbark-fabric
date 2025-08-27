package io.github.mrlucky974.ironbark.init;

import io.github.mrlucky974.ironbark.Ironbark;
import io.github.mrlucky974.ironbark.block.entity.BankBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class BlockEntityInit {
    public static final BlockEntityType<BankBlockEntity> BANK_BLOCK_ENTITY =
            register("bank", BankBlockEntity::new, BlockInit.BANK);

    public static <T extends BlockEntity> BlockEntityType<T> register(String name, BlockEntityType.BlockEntityFactory<? extends T> entityFactory,
                                                                      Block... blocks) {
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, Ironbark.id(name), BlockEntityType.Builder.<T>create(entityFactory, blocks).build());
    }

    public static void init() {
        Ironbark.LOGGER.info("Registering block entities...");
    }
}
