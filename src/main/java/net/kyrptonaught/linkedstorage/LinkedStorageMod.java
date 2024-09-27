package net.kyrptonaught.linkedstorage;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.kyrptonaught.linkedstorage.inventory.LinkedContainer;
import net.kyrptonaught.linkedstorage.inventory.LinkedInventory;
import net.kyrptonaught.linkedstorage.network.ChannelViewers;
import net.kyrptonaught.linkedstorage.network.OpenStoragePacket;
import net.kyrptonaught.linkedstorage.network.SetDyePacket;
import net.kyrptonaught.linkedstorage.recipe.CopyDyeRecipe;
import net.kyrptonaught.linkedstorage.recipe.TriDyableRecipe;
import net.kyrptonaught.linkedstorage.register.ModBlocks;
import net.kyrptonaught.linkedstorage.register.ModItems;
import net.kyrptonaught.linkedstorage.util.ChannelManager;
import net.kyrptonaught.linkedstorage.util.DyeChannel;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraft.world.level.Level;

public class LinkedStorageMod implements ModInitializer {
    public static final String MOD_ID = "linkedstorage";

    private static final ResourceKey<CreativeModeTab> ITEM_GROUP = ResourceKey.create(Registries.CREATIVE_MODE_TAB, new ResourceLocation(MOD_ID, "linkedstorage"));
    public static SimpleCraftingRecipeSerializer<TriDyableRecipe> triDyeRecipe;
    public static RecipeSerializer<CopyDyeRecipe> copyDyeRecipe;
    private static ChannelManager CMAN; //lol

    public static final MenuType<LinkedContainer> LINKED_SCREEN_HANDLER_TYPE = ScreenHandlerRegistry.registerExtended(new ResourceLocation(MOD_ID, "linkedstorage"), LinkedContainer::new);

    @Override
    public void onInitialize() {
        ModBlocks.register();
        ModItems.register();
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, ITEM_GROUP,
                FabricItemGroup.builder()
                        .title(Component.translatable("itemGroup.linkedstorage.linkedstorage"))
                        .icon(() -> new ItemStack(ModBlocks.storageBlock))
                        .displayItems((context, entries) -> {
                            entries.accept(ModBlocks.storageBlock);
                            entries.accept(ModItems.storageItem);
                        })
                        .build());
        SetDyePacket.registerReceivePacket();
        OpenStoragePacket.registerReceivePacket();
        ChannelViewers.registerChannelWatcher();
        triDyeRecipe = Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, new ResourceLocation(MOD_ID, "tri_dyable_recipe"), new SimpleCraftingRecipeSerializer<>(TriDyableRecipe::new));
        copyDyeRecipe = Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, new ResourceLocation(MOD_ID, "copy_dye_recipe"), new CopyDyeRecipe.Serializer());
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            CMAN = (ChannelManager) server.getLevel(Level.OVERWORLD).getDataStorage().computeIfAbsent(ChannelManager::fromNbt, ChannelManager::new, MOD_ID);
        });
    }

    public static LinkedInventory getInventory(DyeChannel dyeChannel) {
        if (CMAN == null) return new LinkedInventory();
        return CMAN.getInv(dyeChannel);
    }
}
