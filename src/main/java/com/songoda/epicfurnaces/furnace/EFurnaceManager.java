package com.songoda.epicfurnaces.furnace;

import com.songoda.epicfurnaces.EpicFurnacesPlugin;
import com.songoda.epicfurnaces.api.furnace.Furnace;
import com.songoda.epicfurnaces.api.furnace.FurnaceManager;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class EFurnaceManager implements FurnaceManager {

    // ConcurrentHashMap, not HashMap: on Folia, EpicFurnacesPlugin's periodic
    // saveToFile() task (see its "Folia" note in PLAN.md) iterates this map
    // from the global region thread while BlockListeners/FurnaceListeners
    // add/remove/mutate entries from whichever per-region thread owns a
    // given furnace's block - concurrent, unsynchronized access to a plain
    // HashMap is undefined behavior (lost updates, iterator
    // ConcurrentModificationException). A vanilla single-threaded Bukkit/
    // Paper server never exercises that concurrency, so this was never
    // observably broken there, but it is a real, fixable hazard for Folia
    // and the fix (this class only) is free: it doesn't require any
    // Folia-only API, so it doesn't risk the older paper-api backward-
    // compatibility builds the way the scheduler-call-site fixes below do.
    private final Map<Location, Furnace> registeredFurnaces = new ConcurrentHashMap<>();

    @Override
    public void addFurnace(Location location, Furnace furnace) {
        registeredFurnaces.put(roundLocation(location), furnace);
    }

    @Override
    public Furnace removeFurnace(Location location) {
        return registeredFurnaces.remove(roundLocation(location));
    }

    @Override
    public Furnace getFurnace(Location location) {
        // Bug fix: addFurnace()/removeFurnace() key the map on a
        // block-rounded Location (roundLocation() below), but this lookup
        // used to key on the raw, unrounded Location it was given. Every
        // current call site happens to already pass a whole-number
        // block.getLocation(), so the mismatch never manifested in
        // practice, but any caller passing a fractional Location (e.g. an
        // entity's exact position) would silently miss an already-registered
        // furnace and create a duplicate one. See PLAN.md ("bugs found").
        Location key = roundLocation(location);
        if (!registeredFurnaces.containsKey(key)) {
            addFurnace(key, new EFurnace(key, EpicFurnacesPlugin.getInstance().getLevelManager().getLowestLevel(), null, 0, 0, new ArrayList<>()));
        }
        return registeredFurnaces.get(key);
    }

    @Override
    public Furnace getFurnace(Block block) {
        return getFurnace(block.getLocation());
    }

    @Override
    public Map<Location, Furnace> getFurnaces() {
        return Collections.unmodifiableMap(registeredFurnaces);
    }

    private Location roundLocation(Location location) {
        location = location.clone();
        location.setX(location.getBlockX());
        location.setY(location.getBlockY());
        location.setZ(location.getBlockZ());
        return location;
    }
}
