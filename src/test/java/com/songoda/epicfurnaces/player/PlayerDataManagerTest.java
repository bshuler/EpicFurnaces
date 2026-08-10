package com.songoda.epicfurnaces.player;

import com.songoda.epicfurnaces.PluginTestSupport;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlayerDataManagerTest {

    private PlayerDataManager manager;

    @BeforeAll
    static void loadPlugin() {
        PluginTestSupport.plugin();
    }

    @BeforeEach
    void setUp() {
        manager = new PlayerDataManager();
    }

    @Test
    void newManagerHasNoRegisteredPlayers() {
        assertTrue(manager.getRegisteredPlayers().isEmpty());
    }

    @Test
    void getPlayerDataCreatesAndCachesByUUID() {
        UUID uuid = UUID.randomUUID();

        PlayerData first = manager.getPlayerData(uuid);
        PlayerData second = manager.getPlayerData(uuid);

        assertSame(first, second);
        assertEquals(uuid, first.getPlayerUUID());
        assertTrue(manager.getRegisteredPlayers().contains(first));
    }

    @Test
    void getPlayerDataForNullUUIDReturnsNull() {
        assertNull(manager.getPlayerData((UUID) null));
    }

    @Test
    void getPlayerDataByPlayerDelegatesToTheirUUID() {
        var player = PluginTestSupport.server().addPlayer();

        PlayerData data = manager.getPlayerData(player);

        assertEquals(player.getUniqueId(), data.getPlayerUUID());
        assertSame(data, manager.getPlayerData(player.getUniqueId()));
    }

    @Test
    void getRegisteredPlayersIsUnmodifiable() {
        manager.getPlayerData(UUID.randomUUID());

        try {
            manager.getRegisteredPlayers().clear();
        } catch (UnsupportedOperationException expected) {
            return;
        }
        throw new AssertionError("Expected getRegisteredPlayers() to be unmodifiable");
    }
}
