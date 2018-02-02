package com.songoda.epicfurnaces.Hooks;

import com.songoda.epicfurnaces.EpicFurnaces;
import com.songoda.epicfurnaces.Utils.Debugger;
import com.wasteofplastic.askyblock.ASkyBlockAPI;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

/**
 * Created by songoda on 3/17/2017.
 */
public class ASkyBlockHook implements Hooks {

    ASkyBlockAPI as = null;

    private EpicFurnaces plugin = EpicFurnaces.pl();
    private String pluginName = "ASkyBlock";

    ASkyBlockHook() {
        if (plugin.getServer().getPluginManager().getPlugin(pluginName) != null) {
            as = ASkyBlockAPI.getInstance();
            plugin.hooks.hooksFile.getConfig().addDefault("hooks." + pluginName, true);
            if (!plugin.hooks.hooksFile.getConfig().contains("hooks." + pluginName) || plugin.hooks.hooksFile.getConfig().getBoolean("hooks." + pluginName)) {
                plugin.hooks.ASkyBlockHook = this;
            }
        }
    }

    @Override
    public boolean canBuild(Player p, Location location) {
        try {
            if (p.hasPermission(plugin.getDescription().getName() + ".bypass")) {
                return true;
            } else {
                if (as.getIslandAt(location) != null) {
                    UUID owner = as.getOwner(location);
                    List<UUID> list = as.getTeamMembers(owner);
                    if (owner != null) {
                        for (UUID uuid : list) {
                            if (uuid.equals(p.getUniqueId())) {
                                return true;
                            }
                        }
                        if (owner.equals(p.getUniqueId())) {
                            return true;
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
    public boolean isInClaim(String uuid, Location location) {
        String owner = as.getOwner(location).toString();
        if (uuid.equals(owner))
            return true;
        return false;
    }

    @Override
    public String getClaimId(String name) {
        return null;
    }

}
