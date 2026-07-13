package fuzs.linkedchests.fabric;

import fuzs.linkedchests.common.LinkedChests;
import fuzs.linkedchests.common.init.ModRegistry;
import fuzs.puzzleslib.common.api.core.v1.ModConstructor;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.recipe.v1.sync.RecipeSynchronization;

public class LinkedChestsFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        ModConstructor.construct(LinkedChests.MOD_ID, LinkedChests::new);
        RecipeSynchronization.synchronizeRecipeSerializer(ModRegistry.SHAPED_DYE_CHANNEL_RECIPE_SERIALIZER.value());
    }
}
