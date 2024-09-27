package net.kyrptonaught.linkedstorage.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.kyrptonaught.linkedstorage.LinkedStorageModClient;
import net.kyrptonaught.linkedstorage.block.StorageBlock;
import net.kyrptonaught.linkedstorage.block.StorageBlockEntity;
import net.kyrptonaught.linkedstorage.util.PlayerDyeChannel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

@Environment(EnvType.CLIENT)
public class StorageBlockRenderer implements BlockEntityRenderer<StorageBlockEntity> {
    private static final ResourceLocation WOOL_TEXTURE = new ResourceLocation("textures/block/white_wool.png");
    private static final ResourceLocation DIAMOND_TEXTURE = new ResourceLocation("textures/block/diamond_block.png");
    LinkedChestModel model;

    public StorageBlockRenderer(BlockEntityRendererProvider.Context ctx) {
        model = new LinkedChestModel(ctx);
    }

    @Override
    public void render(StorageBlockEntity blockEntity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        byte[] dyes = blockEntity.getChannel().dyeChannel;
        float[] color1 = DyeColor.byId(dyes[0]).getTextureDiffuseColors();
        float[] color2 = DyeColor.byId(dyes[1]).getTextureDiffuseColors();
        float[] color3 = DyeColor.byId(dyes[2]).getTextureDiffuseColors();

        Level world = blockEntity.getLevel();
        BlockPos pos = blockEntity.getBlockPos();
        BlockState state = world.getBlockState(pos);

        //fixes crash with carpet
        if (state.getBlock() instanceof StorageBlock) {
            matrices.pushPose();
            float f = state.getValue(StorageBlock.FACING).toYRot();
            matrices.translate(0.5D, 0.5D, 0.5D);
            matrices.mulPose(Axis.YP.rotationDegrees(-f));
            matrices.translate(-0.5D, -0.5D, -0.5D);

            model.setLidPitch(blockEntity.getOpenNess(tickDelta));
            Material spriteIdentifier = new Material(Sheets.CHEST_SHEET, LinkedStorageModClient.TEXTURE);
            VertexConsumer vertexConsumer = spriteIdentifier.buffer(vertexConsumers, RenderType::entityCutout);
            model.render(matrices, vertexConsumer, light, overlay);

            model.button1.render(matrices, vertexConsumers.getBuffer(RenderType.entityCutout(WOOL_TEXTURE)), light, overlay, color1[0], color1[1], color1[2], 1);
            model.button2.render(matrices, vertexConsumers.getBuffer(RenderType.entityCutout(WOOL_TEXTURE)), light, overlay, color2[0], color2[1], color2[2], 1);
            model.button3.render(matrices, vertexConsumers.getBuffer(RenderType.entityCutout(WOOL_TEXTURE)), light, overlay, color3[0], color3[1], color3[2], 1);

            if (blockEntity.getChannel() instanceof PlayerDyeChannel)
                model.latch.render(matrices, vertexConsumers.getBuffer(RenderType.entityCutout(DIAMOND_TEXTURE)), light, overlay, 1, 1, 1, 1);
            else
                model.latch.render(matrices, vertexConsumer, light, overlay);
            matrices.popPose();
        }
    }
}
