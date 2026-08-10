package com.songoda.epicfurnaces.utils;

import com.songoda.epicfurnaces.EpicFurnacesPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Random;

/**
 * Created by songoda on 2/25/2017.
 *
 * Local replacements for the utilities formerly provided by the Arconix
 * library (text formatting, GUI glass, location serialization). See
 * CLAUDE.md for why Arconix was removed.
 */
public class Methods {

    private static final Random RANDOM = new Random();

    public static ItemStack getGlass() {
        try {
            EpicFurnacesPlugin plugin = EpicFurnacesPlugin.getInstance();
            return getGlassPane(plugin.getConfig().getBoolean("Interfaces.Replace Glass Type 1 With Rainbow Glass"), plugin.getConfig().getInt("Interfaces.Glass Type 1"));
        } catch (Exception e) {
            Debugger.runReport(e);
        }
        return null;
    }

    public static void particles(Block b, Player p) {
        try {
            EpicFurnacesPlugin plugin = EpicFurnacesPlugin.getInstance();
            if (plugin.getConfig().getBoolean("settings.On-upgrade-particles")) {
                Location location = b.getLocation();
                location.setX(location.getX() + .5);
                location.setY(location.getY() + .5);
                location.setZ(location.getZ() + .5);
                p.getWorld().spawnParticle(org.bukkit.Particle.valueOf(plugin.getConfig().getString("Main.Upgrade Particle Type")), location, 200, .5, .5, .5);
            }
        } catch (Exception e) {
            Debugger.runReport(e);
        }
    }

    public static ItemStack getBackgroundGlass(boolean type) {
        try {
            EpicFurnacesPlugin plugin = EpicFurnacesPlugin.getInstance();
            if (type)
                return getGlassPane(false, plugin.getConfig().getInt("Interfaces.Glass Type 2"));
            else
                return getGlassPane(false, plugin.getConfig().getInt("Interfaces.Glass Type 3"));
        } catch (Exception e) {
            Debugger.runReport(e);
        }
        return null;
    }

    /**
     * Build a stained glass pane used as GUI filler/background. {@code
     * dyeData} is a legacy 0-15 wool/dye data value (as historically stored
     * in config), mapped onto the modern {@link DyeColor} enum. When {@code
     * rainbow} is true a random color is used instead.
     */
    private static ItemStack getGlassPane(boolean rainbow, int dyeData) {
        DyeColor color;
        if (rainbow) {
            DyeColor[] colors = DyeColor.values();
            color = colors[RANDOM.nextInt(colors.length)];
        } else {
            color = DyeColor.getByWoolData((byte) dyeData);
            if (color == null) color = DyeColor.WHITE;
        }

        Material paneMaterial = Material.matchMaterial(color.name() + "_STAINED_GLASS_PANE");
        if (paneMaterial == null) paneMaterial = Material.WHITE_STAINED_GLASS_PANE;

        ItemStack item = new ItemStack(paneMaterial);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(" ");
            item.setItemMeta(meta);
        }
        return item;
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
            String name = EpicFurnacesPlugin.getInstance().getLocale().getMessage("general.nametag.nameformat", level);

            String info = "";
            if (full) {
                info += toHiddenString(level + ":" + uses + ":");
            }

            return info + formatText(name);
        } catch (Exception e) {
            Debugger.runReport(e);
        }
        return null;
    }

    /**
     * Translate {@code &}-based color codes, e.g. {@code &a} -> {@link
     * ChatColor#GREEN}. Direct replacement for Arconix's TextComponent
     * formatter, which did the same thing.
     */
    public static String formatText(String text) {
        if (text == null) return null;
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    /**
     * Encode a string as a sequence of section-sign-prefixed characters so
     * it stays hidden from players while remaining recoverable from the raw
     * display-name string. Paired with the decode logic in
     * {@code EpicFurnacesPlugin#getFurnceLevel}/{@code #getFurnaceUses},
     * which strips every {@code §} and splits on {@code :}. Direct
     * replacement for Arconix's {@code convertToInvisibleString}.
     */
    public static String toHiddenString(String text) {
        StringBuilder sb = new StringBuilder();
        for (char c : text.toCharArray()) {
            sb.append(ChatColor.COLOR_CHAR).append(c);
        }
        return sb.toString();
    }

    /**
     * Serialize a {@link Location} to a single string for storage in
     * data.yml. Direct replacement for Arconix's location serializer; the
     * exact format is internal to this plugin (only
     * {@link #unserializeLocation(String)} needs to parse it), so no
     * external compatibility constraint applies. Uses {@code ;} rather than
     * {@code :} or {@code .} as a separator since the result is used as a
     * literal segment of a YAML config path, and both of those characters
     * carry meaning there.
     */
    public static String serializeLocation(Location location) {
        return location.getWorld().getName() + ";" + location.getBlockX() + ";" + location.getBlockY() + ";" + location.getBlockZ();
    }

    /**
     * Format a currency amount for display. Direct replacement for
     * Arconix's {@code formatEconomy}; deliberately simple (no locale-aware
     * currency symbol/grouping) since Vault itself does not expose a
     * currency symbol in a version-independent way.
     */
    public static String formatEconomy(double amount) {
        if (amount == Math.floor(amount) && !Double.isInfinite(amount)) {
            return String.valueOf((long) amount);
        }
        return String.format("%.2f", amount);
    }

    public static Location unserializeLocation(String string) {
        String[] parts = string.split(";");
        World world = Bukkit.getWorld(parts[0]);
        return new Location(world, Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));
    }
}
