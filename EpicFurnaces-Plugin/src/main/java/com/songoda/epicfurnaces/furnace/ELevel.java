package com.songoda.epicfurnaces.furnace;

import com.songoda.epicfurnaces.EpicFurnacesPlugin;
import com.songoda.epicfurnaces.api.furnace.Level;

import java.util.ArrayList;
import java.util.List;

public class ELevel implements Level {

    private int level, costExperiance, costEconomy, performance, fuelDuration;

    private String reward;

    private List<String> description = new ArrayList<>();

    public ELevel(int level, int costExperiance, int costEconomy, int performance, String reward, int fuelDuration) {
        this.level = level;
        this.costExperiance = costExperiance;
        this.costEconomy = costEconomy;
        this.performance = performance;
        this.reward = reward;
        this.fuelDuration = fuelDuration;

        EpicFurnacesPlugin instance = EpicFurnacesPlugin.getInstance();

        if (performance != 0)
            description.add(instance.getLocale().getMessage("interface.furnace.performance", performance + "%"));

        if (reward != null)
            description.add(instance.getLocale().getMessage("interface.furnace.reward", reward.split("%:")[0] + "%"));

        if (fuelDuration != 0)
            description.add(instance.getLocale().getMessage("interface.furnace.fuelduration", fuelDuration + "%"));
    }

    @Override
    public List<String> getDescription() {
        return new ArrayList<>(description);
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public int getPerformance() {
        return performance;
    }

    @Override
    public String getReward() {
        return reward;
    }

    @Override
    public int getFuelDuration() {
        return fuelDuration;
    }

    @Override
    public int getCostExperiance() {
        return costExperiance;
    }

    @Override
    public int getCostEconomy() {
        return costEconomy;
    }
}
