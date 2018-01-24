package com.songoda.epicfurnaces.Utils;

import com.songoda.arconix.Arconix;
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
        EpicFurnaces plugin = EpicFurnaces.pl();
        return Arconix.pl().getGUI().getGlass(plugin.getConfig().getBoolean("settings.Rainbow-Glass"), plugin.getConfig().getInt("settings.Glass-Type-1"));
    }

    public static void particles(Block b, Player p) {
        EpicFurnaces plugin = EpicFurnaces.pl();
        if (plugin.getConfig().getBoolean("settings.On-upgrade-particles")) {
            Location location = b.getLocation();
            location.setX(location.getX() + .5);
            location.setY(location.getY() + .5);
            location.setZ(location.getZ() + .5);
            if (!plugin.v1_8 && !plugin.v1_7) {
                p.getWorld().spawnParticle(org.bukkit.Particle.valueOf(plugin.getConfig().getString("settings.Upgrade-particle-type")), location, 200, .5, .5, .5);
            } else {
                p.getWorld().spigot().playEffect(location, org.bukkit.Effect.valueOf(plugin.getConfig().getString("settings.Upgrade-particle-type")), 1, 0, (float)0.5, (float)0.5, (float)0.5, 1, 200, 100);
            }
        }
    }

    public static ItemStack getBackgroundGlass(boolean type) {
        EpicFurnaces plugin = EpicFurnaces.pl();
        if (type)
            return Arconix.pl().getGUI().getGlass(false, plugin.getConfig().getInt("settings.Glass-Type-2"));
        else
            return Arconix.pl().getGUI().getGlass(false, plugin.getConfig().getInt("settings.Glass-Type-3"));
    }

    public static String cleanString(String typ) {
        String type = typ.replaceAll("_", " ");
        type = ChatColor.stripColor(type.substring(0, 1).toUpperCase() + type.toLowerCase().substring(1));
        return type;
    }

    public static String formatName(int level, int uses, boolean full) {
        String name = Lang.NAME_FORMAT.getConfigValue(level);

        String info = "";
        if (full) {
            info += Arconix.pl().format().convertToInvisibleString(level+":"+uses+":");
        }

        return info + Arconix.pl().format().formatText(name);
    }
}
