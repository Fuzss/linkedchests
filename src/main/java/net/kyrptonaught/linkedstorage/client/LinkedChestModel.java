package net.kyrptonaught.linkedstorage.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.kyrptonaught.linkedstorage.LinkedStorageModClient;
import net.minecraft.client.model.*;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

@Environment(EnvType.CLIENT)
public class LinkedChestModel extends Model {
    private final ModelPart lid;
    protected ModelPart base;
    public final ModelPart latch;
    public ModelPart button1, button2, button3;

    public LinkedChestModel(BlockEntityRendererProvider.Context ctx) {
        super(RenderType::entityCutout);
        ModelPart modelPart = ctx.bakeLayer(LinkedStorageModClient.LINKEDCHESTMODELLAYER);
        this.base = modelPart.getChild("bottom");
        this.lid = modelPart.getChild("lid");
        this.latch = modelPart.getChild("lock");
        this.button1 = modelPart.getChild("color1");
        this.button2 = modelPart.getChild("color2");
        this.button3 = modelPart.getChild("color3");
    }

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition modelPartData = modelData.getRoot();
        modelPartData.addOrReplaceChild("bottom", CubeListBuilder.create().texOffs(0, 19).addBox(1.0F, 0.0F, 1.0F, 14.0F, 10.0F, 14.0F), PartPose.ZERO);
        modelPartData.addOrReplaceChild("lid", CubeListBuilder.create().texOffs(0, 0).addBox(1.0F, 0.0F, 0.0F, 14.0F, 5.0F, 14.0F), PartPose.offset(0.0F, 9.0F, 1.0F));
        modelPartData.addOrReplaceChild("lock", CubeListBuilder.create().texOffs(0, 0).addBox(7.0F, -1.0F, 15.0F, 2.0F, 4.0F, 1.0F), PartPose.offset(0.0F, 8.0F, 0.0F));
        modelPartData.addOrReplaceChild("color1", CubeListBuilder.create().texOffs(0, 19).addBox(4, 5, 5, 2, 1, 4), PartPose.offset(0, 9f, 1f));
        modelPartData.addOrReplaceChild("color2", CubeListBuilder.create().texOffs(0, 19).addBox(7, 5, 5, 2, 1, 4), PartPose.offset(0, 9f, 1f));
        modelPartData.addOrReplaceChild("color3", CubeListBuilder.create().texOffs(0, 19).addBox(10, 5, 5, 2, 1, 4), PartPose.offset(0, 9f, 1f));

        return LayerDefinition.create(modelData, 64, 64);
    }

    public void setLidPitch(float pitch) {
        pitch = 1.0f - pitch;
        button1.xRot = button2.xRot = button3.xRot = latch.xRot = lid.xRot = -((1.0F - pitch * pitch * pitch) * 1.5707964F);
    }

    public void render(PoseStack matrixStack, VertexConsumer vertexConsumer, int i, int j) {
        renderToBuffer(matrixStack, vertexConsumer, i, j, 1, 1, 1, 1);
    }

    @Override
    public void renderToBuffer(PoseStack stack, VertexConsumer consumer, int i, int j, float r, float g, float b, float f) {
        base.render(stack, consumer, i, j);
        lid.render(stack, consumer, i, j);
    }
}