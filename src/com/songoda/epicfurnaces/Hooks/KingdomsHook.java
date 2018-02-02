package com.songoda.epicfurnaces.Hooks;

import com.songoda.epicfurnaces.EpicFurnaces;
import com.songoda.epicfurnaces.Utils.Debugger;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.kingdoms.constants.land.Land;
import org.kingdoms.constants.land.SimpleChunkLocation;
import org.kingdoms.constants.player.OfflineKingdomPlayer;
import org.kingdoms.manager.game.GameManagement;

/**
 * Created by songoda on 3/17/2017.
 */
public class KingdomsHook implements Hooks {

    private EpicFurnaces plugin = EpicFurnaces.pl();
    private String pluginName = "Kingdoms";

    KingdomsHook() {
        if (plugin.getServer().getPluginManager().getPlugin(pluginName) != null) {
            plugin.hooks.hooksFile.getConfig().addDefault("hooks." + pluginName, true);
            if (!plugin.hooks.hooksFile.getConfig().contains("hooks." + pluginName) || plugin.hooks.hooksFile.getConfig().getBoolean("hooks." + pluginName)) {
                plugin.hooks.KingdomsHook = this;
            }
        }
    }

    @Override
    public boolean canBuild(Player p, Location location) {
        try {
            if (p.hasPermission(plugin.getDescription().getName() + ".bypass")) {
                return true;
            } else {
                OfflineKingdomPlayer pl = GameManagement.getPlayerManager().getOfflineKingdomPlayer(p);
                if (pl.getKingdomPlayer().getKingdom() != null) {
                    SimpleChunkLocation chunkLocation = new SimpleChunkLocation(location.getWorld().getName(), location.getChunk().getX(), location.getChunk().getZ());
                    Land land = GameManagement.getLandManager().getOrLoadLand(chunkLocation);
                    String owner = land.getOwner();
                    if (pl.getKingdomPlayer().getKingdom().getKingdomName().equals(owner)) {
                        return true;
                    } else {
                        if (owner == null) {
                            return true;
                        }
                    }
                    return false;
                }
            }
        } catch (Exception e) {
            Debugger.runReport(e);
        }
        return true;
    }

    @Override
    public boolean isInClaim(String id, Location location) {
        return false;
    }

    @Override
    public String getClaimId(String name) {
        return null;
    }
}
