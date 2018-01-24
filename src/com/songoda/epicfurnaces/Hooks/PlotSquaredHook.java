package com.songoda.epicfurnaces.Hooks;

import com.intellectualcrafters.plot.api.PlotAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Created by songoda on 3/17/2017.
 */
public class PlotSquaredHook implements Hooks {

    final Plugin plotsquared = Bukkit.getServer().getPluginManager().getPlugin("PlotSquared");

    @Override
    public boolean canBuild(Player p, Location location) {
            PlotAPI api = new PlotAPI();
            if (api.getPlot(location) != null) {
                if (api.isInPlot(p)) {
                    if (api.getPlot(p) == api.getPlot(location)) {
                        return  true;
                    } else {
                        return false;
                    }
                }
                return false;
            }
        return true;
    }
}
