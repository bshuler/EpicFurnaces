package com.songoda.epicfurnaces.furnace;

import com.songoda.epicfurnaces.PluginTestSupport;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ELevelTest {

    @BeforeAll
    static void loadPlugin() {
        PluginTestSupport.plugin();
    }

    @Test
    void gettersReflectConstructorArguments() {
        ELevel level = new ELevel(3, 30, 10000, 40, "35%:2-3", 10);

        assertEquals(3, level.getLevel());
        assertEquals(30, level.getCostExperiance());
        assertEquals(10000, level.getCostEconomy());
        assertEquals(40, level.getPerformance());
        assertEquals("35%:2-3", level.getReward());
        assertEquals(10, level.getFuelDuration());
    }

    @Test
    void descriptionOmitsLinesForZeroOrNullFields() {
        ELevel level = new ELevel(1, 0, 0, 0, null, 0);

        assertTrue(level.getDescription().isEmpty());
    }

    @Test
    void descriptionIncludesALineForEachNonZeroOrNonNullField() {
        ELevel level = new ELevel(1, 20, 5000, 10, "10%:1", 5);

        // One description line each for performance, reward, and fuel
        // duration (all three are set on this level).
        assertEquals(3, level.getDescription().size());
    }

    @Test
    void descriptionIncludesOnlyPerformanceWhenOnlyPerformanceIsSet() {
        ELevel level = new ELevel(1, 20, 5000, 10, null, 0);

        assertEquals(1, level.getDescription().size());
    }

    @Test
    void getDescriptionReturnsAnIndependentCopyEachCall() {
        ELevel level = new ELevel(1, 20, 5000, 10, "10%:1", 5);

        var first = level.getDescription();
        first.clear();

        assertEquals(3, level.getDescription().size());
    }

    @Test
    void rewardIsNullByDefaultWhenNotProvided() {
        ELevel level = new ELevel(1, 0, 0, 0, null, 0);

        assertNull(level.getReward());
    }
}
