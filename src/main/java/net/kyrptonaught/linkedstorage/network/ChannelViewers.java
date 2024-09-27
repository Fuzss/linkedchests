package net.kyrptonaught.linkedstorage.network;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.kyrptonaught.linkedstorage.inventory.LinkedContainer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ChannelViewers {
    private static final ConcurrentHashMap<String, Set<UUID>> viewers = new ConcurrentHashMap<>();

    public static Boolean getViewersFor(String channel) {
        if (!viewers.containsKey(channel)) return false;
        return viewers.get(channel).size() > 0;
    }

    static void addViewerFor(String channel, UUID uuid) {
        if (!viewers.containsKey(channel)) viewers.put(channel, ConcurrentHashMap.newKeySet());
        viewers.get(channel).add(uuid);
    }

    public static void addViewerFor(String channel, Player player) {
        addViewerFor(channel, player.getUUID());
        if (!player.level().isClientSide)
            UpdateViewerList.sendPacket(player.getServer(), channel, player.getUUID(), true);
    }

    static void removeViewerFor(String channel, UUID player) {
        viewers.getOrDefault(channel, ConcurrentHashMap.newKeySet()).remove(player);
    }

    private static void removeViewerForServer(String channel, UUID player, MinecraftServer server) {
        removeViewerFor(channel, player);
        UpdateViewerList.sendPacket(server, channel, player, false);
    }

    public static void registerChannelWatcher() {
        ServerTickEvents.START_SERVER_TICK.register(server -> {
            for (String channel : ChannelViewers.viewers.keySet())
                for (UUID uuid : ChannelViewers.viewers.get(channel)) {
                    Player player = server.getPlayerList().getPlayer(uuid);
                    if (player == null || !(player.containerMenu instanceof LinkedContainer)) {
                        removeViewerForServer(channel, uuid, server);
                    }
                }
        });
    }
}