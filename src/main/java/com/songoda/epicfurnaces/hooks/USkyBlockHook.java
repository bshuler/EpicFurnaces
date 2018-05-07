package com.songoda.epicfurnaces.hooks;

import com.songoda.epicfurnaces.EpicFurnaces;
import com.songoda.epicfurnaces.utils.Debugger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import us.talabrek.ultimateskyblock.api.uSkyBlockAPI;

/**
 * Created by songoda on 3/17/2017.
 */
public class USkyBlockHook extends Hook {

    private uSkyBlockAPI usb;

    public USkyBlockHook() {
        super("USkyBlock");
        if (isEnabled()) {
            EpicFurnaces plugin = EpicFurnaces.getInstance();
            plugin.hooks.USkyBlockHook = this;
            this.usb = (uSkyBlockAPI) Bukkit.getPluginManager().getPlugin(pluginName);
        }
    }

    @Override
    public boolean canBuild(Player p, Location location) {
        try {
            if (hasBypass(p)) return true;

            for (Player pl : usb.getIslandInfo(location).getOnlineMembers()) {
                if (pl.equals(p)) {
                    return true;
                }
            }

            return usb.getIslandInfo(location).isLeader(p);
        } catch (Exception e) {
            Debugger.runReport(e);
        }
        return false;
    }

    @Override
    public boolean isInClaim(String uuid, Location location) {
        return usb.getIslandInfo(location).getLeader().equals(uuid);
    }
}