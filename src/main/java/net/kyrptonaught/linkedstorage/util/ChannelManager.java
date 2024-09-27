package net.kyrptonaught.linkedstorage.util;

import net.kyrptonaught.linkedstorage.inventory.LinkedInventory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;
import java.util.HashMap;
import java.util.UUID;

public class ChannelManager extends SavedData {
    public final static String SAVEVERSION = "2.0";
    private final InventoryStorage globalInventories = new InventoryStorage("GLOBAL");
    private final HashMap<UUID, InventoryStorage> personalInventories = new HashMap<>();

    public ChannelManager() {
        super();
    }

    public static SavedData fromNbt(CompoundTag tag) {
        ChannelManager cman = new ChannelManager();
        cman.globalInventories.fromTag(tag);
        cman.personalInventories.clear();
        CompoundTag personalInvs = tag.getCompound("personalInvs");
        personalInvs.getAllKeys().forEach(uuid -> {
            InventoryStorage personalInv = new InventoryStorage(uuid);
            personalInv.fromTag(personalInvs.getCompound(uuid));
            cman.personalInventories.put(UUID.fromString(uuid), personalInv);
        });
        String savedVersion = tag.getString("saveVersion");
        if (!savedVersion.equals(SAVEVERSION)) Migrator.Migrate(cman, savedVersion);
        return cman;
    }

    public CompoundTag save(CompoundTag tag) {
        globalInventories.toTag(tag);
        CompoundTag personalInvs = new CompoundTag();
        personalInventories.values().forEach(inventoryStorage -> {
            if (inventoryStorage.getInventories().size() > 0)
                personalInvs.put(inventoryStorage.name, inventoryStorage.toTag(new CompoundTag()));
        });
        tag.put("personalInvs", personalInvs);
        tag.putString("saveVersion", SAVEVERSION);
        return tag;
    }

    public LinkedInventory getInv(DyeChannel dyeChannel) {
        if (dyeChannel instanceof PlayerDyeChannel)
            return getPersonalInv((PlayerDyeChannel) dyeChannel);
        return globalInventories.getInv(dyeChannel);
    }

    public LinkedInventory getPersonalInv(PlayerDyeChannel dyeChannel) {
        if (!personalInventories.containsKey(dyeChannel.playerID))
            personalInventories.put(dyeChannel.playerID, new InventoryStorage(dyeChannel.playerID.toString()));
        return personalInventories.get(dyeChannel.playerID).getInv(dyeChannel);
    }

    public InventoryStorage getGlobalInventories() {
        return globalInventories;
    }

    public HashMap<UUID, InventoryStorage> getPersonalInventories() {
        return personalInventories;
    }

    @Override
    public boolean isDirty() {
        return true;
    }
}