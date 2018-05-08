package com.songoda.epicfurnaces.Hooks;

import com.songoda.epicfurnaces.EpicFurnaces;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public abstract class Hook {

    final String pluginName;

    protected Hook(String pluginName) {
        this.pluginName = pluginName;
        if (isEnabled())
            EpicFurnaces.getInstance().hooks.hooksFile.getConfig().addDefault("hooks." + pluginName, true);
    }

    protected boolean isEnabled() {
        return (Bukkit.getPluginManager().isPluginEnabled(pluginName)
                && EpicFurnaces.getInstance().hooks.hooksFile.getConfig().getBoolean("hooks." + pluginName, true));
    }

    boolean hasBypass(Player p) {
        return p.hasPermission(EpicFurnaces.getInstance().getDescription().getName() + ".bypass");
    }

    public abstract boolean canBuild(Player p, Location location);

    public boolean isInClaim(String id, Location location) {
        return false;
    }

    public String getClaimId(String name) {
        return null;
    }


}
