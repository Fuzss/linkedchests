package net.kyrptonaught.linkedstorage.inventory;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.kyrptonaught.linkedstorage.LinkedStorageMod;
import net.kyrptonaught.linkedstorage.item.StorageItem;
import net.kyrptonaught.linkedstorage.network.ChannelViewers;
import net.kyrptonaught.linkedstorage.util.DyeChannel;
import net.kyrptonaught.linkedstorage.util.LinkedInventoryHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;

public class LinkedContainer extends ChestMenu {
    DyeChannel dyeChannel;

    public LinkedContainer(int syncId, Inventory playerInventory, DyeChannel channel) {
        this(syncId, playerInventory, LinkedStorageMod.getInventory(channel));
        this.dyeChannel = channel;
    }

    public LinkedContainer(int syncId, Inventory playerInventory, Container inventory) {
        super(LinkedStorageMod.LINKED_SCREEN_HANDLER_TYPE, syncId, playerInventory, inventory, 3);
    }

    public LinkedContainer(int syncId, Inventory playerInventory, FriendlyByteBuf buf) {
        this(syncId, playerInventory, new LinkedInventory());
        dyeChannel = DyeChannel.fromBuf(buf);
    }

    @Override
    public void clicked(int slotId, int clickData, ClickType actionType, Player player) {
        if (slotId > -1 && this.getSlot(slotId).getItem().getItem() instanceof StorageItem && getSlot(slotId).container instanceof Inventory)
            if (dyeChannel.equals(LinkedInventoryHelper.getItemChannel(getSlot(slotId).getItem())))
                return;

        super.clicked(slotId, clickData, actionType, player);
    }

    public static ExtendedScreenHandlerFactory createScreenHandlerFactory(DyeChannel channel) {
        return new ExtendedScreenHandlerFactory() {
            @Override
            public AbstractContainerMenu createMenu(int syncId, Inventory inventory, Player player) {
                ChannelViewers.addViewerFor(channel.getChannelName(), player);
                return new LinkedContainer(syncId, inventory, channel);
            }

            @Override
            public Component getDisplayName() {
                return Component.translatable("container.linkedstorage");
            }

            @Override
            public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
                channel.toBuf(buf);
            }
        };
    }

    @Override
    public MenuType<?> getType() {
        return LinkedStorageMod.LINKED_SCREEN_HANDLER_TYPE;
    }
}
