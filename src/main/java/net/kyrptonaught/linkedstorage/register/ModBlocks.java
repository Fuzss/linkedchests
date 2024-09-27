package net.kyrptonaught.linkedstorage.register;

import net.kyrptonaught.linkedstorage.block.StorageBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

public class ModBlocks {
    public static Block storageBlock;

    public static void register() {
        storageBlock = new StorageBlock(BlockBehaviour.Properties.of().mapColor(MapColor.EMERALD).requiresCorrectToolForDrops().strength(2.5f, 2.5f));
    }
}
