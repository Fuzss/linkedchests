package net.kyrptonaught.linkedstorage.register;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.kyrptonaught.linkedstorage.LinkedStorageMod;
import net.kyrptonaught.linkedstorage.item.StorageItem;
import net.minecraft.world.item.Item;

public class ModItems {
    public static Item storageItem;

    public static void register() {
        storageItem = new StorageItem(new Item.Properties().stacksTo(1));
    }
}
