package net.kyrptonaught.linkedstorage.util;

import net.kyrptonaught.linkedstorage.inventory.LinkedInventory;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import java.util.HashMap;

public class InventoryStorage {
    private final HashMap<String, LinkedInventory> inventories = new HashMap<>();
    public String name;

    public InventoryStorage(String name) {
        this.name = name;
    }

    public void fromTag(CompoundTag tag) {
        inventories.clear();
        CompoundTag invs = tag.getCompound("invs");
        for (String key : invs.getAllKeys()) {
            inventories.put(key, fromList(invs.getCompound(key)));
        }
    }

    public CompoundTag toTag(CompoundTag tag) {
        CompoundTag invs = new CompoundTag();
        for (String key : inventories.keySet()) {
            if (!inventories.get(key).isEmpty())
                invs.put(key, ContainerHelper.saveAllItems(new CompoundTag(), toList(inventories.get(key))));
        }
        tag.put("invs", invs);
        return tag;
    }

    public LinkedInventory getInv(DyeChannel dyeChannel) {
        String channel = dyeChannel.getSaveName();
        if (!inventories.containsKey(channel))
            inventories.put(channel, new LinkedInventory());
        return inventories.get(channel);
    }

    private NonNullList<ItemStack> toList(Container inv) {
        NonNullList<ItemStack> stacks = NonNullList.withSize(inv.getContainerSize(), ItemStack.EMPTY);
        for (int i = 0; i < inv.getContainerSize(); i++)
            stacks.set(i, inv.getItem(i));
        return stacks;
    }

    private LinkedInventory fromList(CompoundTag tag) {
        LinkedInventory inventory = new LinkedInventory();
        NonNullList<ItemStack> stacks = NonNullList.withSize(inventory.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, stacks);
        for (int i = 0; i < stacks.size(); i++)
            inventory.setItem(i, stacks.get(i));
        return inventory;
    }

    public HashMap<String, LinkedInventory> getInventories() {
        return inventories;
    }
}
