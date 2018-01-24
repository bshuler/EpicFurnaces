package com.songoda.epicfurnaces.Hooks;

import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.ps.PS;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Created by songoda on 3/17/2017.
 */
public class FactionsHook implements Hooks {
    @Override
    public boolean canBuild(Player p, Location location) {
        MPlayer mp = MPlayer.get(p);

        Faction faction = BoardColl.get().getFactionAt(PS.valueOf(location));

        if (mp.getFaction().equals(faction)) {
            return true;
        }
        return false;
    }
}
