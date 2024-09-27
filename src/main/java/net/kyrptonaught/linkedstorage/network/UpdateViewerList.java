package net.kyrptonaught.linkedstorage.network;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.kyrptonaught.linkedstorage.LinkedStorageMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import java.util.UUID;

public class UpdateViewerList {
    private static final ResourceLocation UPDATE_VIEWERS = new ResourceLocation(LinkedStorageMod.MOD_ID, "updateviewers");

    public static void registerReceivePacket() {
        ClientPlayNetworking.registerGlobalReceiver(UPDATE_VIEWERS, (client, handler, buf, responseSender) -> {
            String channel = buf.readUtf(32767);
            UUID uuid = buf.readUUID();
            boolean adding = buf.readBoolean();
            client.execute(() -> {
                if (adding)
                    ChannelViewers.addViewerFor(channel, uuid);
                else
                    ChannelViewers.removeViewerFor(channel, uuid);
            });
        });
    }

    static void sendPacket(MinecraftServer server, String channel, UUID uuid, Boolean add) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeUtf(channel);
        buf.writeUUID(uuid);
        buf.writeBoolean(add);
        server.getPlayerList().broadcastAll(new ClientboundCustomPayloadPacket(UPDATE_VIEWERS, new FriendlyByteBuf(buf)));
    }
}
