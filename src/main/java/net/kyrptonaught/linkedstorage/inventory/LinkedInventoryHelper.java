package net.kyrptonaught.linkedstorage.inventory;

import net.kyrptonaught.linkedstorage.block.StorageBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class LinkedInventoryHelper {
    public static void setBlockChannel(byte[] channel, World world, BlockPos pos) {
        StorageBlockEntity sbe = (StorageBlockEntity) world.getBlockEntity(pos);
        sbe.setChannel(channel.clone());
    }

    public static void setBlockDye(int slot, int dye, World world, BlockPos pos) {
        StorageBlockEntity sbe = (StorageBlockEntity) world.getBlockEntity(pos);
        sbe.setDye(slot, dye);
    }

    public static void setItemChannel(byte[] channel, ItemStack stack) {
        stack.getOrCreateTag().putByteArray("dyechannel", channel.clone());
    }

    public static byte[] getBlockChannel(World world, BlockPos pos) {
        StorageBlockEntity sbe = (StorageBlockEntity) world.getBlockEntity(pos);
        return sbe.getChannel();
    }

    public static byte[] getItemChannel(ItemStack stack) {
        return stack.getOrCreateTag().getByteArray("dyechannel");
    }

    public static byte[] getDefaultChannel() {
        return new byte[]{(byte) DyeColor.WHITE.getId(), (byte) DyeColor.WHITE.getId(), (byte) DyeColor.WHITE.getId()};
    }

    public static String getChannelName(byte[] dyeChannel) {
        return dyeChannel[0] + "" + dyeChannel[1] + "" + dyeChannel[2];
    }

    public static Boolean itemHasChannel(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        if (tag.contains("dyechannel", 11)) {
            int[] oldChannel = tag.getIntArray("dyechannel").clone();
            tag.remove("dyechannel");
            tag.putByteArray("dyechannel", new byte[]{(byte) oldChannel[0], (byte) oldChannel[1], (byte) oldChannel[2]});
        }
        return tag.contains("dyechannel", 7);
    }
}