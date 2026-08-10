package com.songoda.epicfurnaces.api.furnace;

import java.util.List;

public interface Level {
    List<String> getDescription();

    int getLevel();

    int getPerformance();

    String getReward();

    int getFuelDuration();

    int getCostExperiance();

    int getCostEconomy();
}
