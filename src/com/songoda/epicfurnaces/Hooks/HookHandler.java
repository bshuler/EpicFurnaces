package com.songoda.epicfurnaces.Hooks;

import com.songoda.epicfurnaces.EpicFurnaces;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Created by songoda on 3/17/2017.
 */
public class HookHandler {

    public Hooks FactionsHook = null;
    public Hooks RedProtectHook = null;
    public Hooks ASkyBlockHook = null;
    public Hooks USkyBlockHook = null;
    public Hooks WorldGuardHook = null;
    public Hooks GriefPreventionHook = null;
    public Hooks PlotSquaredHook = null;
    public Hooks KingdomsHook = null;
    public Hooks TownyHook = null;

    EpicFurnaces plugin = EpicFurnaces.pl();

    public HookHandler() {
        hook();
    }

    public void hook() {
        if (plugin.getServer().getPluginManager().getPlugin("Factions") != null) {
            try {
                Class.forName("com.massivecraft.factions.FPlayer");
                if (plugin.getServer().getPluginManager().getPlugin("FactionsFramework") != null) {
                    FactionsHook = new FactionsOldHook();
                }
            } catch (Exception e) {
                FactionsHook = new FactionsHook();
            }
        }
        if (plugin.getServer().getPluginManager().getPlugin("RedProtect") != null) {
            RedProtectHook = new RedProtectHook();
        }
        if (plugin.getServer().getPluginManager().getPlugin("GriefPrevention") != null) {
            GriefPreventionHook = new GriefPreventionHook();
        }
        if (plugin.getServer().getPluginManager().getPlugin("ASkyBlock") != null) {
            ASkyBlockHook = new ASkyBlockHook();
        }
        if (plugin.getServer().getPluginManager().getPlugin("USkyBlock") != null) {
            USkyBlockHook = new USkyBlockHook();
        }
        if (plugin.getServer().getPluginManager().getPlugin("WorldGuard") != null) {
            WorldGuardHook = new WorldGuardHook();
        }
        if (plugin.getServer().getPluginManager().getPlugin("PlotSquared") != null) {
            PlotSquaredHook = new PlotSquaredHook();
        }
        if (plugin.getServer().getPluginManager().getPlugin("Kingdoms") != null) {
            KingdomsHook = new KingdomsHook();
        }
        if (plugin.getServer().getPluginManager().getPlugin("Towny") != null) {
            TownyHook = new TownyHook();
        }
    }

    public boolean canBuild(Player p, Location l) {
        boolean result = true;
        if (!p.hasPermission("epicfurnaces.bypass")) {
            if (WorldGuardHook != null && result != false) {
                result = WorldGuardHook.canBuild(p, l);
            }
            if (RedProtectHook != null && result != false) {
                result = RedProtectHook.canBuild(p, l);
            }
            if (FactionsHook != null && result != false) {
                result = FactionsHook.canBuild(p, l);
            }
            if (ASkyBlockHook != null && result != false) {
                result = ASkyBlockHook.canBuild(p, l);
            }
            if (USkyBlockHook != null && result != false) {
                result = USkyBlockHook.canBuild(p, l);
            }
            if (GriefPreventionHook != null && result != false) {
                result = GriefPreventionHook.canBuild(p, l);
            }
            if (PlotSquaredHook != null && result != false) {
                result = PlotSquaredHook.canBuild(p, l);
            }
            if (KingdomsHook != null && result != false) {
                result = KingdomsHook.canBuild(p, l);
            }
            if (TownyHook != null && result != false) {
                result = TownyHook.canBuild(p, l);
            }
        }
        return result;
    }
}
