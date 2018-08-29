package com.songoda.epicfurnaces.api.furnace;

import java.util.Map;

public interface LevelManager {
    void addLevel(int level, int costExperiance, int costEconomy, int performance, String reward, int fuelDuration);

    Level getLevel(int level);

    Level getLowestLevel();

    Level getHighestLevel();

    Map<Integer, Level> getLevels();

    void clear();
}
