package com.songoda.epicfurnaces;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

public enum Lang {

    PREFIX("prefix", "&8[&6EpicFurnaces&8]"),

    UPGRADE_MESSAGE("Upgrade-message", "&7You successfully upgraded this furnace to &6level {LEVEL}&7!"),
    CANT_AFFORD("Cant-afford", "&cYou cannot afford this upgrade."),

    NAME_FORMAT("Name-format", "&eLevel {LEVEL} &9Furnace"),

    NO_PERMS("No-perms", "&7You do not have permission to do this."),

    SMELTED_X("Smelted_x", "&7Smelted &6{AMT} Materials&7."),

    PERFORMANCE_AMT("Performance-amt", "&7Performance: &6{AMT}&7."),

    REWARD_AMT("Reward-amt", "&7Reward: &6{AMT}&7."),

    FuelDuration_AMT("FuelDuration-amt", "&7Fuel Duration: &6{AMT}&7."),

    LEVEL("Level", "&6Furnace Level &7{LEVEL}"),

    NEXT_LEVEL("Next-Level", "&6Next Level &7{LEVEL}"),

    TO_LEVELUP("To-levelup", "&6{AMT} {TYPE}s &7away from leveling up."),

    PERFORMANCE_TITLE("Performance-title", "&a&lPerformance"),

    XPTITLE("Xp-upgrade-title", "&aUpgrade with XP"),
    XPLORE("Xp-upgrade-lore", "&7Cost: &a{COST} Levels"),

    ECOTITLE("Eco-upgrade-title", "&aUpgrade with ECO"),
    ECOLORE("Eco-upgrade-lore", "&7Cost: &a${COST}"),

    REWARD_TITLE("Reward-title", "&c&lReward"),

    FUELDURATION_TITLE("FuelDuration-title", "&7&lFuel Duration"),

    PERFORMANCE_INFO("Performance-info", "&7This furnaces performance is |&7currently boosted an extra &6{AMT}&7. | |&7Performance boosts the speed in |&7which a furnace processes |&7materials."),

    REWARD_INFO("Reward-info", "&7This furnace currently |&7has a &6{AMT}&7 chance of |&7producing multiple resources."),

    FUELDURATION_INFO("FuelDuration-info", "&7This furnaces fuel duration is |&7currently boosted by &6{AMT}&7. | |&7Fuel Duration boosts how long |&7fuel in the furnace lasts."),

    REMOTE_FURNACE("Remote-Furnace", "&5&lRemote Control"),
    REMOTE_FURNACE_LORE("Remote-Furnace-Lore", "&7Left-Click to assign a nickname.|&7Right-Click to give yourself |&7remote access.|&7Current nickname is: &6{NICKNAME}&7."),

    MAXED("Maxed", "&6This furnace is currently maxed out!"),

    ENTER("Enter", "&7Enter a unique nickname for the furnace."),

    NICKNAME_MATCH("Nickname-match", "&cThat nickname is already in use."),
    NICKNAME_SUCCESS("Nickname-success", "&aNickname set successfully."),
    REMOTE_LIST("Remote-List", "&7Players with remote access:"),
    REMOTE_UTIL("Remote-Util", "|&7To utilize remote access|&7use the command:|&6/EF Remote {NICKNAME}"),
    REMOTE_NOT_FOUND("Remote-Not-Found", "&cRemote furnace not found.");

    private String path;
    private String def;
    private static FileConfiguration LANG;

    Lang(String path, String start) {
        this.path = path;
        this.def = start;
    }

    public static void setFile(final FileConfiguration config) {
        LANG = config;
    }

    public String getDefault() {
        return this.def;
    }

    public String getPath() {
        return this.path;
    }

    public String getConfigValue(int arg) {
        return getConfigValue(Integer.toString(arg), null);
    }

    public String getConfigValue() {
        return getConfigValue(null, null);
    }

    public String getConfigValue(String arg) {
        return getConfigValue(arg, null);
    }

    public String getConfigValue(String arg, String arg2) {
        String value = ChatColor.translateAlternateColorCodes('&', LANG.getString(this.path, this.def));

        if (arg != null) {
            value = value.replace("{AMT}", arg);
            value = value.replace("{LEVEL}", arg);
            value = value.replace("{COST}", arg);
            value = value.replace("{NICKNAME}", arg);
        }

        if (arg2 != null) {
            value = value.replace("{TYPE}", arg2);
            value = value.replace("{NEWM}", arg2);
        }

        return value;
    }
}
