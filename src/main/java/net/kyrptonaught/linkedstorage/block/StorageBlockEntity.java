package net.kyrptonaught.linkedstorage.block;

import com.google.common.base.Preconditions;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.kyrptonaught.linkedstorage.LinkedStorageMod;
import net.kyrptonaught.linkedstorage.inventory.LinkedInventory;
import net.kyrptonaught.linkedstorage.network.ChannelViewers;
import net.kyrptonaught.linkedstorage.util.DyeChannel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class StorageBlockEntity extends OpenableBlockEntity {
    private DyeChannel dyeChannel = DyeChannel.defaultChannel();
    private LinkedInventory linkedInventory;

    StorageBlockEntity(BlockPos pos, BlockState state) {
        super(StorageBlock.blockEntity, pos, state);
    }

    StorageBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void load(CompoundTag compoundTag_1) {
        super.load(compoundTag_1);
        dyeChannel = DyeChannel.fromTag(compoundTag_1);
        this.setChanged();
    }

    @Override
    public void saveAdditional(CompoundTag compoundTag_1) {
        super.saveAdditional(compoundTag_1);
        dyeChannel.toTag(compoundTag_1);
    }

    LinkedInventory getLinkedInventory() {
        if (linkedInventory == null) updateInventory();
        return linkedInventory;
    }

    private void updateInventory() {
        if (!level.isClientSide) {
            linkedInventory = LinkedStorageMod.getInventory(dyeChannel);
        }
    }

    public void setDye(int slot, int dye) {
        dyeChannel.setSlot(slot, (byte) dye);
        updateInventory();
        this.setChanged();
        if (!level.isClientSide) sync();
    }

    public void setChannel(DyeChannel channel) {
        this.dyeChannel = channel;
        updateInventory();
        this.setChanged();
        if (!level.isClientSide) sync();
    }

    public DyeChannel getChannel() {
        return dyeChannel;
    }

    @Override
    public final ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public final CompoundTag getUpdateTag() {
        CompoundTag nbt = super.getUpdateTag();
        saveAdditional(nbt);
        return nbt;
    }

    // Thank you Fabric API
    public void sync() {
        Preconditions.checkNotNull(level); // Maintain distinct failure case from below
        if (!(level instanceof ServerLevel))
            throw new IllegalStateException("Cannot call sync() on the logical client! Did you check world.isClient first?");

        ((ServerLevel) level).getChunkSource().blockChanged(getBlockPos());
    }

    @Override
    @Environment(EnvType.CLIENT)
    public int countViewers() {
        return ChannelViewers.getViewersFor(dyeChannel.getChannelName()) ? 1 : 0;
    }
}