package com.songoda.epicfurnaces.Hooks;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.songoda.epicfurnaces.EpicFurnaces;
import com.songoda.epicfurnaces.Utils.Debugger;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Created by songoda on 3/17/2017.
 */
public class WorldGuardHook extends Hook {

    private EpicFurnaces plugin = EpicFurnaces.getInstance();

    public WorldGuardHook() {
        super("WorldGuard");
        if (isEnabled())
            plugin.hooks.WorldGuardHook = this;
    }

    @Override
    public boolean canBuild(Player p, Location location) {
        try {
            return p.hasPermission(plugin.getDescription().getName() + ".bypass") || WorldGuardPlugin.inst() == null || WorldGuardPlugin.inst().canBuild(p, location);
        } catch (Exception e) {
            Debugger.runReport(e);
        }
        return false;
    }
}
