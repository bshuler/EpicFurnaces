package com.songoda.epicfurnaces.player;

import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerDataManager {

    // ConcurrentHashMap, not HashMap: see the identical note on
    // EFurnaceManager.registeredFurnaces (PLAN.md "Folia") - this map is
    // read/written from per-player listener callbacks (whichever region
    // thread owns that player on Folia) and iterated by
    // EpicFurnacesPlugin's periodic global-scheduler task.
    private final Map<UUID, PlayerData> registeredPlayers = new ConcurrentHashMap<>();

    public PlayerData getPlayerData(UUID uuid) {
        return (uuid != null) ? registeredPlayers.computeIfAbsent(uuid, PlayerData::new) : null;
    }

    public PlayerData getPlayerData(Player player) {
        return getPlayerData(player.getUniqueId());
    }

    public Collection<PlayerData> getRegisteredPlayers() {
        return Collections.unmodifiableCollection(registeredPlayers.values());
    }
}
