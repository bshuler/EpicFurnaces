package com.songoda.epicfurnaces.furnace;

import com.songoda.epicfurnaces.PluginTestSupport;
import com.songoda.epicfurnaces.api.furnace.Level;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ELevelManagerTest {

    private ELevelManager manager;

    @BeforeAll
    static void loadPlugin() {
        PluginTestSupport.plugin();
    }

    @BeforeEach
    void setUp() {
        manager = new ELevelManager();
    }

    @Test
    void newManagerHasNoLevels() {
        assertTrue(manager.getLevels().isEmpty());
    }

    @Test
    void addLevelRegistersItByLevelNumber() {
        manager.addLevel(1, 20, 5000, 10, "10%:1", 0);

        Level level = manager.getLevel(1);
        assertEquals(1, level.getLevel());
        assertEquals(20, level.getCostExperiance());
        assertEquals(5000, level.getCostEconomy());
        assertEquals(10, level.getPerformance());
        assertEquals("10%:1", level.getReward());
        assertEquals(0, level.getFuelDuration());
    }

    @Test
    void getLevelForUnregisteredNumberIsNull() {
        assertNull(manager.getLevel(42));
    }

    @Test
    void lowestAndHighestLevelReflectRegisteredRange() {
        manager.addLevel(1, 20, 5000, 10, "10%:1", 0);
        manager.addLevel(2, 25, 7500, 25, "20%:1-2", 0);
        manager.addLevel(3, 30, 10000, 40, "35%:2-3", 10);

        assertEquals(1, manager.getLowestLevel().getLevel());
        assertEquals(3, manager.getHighestLevel().getLevel());
    }

    @Test
    void getLevelsIsUnmodifiable() {
        manager.addLevel(1, 20, 5000, 10, "10%:1", 0);
        assertTrue(manager.getLevels().containsKey(1));
        assertEquals(UnsupportedOperationException.class,
                assertThrowsUnsupportedOperationException(manager));
    }

    private static Class<?> assertThrowsUnsupportedOperationException(ELevelManager manager) {
        try {
            manager.getLevels().put(99, null);
        } catch (UnsupportedOperationException e) {
            return UnsupportedOperationException.class;
        }
        throw new AssertionError("Expected getLevels() to be unmodifiable");
    }

    @Test
    void clearRemovesAllRegisteredLevels() {
        manager.addLevel(1, 20, 5000, 10, "10%:1", 0);
        manager.clear();
        assertTrue(manager.getLevels().isEmpty());
    }
}
