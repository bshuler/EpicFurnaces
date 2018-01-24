package com.songoda.epicfurnaces.Hooks;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Created by songoda on 3/17/2017.
 */
public class GriefPreventionHook implements Hooks {
    @Override
    public boolean canBuild(Player p, Location location) {
        Claim claim = GriefPrevention.instance.dataStore.getClaimAt(location, false, null);
        if(claim != null) {
            if (claim.allowBuild(p, Material.STONE) == null) {
                return true;
            }
        }
        return false;
    }
}
