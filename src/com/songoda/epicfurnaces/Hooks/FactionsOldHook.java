package com.songoda.epicfurnaces.Hooks;

import me.markeh.factionsframework.entities.FPlayer;
import me.markeh.factionsframework.entities.FPlayers;
import me.markeh.factionsframework.entities.Factions;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Created by songoda on 3/17/2017.
 */
public class FactionsOldHook implements Hooks {
    @Override
    public boolean canBuild(Player p, Location location) {
        FPlayer fp = FPlayers.getBySender(p);

        me.markeh.factionsframework.entities.Faction faction = Factions.getFactionAt(location);

        if (fp.getFaction().equals(faction) || faction.isNone()) {
            return true;
        }
        return false;
    }
}
