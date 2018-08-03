package com.songoda.epicfurnaces.furnace;

import com.songoda.epicfurnaces.EpicFurnaces;

import java.util.ArrayList;
import java.util.List;

public class Level {

    private int level, costExperiance, costEconomy, performance, fuelDuration;

    private String reward;

    private List<String> description = new ArrayList<>();

    public Level(int level, int costExperiance, int costEconomy, int performance, String reward, int fuelDuration) {
        this.level = level;
        this.costExperiance = costExperiance;
        this.costEconomy = costEconomy;
        this.performance = performance;
        this.reward = reward;
        this.fuelDuration = fuelDuration;

        EpicFurnaces instance = EpicFurnaces.getInstance();

        if (performance != 0)
            description.add(instance.getLocale().getMessage("interface.furnace.performance", performance + "%"));

        if (reward != null)
            description.add(instance.getLocale().getMessage("interface.furnace.reward", reward.split("%:")[0] + "%"));

        if (fuelDuration != 0)
            description.add(instance.getLocale().getMessage("interface.furnace.fuelduration", fuelDuration + "%"));
    }

    public List<String> getDescription() {
        return new ArrayList<>(description);
    }

    public int getLevel() {
        return level;
    }

    public int getPerformance() {
        return performance;
    }

    public String getReward() {
        return reward;
    }

    public int getFuelDuration() {
        return fuelDuration;
    }

    public int getCostExperiance() {
        return costExperiance;
    }

    public int getCostEconomy() {
        return costEconomy;
    }
}
