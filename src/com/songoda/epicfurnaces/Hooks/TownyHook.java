package com.songoda.epicfurnaces.Hooks;

import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Created by songoda on 3/17/2017.
 */
public class TownyHook implements Hooks {

    @Override
    public boolean canBuild(Player p, Location location) {
        try {
            if (TownyUniverse.isWilderness(location.getBlock())) {
                return true;
            } else {
                if (TownyUniverse.getTownBlock(location).hasTown()) {
                    Resident r = TownyUniverse.getDataSource().getResident(p.getName());
                    if (r.hasTown()) {
                        if (TownyUniverse.getTownName(location).equals(r.getTown().getName())) {
                            return true;
                        }
                    }
                    return false;
                }
            }
        } catch (Exception e) {
        }
        return true;
    }
}
