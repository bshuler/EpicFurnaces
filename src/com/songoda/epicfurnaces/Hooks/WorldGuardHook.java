package com.songoda.epicfurnaces.Hooks;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Created by songoda on 3/17/2017.
 */
public class WorldGuardHook implements Hooks {
    @Override
    public boolean canBuild(Player p, Location location) {
        return WorldGuardPlugin.inst().canBuild(p, location);
    }
}
