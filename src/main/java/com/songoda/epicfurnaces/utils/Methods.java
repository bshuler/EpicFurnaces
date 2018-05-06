package com.songoda.epicfurnaces.utils;

import com.songoda.arconix.plugin.Arconix;
import com.songoda.epicfurnaces.EpicFurnaces;
import com.songoda.epicfurnaces.Lang;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by songoda on 2/25/2017.
 */
public class Methods {

    public static ItemStack getGlass() {
        try {
            EpicFurnaces plugin = EpicFurnaces.pl();
            return Arconix.pl().getApi().getGUI().getGlass(plugin.getConfig().getBoolean("settings.Rainbow-Glass"), plugin.getConfig().getInt("settings.Glass-Type-1"));
        } catch (Exception e) {
            Debugger.runReport(e);
        }
        return null;
    }

    public static void particles(Block b, Player p) {
        try {
            EpicFurnaces plugin = EpicFurnaces.pl();
            if (plugin.getConfig().getBoolean("settings.On-upgrade-particles")) {
                Location location = b.getLocation();
                location.setX(location.getX() + .5);
                location.setY(location.getY() + .5);
                location.setZ(location.getZ() + .5);
                if (!plugin.v1_8 && !plugin.v1_7) {
                    p.getWorld().spawnParticle(org.bukkit.Particle.valueOf(plugin.getConfig().getString("settings.Upgrade-particle-type")), location, 200, .5, .5, .5);
                } else {
                    p.getWorld().playEffect(location, org.bukkit.Effect.valueOf(plugin.getConfig().getString("settings.Upgrade-particle-type")), 1, 0);
                    //Still not resolving --Nova
                    //p.getWorld().spigot().playEffect(location, org.bukkit.Effect.valueOf(plugin.getConfig().getString("settings.Upgrade-particle-type")), 1, 0, (float) 0.5, (float) 0.5, (float) 0.5, 1, 200, 100);
                }
            }
        } catch (Exception e) {
            Debugger.runReport(e);
        }
    }

    public static ItemStack getBackgroundGlass(boolean type) {
        try {
            EpicFurnaces plugin = EpicFurnaces.pl();
            if (type)
                return Arconix.pl().getApi().getGUI().getGlass(false, plugin.getConfig().getInt("settings.Glass-Type-2"));
            else
                return Arconix.pl().getApi().getGUI().getGlass(false, plugin.getConfig().getInt("settings.Glass-Type-3"));
        } catch (Exception e) {
            Debugger.runReport(e);
        }
        return null;
    }

    public static String cleanString(String typ) {
        try {
            String type = typ.replaceAll("_", " ");
            type = ChatColor.stripColor(type.substring(0, 1).toUpperCase() + type.toLowerCase().substring(1));
            return type;
        } catch (Exception e) {
            Debugger.runReport(e);
        }
        return null;
    }

    public static String formatName(int level, int uses, boolean full) {
        try {
            String name = Lang.NAME_FORMAT.getConfigValue(level);

            String info = "";
            if (full) {
                info += Arconix.pl().getApi().format().convertToInvisibleString(level + ":" + uses + ":");
            }

            return info + Arconix.pl().getApi().format().formatText(name);
        } catch (Exception e) {
            Debugger.runReport(e);
        }
        return null;
    }
}
