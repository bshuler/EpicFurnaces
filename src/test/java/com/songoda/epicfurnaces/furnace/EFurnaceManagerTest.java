package com.songoda.epicfurnaces.furnace;

import com.songoda.epicfurnaces.PluginTestSupport;
import com.songoda.epicfurnaces.api.furnace.Furnace;
import org.bukkit.Location;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EFurnaceManagerTest {

    private EFurnaceManager manager;

    @BeforeAll
    static void loadPlugin() {
        PluginTestSupport.plugin();
    }

    @BeforeEach
    void setUp() {
        manager = new EFurnaceManager();
    }

    @Test
    void newManagerHasNoFurnaces() {
        assertTrue(manager.getFurnaces().isEmpty());
    }

    @Test
    void addFurnaceThenGetFurnaceReturnsTheSameInstance() {
        Location location = new Location(PluginTestSupport.world(), 5, 64, 5);
        Furnace furnace = new EFurnace(location, PluginTestSupport.plugin().getLevelManager().getLowestLevel(), null, 0, 0, new ArrayList<>());

        manager.addFurnace(location, furnace);

        assertSame(furnace, manager.getFurnace(location));
    }

    @Test
    void getFurnaceForUnregisteredLocationAutoCreatesOneAtTheLowestLevel() {
        Location location = new Location(PluginTestSupport.world(), 1, 65, 1);

        Furnace created = manager.getFurnace(location);

        assertEquals(PluginTestSupport.plugin().getLevelManager().getLowestLevel(), created.getLevel());
        assertTrue(manager.getFurnaces().containsValue(created));
    }

    @Test
    void getFurnaceRoundsFractionalLocationsToTheSameBlockKeyAsAddFurnace() {
        // Regression test for a real bug: addFurnace() always rounds its key
        // to whole block coordinates, but getFurnace() used to look up the
        // raw, unrounded Location it was given. A fractional Location
        // pointing at the same block used to silently miss the
        // already-registered furnace and create a duplicate. See PLAN.md.
        Location wholeBlock = new Location(PluginTestSupport.world(), 2, 64, 3);
        Furnace furnace = new EFurnace(wholeBlock, PluginTestSupport.plugin().getLevelManager().getLowestLevel(), null, 7, 0, new ArrayList<>());
        manager.addFurnace(wholeBlock, furnace);

        Location sameBlockFractional = new Location(PluginTestSupport.world(), 2.7, 64.9, 3.4);

        assertSame(furnace, manager.getFurnace(sameBlockFractional));
        assertEquals(1, manager.getFurnaces().size());
    }

    @Test
    void removeFurnaceRoundsItsLocationTooAndReturnsTheRemovedFurnace() {
        Location wholeBlock = new Location(PluginTestSupport.world(), 10, 64, 10);
        Furnace furnace = new EFurnace(wholeBlock, PluginTestSupport.plugin().getLevelManager().getLowestLevel(), null, 0, 0, new ArrayList<>());
        manager.addFurnace(wholeBlock, furnace);

        Location sameBlockFractional = new Location(PluginTestSupport.world(), 10.3, 64.1, 10.9);
        Furnace removed = manager.removeFurnace(sameBlockFractional);

        assertSame(furnace, removed);
        assertTrue(manager.getFurnaces().isEmpty());
    }

    @Test
    void removeFurnaceForUnknownLocationReturnsNull() {
        assertNull(manager.removeFurnace(new Location(PluginTestSupport.world(), 99, 99, 99)));
    }

    @Test
    void getFurnacesIsUnmodifiable() {
        Location location = new Location(PluginTestSupport.world(), 0, 0, 0);
        manager.getFurnace(location);

        expectUnmodifiable(manager);
    }

    private static void expectUnmodifiable(EFurnaceManager manager) {
        try {
            manager.getFurnaces().put(new Location(PluginTestSupport.world(), 1, 1, 1), null);
        } catch (UnsupportedOperationException expected) {
            return;
        }
        throw new AssertionError("Expected getFurnaces() to be unmodifiable");
    }

    @Test
    void getFurnaceByBlockDelegatesToLocationOverload() {
        Location location = new Location(PluginTestSupport.world(), 8, 64, 8);
        Furnace viaLocation = manager.getFurnace(location);

        Furnace viaBlock = manager.getFurnace(location.getBlock());

        assertSame(viaLocation, viaBlock);
    }
}
