package com.songoda.epicfurnaces.api.furnace;

import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.Map;

public interface FurnaceManager {
    void addFurnace(Location location, Furnace furnace);

    Furnace removeFurnace(Location location);

    Furnace getFurnace(Location location);

    Furnace getFurnace(Block block);

    Map<Location, Furnace> getFurnaces();
}
