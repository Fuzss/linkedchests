package net.kyrptonaught.linkedstorage.recipe;

import com.google.gson.JsonObject;
import net.kyrptonaught.linkedstorage.LinkedStorageMod;
import net.kyrptonaught.linkedstorage.util.LinkedInventoryHelper;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;

public class CopyDyeRecipe extends ShapedRecipe {

    public CopyDyeRecipe(ShapedRecipe shapedRecipe) {
        super(shapedRecipe.getId(), "linkedstorage", shapedRecipe.category(), shapedRecipe.getWidth(), shapedRecipe.getHeight(), shapedRecipe.getIngredients(), shapedRecipe.getResultItem(null));
    }

    @Override
    public ItemStack assemble(CraftingContainer inv, RegistryAccess dynamicRegistryManager) {
        ItemStack output = this.getResultItem(dynamicRegistryManager).copy();
        LinkedInventoryHelper.setItemChannel(LinkedInventoryHelper.getItemChannel(inv.getItem(4)), output);
        return output;
    }

    public RecipeSerializer<?> getSerializer() {
        return LinkedStorageMod.copyDyeRecipe;
    }

    public static class Serializer implements RecipeSerializer<CopyDyeRecipe> {

        @Override
        public CopyDyeRecipe fromJson(ResourceLocation id, JsonObject json) {
            return new CopyDyeRecipe(ShapedRecipe.Serializer.SHAPED_RECIPE.fromJson(id, json));
        }

        @Override
        public CopyDyeRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            return new CopyDyeRecipe(ShapedRecipe.Serializer.SHAPED_RECIPE.fromNetwork(id, buf));
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, CopyDyeRecipe recipe) {
            ShapedRecipe.Serializer.SHAPED_RECIPE.toNetwork(buf, recipe);
        }
    }
}
