package net.kyrptonaught.linkedstorage.util;

import com.mojang.authlib.GameProfile;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class PlayerDyeChannel extends DyeChannel {
    public UUID playerID;
    private Component playerName;

    public PlayerDyeChannel(UUID playerID, byte[] dyeChannel) {
        super(dyeChannel);
        this.playerID = playerID;
        super.type = 1;
    }

    @Override
    public String getChannelName() {
        return playerID + ":" + super.getChannelName();
    }

    @Override
    public String getSaveName() {
        return super.getChannelName();
    }

    @Override
    public DyeChannel clone() {
        return new PlayerDyeChannel(playerID, dyeChannel.clone());
    }

    @Override
    @Environment(EnvType.CLIENT)
    public List<Component> getCleanName() {
        if (playerName == null) {
            IntegratedServer server = Minecraft.getInstance().getSingleplayerServer();
            Optional<GameProfile> player = Optional.empty();
            if (server != null)
                player = server.getProfileCache().get(playerID);
            playerName = player.isPresent() ? Component.literal(player.get().getName()) : Component.translatable("text.linkeditem.unknownplayerdyechannel");
        }
        ArrayList<Component> output = new ArrayList<>(super.getCleanName());
        output.add(0, Component.translatable("text.linkeditem.playerdyechannel", playerName));
        return output;
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        tag.putUUID("playerid", playerID);
        return super.toTag(tag);
    }
}
