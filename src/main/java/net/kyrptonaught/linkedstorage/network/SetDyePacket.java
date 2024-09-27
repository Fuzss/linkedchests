package net.kyrptonaught.linkedstorage.network;

import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.kyrptonaught.linkedstorage.LinkedStorageMod;
import net.kyrptonaught.linkedstorage.util.LinkedInventoryHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeItem;

public class SetDyePacket {
    private static final ResourceLocation DYE_SET_PACKET = new ResourceLocation(LinkedStorageMod.MOD_ID, "dyesetpacket");

    public static void registerReceivePacket() {
        ServerPlayNetworking.registerGlobalReceiver(DYE_SET_PACKET, (server, player, handler, buf, responseSender) -> {
            int slot = buf.readInt();
            BlockPos pos = buf.readBlockPos();
            server.execute(() -> {
                int dye = ((DyeItem) player.getMainHandItem().getItem()).getDyeColor().getId();
                LinkedInventoryHelper.setBlockDye(slot, dye, player.getCommandSenderWorld(), pos);
                if (!player.isCreative())
                    player.getMainHandItem().shrink(1);
            });
        });
    }

    @Environment(EnvType.CLIENT)
    public static void sendPacket(int slot, BlockPos pos) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeInt(slot);
        buf.writeBlockPos(pos);
        Minecraft.getInstance().getConnection().getConnection().send(new ServerboundCustomPayloadPacket(DYE_SET_PACKET, new FriendlyByteBuf(buf)));
    }
}
