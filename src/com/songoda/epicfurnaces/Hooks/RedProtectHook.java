package com.songoda.epicfurnaces.Hooks;

import br.net.fabiozumbi12.RedProtect.Bukkit.RedProtect;
import com.songoda.epicfurnaces.EpicFurnaces;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Created by songoda on 3/17/2017.
 */
public class RedProtectHook implements Hooks {

    private EpicFurnaces plugin = EpicFurnaces.pl();
    @Override
    public boolean canBuild(Player p, Location location) {
        if (RedProtect.get().getAPI().getRegion(location) != null) {
            return RedProtect.get().getAPI().getRegion(location).canBuild(p);
        }
        return true;
    }

}
