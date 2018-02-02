package com.songoda.epicfurnaces.Hooks;

import br.net.fabiozumbi12.RedProtect.Bukkit.RedProtect;
import com.songoda.epicfurnaces.EpicFurnaces;
import com.songoda.epicfurnaces.Utils.Debugger;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Created by songoda on 3/17/2017.
 */
public class RedProtectHook implements Hooks {

    private EpicFurnaces plugin = EpicFurnaces.pl();
    private String pluginName = "RedProtect";

    RedProtectHook() {
        if (plugin.getServer().getPluginManager().getPlugin(pluginName) != null) {
            plugin.hooks.hooksFile.getConfig().addDefault("hooks." + pluginName, true);
            if (!plugin.hooks.hooksFile.getConfig().contains("hooks." + pluginName) || plugin.hooks.hooksFile.getConfig().getBoolean("hooks." + pluginName)) {
                plugin.hooks.hooksFile.getConfig().addDefault("hooks." + pluginName, true);
                plugin.hooks.RedProtectHook = this;
            }
        }
    }

    @Override
    public boolean canBuild(Player p, Location location) {
        try {
            if (p.hasPermission(plugin.getDescription().getName() + ".bypass")) {
                return true;
            } else {
                if (RedProtect.get().getAPI().getRegion(location) != null) {
                    return RedProtect.get().getAPI().getRegion(location).canBuild(p);
                }
            }
        } catch (Exception e) {
            Debugger.runReport(e);
        }
        return true;
    }

    @Override
    public boolean isInClaim(String uuid, Location location) {
        return false;
    }

    @Override
    public String getClaimId(String name) {
        return null;
    }

}
