package net.kyrptonaught.linkedstorage.util;

import net.kyrptonaught.linkedstorage.block.StorageBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class LinkedInventoryHelper {
    public static void setBlockChannel(DyeChannel channel, Level world, BlockPos pos) {
        StorageBlockEntity sbe = (StorageBlockEntity) world.getBlockEntity(pos);
        sbe.setChannel(channel.clone());
    }

    public static void setBlockDye(int slot, int dye, Level world, BlockPos pos) {
        StorageBlockEntity sbe = (StorageBlockEntity) world.getBlockEntity(pos);
        sbe.setDye(slot, dye);
    }

    public static void setItemChannel(DyeChannel channel, ItemStack stack) {
        channel.clone().toTag(stack.getOrCreateTag());
    }

    public static DyeChannel getBlockChannel(Level world, BlockPos pos) {
        StorageBlockEntity sbe = (StorageBlockEntity) world.getBlockEntity(pos);
        return sbe.getChannel();
    }

    public static DyeChannel getItemChannel(ItemStack stack) {
        return DyeChannel.fromTag(stack.getOrCreateTag());
    }
}