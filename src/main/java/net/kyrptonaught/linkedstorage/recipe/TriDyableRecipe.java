package net.kyrptonaught.linkedstorage.recipe;

import net.kyrptonaught.linkedstorage.LinkedStorageMod;
import net.kyrptonaught.linkedstorage.block.StorageBlock;
import net.kyrptonaught.linkedstorage.item.StorageItem;
import net.kyrptonaught.linkedstorage.util.DyeChannel;
import net.kyrptonaught.linkedstorage.util.LinkedInventoryHelper;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class TriDyableRecipe extends CustomRecipe {
    public TriDyableRecipe(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(CraftingContainer inv, Level world) {
        Item center = inv.getItem(4).getItem();
        return (inv.getItem(0).getItem() instanceof DyeItem ||
                inv.getItem(1).getItem() instanceof DyeItem ||
                inv.getItem(2).getItem() instanceof DyeItem) &&
                (center instanceof StorageItem || (center instanceof BlockItem && ((BlockItem) center).getBlock() instanceof StorageBlock));
    }

    @Override
    public ItemStack assemble(CraftingContainer inv, RegistryAccess registryManager) {
        ItemStack newStack = inv.getItem(4).copy();
        DyeChannel dyeChannel = LinkedInventoryHelper.getItemChannel(newStack).clone();
        for (int i = 0; i < 3; i++)
            if (inv.getItem(i).getItem() instanceof DyeItem)
                dyeChannel.setSlot(i, (byte) ((DyeItem) inv.getItem(i).getItem()).getDyeColor().getId());
        LinkedInventoryHelper.setItemChannel(dyeChannel, newStack);
        return newStack;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return LinkedStorageMod.triDyeRecipe;
    }
}
