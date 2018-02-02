package com.songoda.epicfurnaces.Hooks;

import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import com.songoda.epicfurnaces.EpicFurnaces;
import com.songoda.epicfurnaces.Utils.Debugger;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Created by songoda on 3/17/2017.
 */
public class TownyHook implements Hooks {

    private EpicFurnaces plugin = EpicFurnaces.pl();
    private String pluginName = "Towny";

    TownyHook() {
        if (plugin.getServer().getPluginManager().getPlugin(pluginName) != null) {
            plugin.hooks.hooksFile.getConfig().addDefault("hooks." + pluginName, true);
            if (!plugin.hooks.hooksFile.getConfig().contains("hooks." + pluginName) || plugin.hooks.hooksFile.getConfig().getBoolean("hooks." + pluginName)) {
                plugin.hooks.TownyHook = this;
            }
        }
    }

    @Override
    public boolean canBuild(Player p, Location location) {
        try {
            if (p.hasPermission(plugin.getDescription().getName() + ".bypass")) {
                return true;
            } else {
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
            }
        } catch (Exception e) {
            Debugger.runReport(e);
        }
        return true;
    }

    @Override
    public boolean isInClaim(String id, Location location) {
        try {
            if (TownyUniverse.isWilderness(location.getBlock())) {
                return false;
            }
            if (TownyUniverse.getTownBlock(location).getTown().getUID().equals(Integer.parseInt(id))) {
                return true;
            }
        } catch (Exception e) {
            Debugger.runReport(e);
        }
        return false;
    }

    @Override
    public String getClaimId(String name) {
        try {
        return TownyUniverse.getDataSource().getTown(name).getUID().toString();
        } catch (Exception e) {
        }
        return null;
    }
}
