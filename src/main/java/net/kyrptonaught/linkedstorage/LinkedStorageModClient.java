package net.kyrptonaught.linkedstorage;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.impl.client.rendering.ColorProviderRegistryImpl;
import net.fabricmc.fabric.impl.client.rendering.EntityModelLayerImpl;
import net.fabricmc.loader.api.FabricLoader;
import net.kyrptonaught.linkedstorage.block.StorageBlock;
import net.kyrptonaught.linkedstorage.client.LinkedChestModel;
import net.kyrptonaught.linkedstorage.client.StorageBlockRenderer;
import net.kyrptonaught.linkedstorage.network.ChannelViewers;
import net.kyrptonaught.linkedstorage.network.UpdateViewerList;
import net.kyrptonaught.linkedstorage.register.ModBlocks;
import net.kyrptonaught.linkedstorage.register.ModItems;
import net.kyrptonaught.linkedstorage.util.DyeChannel;
import net.kyrptonaught.linkedstorage.util.LinkedInventoryHelper;
import net.kyrptonaught.linkedstorage.util.PlayerDyeChannel;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;


@Environment(EnvType.CLIENT)
public class LinkedStorageModClient implements ClientModInitializer {
    public static final ResourceLocation TEXTURE = new ResourceLocation(LinkedStorageMod.MOD_ID, "block/linkedstorage");
    public static final ModelLayerLocation LINKEDCHESTMODELLAYER = new ModelLayerLocation(new ResourceLocation(LinkedStorageMod.MOD_ID, "linkedchest"), "main");

    @Override
    public void onInitializeClient() {
        EntityModelLayerImpl.PROVIDERS.put(LINKEDCHESTMODELLAYER, LinkedChestModel::getTexturedModelData);
        BlockEntityRendererRegistry.register(StorageBlock.blockEntity, StorageBlockRenderer::new);
        FabricModelPredicateProviderRegistry.register(ModItems.storageItem, new ResourceLocation("open"), (stack, world, entity, seed) -> {
            String channel = LinkedInventoryHelper.getItemChannel(stack).getChannelName();
            return ChannelViewers.getViewersFor(channel) ? 1 : 0;
        });
        ScreenRegistry.register(LinkedStorageMod.LINKED_SCREEN_HANDLER_TYPE, ContainerScreen::new);
        ColorProviderRegistryImpl.ITEM.register((stack, layer) -> {
            DyeChannel dyeChannel = LinkedInventoryHelper.getItemChannel(stack);
            if (layer > 0 && layer < 4) {
                byte[] colors = dyeChannel.dyeChannel;
                return DyeColor.byId(colors[layer - 1]).getMapColor().col;
            }
            if (layer == 4 && dyeChannel instanceof PlayerDyeChannel)
                return DyeColor.LIGHT_BLUE.getMapColor().col;
            return DyeColor.WHITE.getMapColor().col;
        }, ModItems.storageItem, ModBlocks.storageBlock);
        UpdateViewerList.registerReceivePacket();
        FabricLoader.getInstance().getModContainer(LinkedStorageMod.MOD_ID).ifPresent(modContainer -> {
            ResourceManagerHelper.registerBuiltinResourcePack(new ResourceLocation(LinkedStorageMod.MOD_ID, "enderstorage"), "resourcepacks/enderstorage", modContainer, false);
        });
    }

}
