package net.kyrptonaught.linkedstorage.network;

import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.kyrptonaught.linkedstorage.LinkedStorageMod;
import net.kyrptonaught.linkedstorage.inventory.LinkedContainer;
import net.kyrptonaught.linkedstorage.util.LinkedInventoryHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public class OpenStoragePacket {
    private static final ResourceLocation OPEN_STORAGE_PACKET = new ResourceLocation(LinkedStorageMod.MOD_ID, "openpacket");

    public static void registerReceivePacket() {
        ServerPlayNetworking.registerGlobalReceiver(OPEN_STORAGE_PACKET, (server, player, handler, buf, responseSender) -> {
            BlockPos pos = buf.readBlockPos();
            server.execute(() -> {
                Level world = player.getCommandSenderWorld();
                player.openMenu(LinkedContainer.createScreenHandlerFactory(LinkedInventoryHelper.getBlockChannel(world, pos)));
            });
        });
    }

    @Environment(EnvType.CLIENT)
    public static void sendPacket(BlockPos pos) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeBlockPos(pos);
        Minecraft.getInstance().getConnection().getConnection().send(new ServerboundCustomPayloadPacket(OPEN_STORAGE_PACKET, new FriendlyByteBuf(buf)));
    }
}
