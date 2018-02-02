package com.songoda.epicfurnaces.Hooks;

import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Created by songoda on 3/17/2017.
 */
public interface Hooks {

    boolean canBuild(Player p, Location location);

    boolean isInClaim(String id, Location location);

    String getClaimId(String name);

}
