package com.songoda.epicfurnaces.hooks;

import com.palmergames.bukkit.towny.Towny;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.songoda.epicfurnaces.api.utils.ClaimableProtectionPluginHook;
import com.songoda.epicfurnaces.utils.Debugger;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Rewritten against modern Towny (0.10x): the old hook called static methods
 * on {@code TownyUniverse} ({@code isWilderness(Block)},
 * {@code getTownBlock(Location)}, {@code getDataSource().getResident(String)})
 * that no longer exist. The current API is exposed through the
 * {@link TownyAPI} singleton instead: {@code isWilderness(Location)},
 * {@code getTownBlock(Location)}, and {@code getResident(Player)}.
 * {@link TownBlock#getTown()} and {@link Resident#getTown()} still throw
 * {@link NotRegisteredException} (same class, unchanged), so that part of the
 * original control flow is preserved as-is. See CLAUDE.md/PLAN.md.
 */
public class HookTowny implements ClaimableProtectionPluginHook {

    private final Towny towny;
    private final TownyAPI townyAPI;

    public HookTowny() {
        this.towny = Towny.getPlugin();
        this.townyAPI = TownyAPI.getInstance();
    }

    @Override
    public JavaPlugin getPlugin() {
        return towny;
    }

    @Override
    public boolean canBuild(Player player, Location location) {
        if (townyAPI.isWilderness(location)) return true;

        TownBlock townBlock = townyAPI.getTownBlock(location);
        if (townBlock == null || !townBlock.hasTown()) return true;

        try {
            Resident resident = townyAPI.getResident(player);
            if (resident == null || !resident.hasTown()) return false;

            Town locationTown = townBlock.getTown();
            Town residentTown = resident.getTown();
            return locationTown.getUUID().equals(residentTown.getUUID());
        } catch (NotRegisteredException e) {
            Debugger.runReport(e);
            return false;
        }
    }

    @Override
    public boolean isInClaim(Location location, String id) {
        if (townyAPI.isWilderness(location)) return false;

        TownBlock townBlock = townyAPI.getTownBlock(location);
        if (townBlock == null || !townBlock.hasTown()) return false;

        try {
            return townBlock.getTown().getUUID().toString().equals(id);
        } catch (NotRegisteredException e) {
            Debugger.runReport(e);
            return false;
        }
    }

    @Override
    public String getClaimID(String name) {
        Town town = townyAPI.getTown(name);
        return town == null ? null : town.getUUID().toString();
    }

}
