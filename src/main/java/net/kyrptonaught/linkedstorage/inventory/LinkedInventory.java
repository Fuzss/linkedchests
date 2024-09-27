package net.kyrptonaught.linkedstorage.inventory;

import net.minecraft.core.Direction;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.ItemStack;

public class LinkedInventory extends SimpleContainer implements WorldlyContainer {

    public LinkedInventory() {
        super(27);
    }

    @Override
    public int[] getSlotsForFace(Direction var1) {
        int[] result = new int[getContainerSize()];
        for (int i = 0; i < result.length; i++) {
            result[i] = i;
        }
        return result;

    }

    @Override
    public boolean canPlaceItemThroughFace(int var1, ItemStack var2, Direction var3) {
        return true;
    }

    @Override
    public boolean canTakeItemThroughFace(int var1, ItemStack var2, Direction var3) {
        return true;
    }

    public LinkedInventory copy() {
        LinkedInventory copy = new LinkedInventory();

        for (int i = 0; i < this.getContainerSize(); i++)
            copy.setItem(i, this.getItem(i).copy());

        return copy;
    }
}