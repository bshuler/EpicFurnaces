package com.songoda.epicfurnaces.player;

import com.songoda.epicfurnaces.PluginTestSupport;
import com.songoda.epicfurnaces.furnace.EFurnace;
import org.bukkit.Location;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlayerDataTest {

    private UUID uuid;
    private PlayerData playerData;

    @BeforeAll
    static void loadPlugin() {
        PluginTestSupport.plugin();
    }

    @BeforeEach
    void setUp() {
        uuid = UUID.randomUUID();
        playerData = new PlayerData(uuid);
    }

    @Test
    void constructorStoresThePlayerUUID() {
        assertEquals(uuid, playerData.getPlayerUUID());
    }

    @Test
    void defaultsAreUnsetFalseAndNoFurnace() {
        assertNull(playerData.getLastFurace());
        assertFalse(playerData.isInOverview());
        assertFalse(playerData.isSettingNickname());
    }

    @Test
    void setLastFuraceIsReturnedByGetLastFurace() {
        EFurnace furnace = new EFurnace(new Location(PluginTestSupport.world(), 0, 0, 0),
                PluginTestSupport.plugin().getLevelManager().getLowestLevel(), null, 0, 0, new ArrayList<>());

        playerData.setLastFurace(furnace);

        assertSame(furnace, playerData.getLastFurace());
    }

    @Test
    void setInOverviewIsReturnedByIsInOverview() {
        playerData.setInOverview(true);
        assertTrue(playerData.isInOverview());

        playerData.setInOverview(false);
        assertFalse(playerData.isInOverview());
    }

    @Test
    void setSettingNicknameIsReturnedByIsSettingNickname() {
        playerData.setSettingNickname(true);
        assertTrue(playerData.isSettingNickname());

        playerData.setSettingNickname(false);
        assertFalse(playerData.isSettingNickname());
    }
}
