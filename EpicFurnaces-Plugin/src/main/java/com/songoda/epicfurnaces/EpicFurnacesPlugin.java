package com.songoda.epicfurnaces;

import com.google.common.base.Preconditions;
import com.songoda.arconix.api.methods.formatting.TextComponent;
import com.songoda.arconix.api.utils.ConfigWrapper;
import com.songoda.arconix.plugin.Arconix;
import com.songoda.epicfurnaces.api.EpicFurnaces;
import com.songoda.epicfurnaces.api.furnace.Furnace;
import com.songoda.epicfurnaces.api.utils.ClaimableProtectionPluginHook;
import com.songoda.epicfurnaces.api.utils.ProtectionPluginHook;
import com.songoda.epicfurnaces.furnace.EFurnaceManager;
import com.songoda.epicfurnaces.furnace.ELevelManager;
import com.songoda.epicfurnaces.hooks.*;
import com.songoda.epicfurnaces.listeners.*;
import com.songoda.epicfurnaces.furnace.EFurnace;
import com.songoda.epicfurnaces.handlers.CommandHandler;
import com.songoda.epicfurnaces.player.PlayerDataManager;
import com.songoda.epicfurnaces.utils.Debugger;
import com.songoda.epicfurnaces.utils.SettingsManager;
import com.massivestats.MassiveStats;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class EpicFurnacesPlugin extends JavaPlugin implements EpicFurnaces {
    private static CommandSender console = Bukkit.getConsoleSender();

    private ConfigWrapper langFile = new ConfigWrapper(this, "", "lang.yml");
    private ConfigWrapper hooksFile = new ConfigWrapper(this, "", "hooks.yml");
    private ConfigWrapper dataFile = new ConfigWrapper(this, "", "data.yml");
    private ConfigWrapper furnaceRecipeFile = new ConfigWrapper(this, "", "Furnace Recipes.yml");

    private List<ProtectionPluginHook> protectionHooks = new ArrayList<>();
    private ClaimableProtectionPluginHook factionsHook, townyHook, aSkyblockHook, uSkyblockHook;

    private static EpicFurnacesPlugin INSTANCE;

    public References references = null;

    private SettingsManager settingsManager;
    private ELevelManager levelManager;
    private EFurnaceManager furnaceManager;
    private PlayerDataManager playerDataManager;

    private Locale locale;

    private void checkVersion() {
        int workingVersion = 13;
        int currentVersion = Integer.parseInt(Bukkit.getServer().getClass()
                .getPackage().getName().split("\\.")[3].split("_")[1]);

        if (currentVersion < workingVersion) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
                Bukkit.getConsoleSender().sendMessage("");
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "You installed the 1." + workingVersion + "+ only version of " + this.getDescription().getName() + " on a 1." + currentVersion + " server. Since you are on the wrong version we disabled the plugin for you. Please install correct version to continue using " + this.getDescription().getName() + ".");
                Bukkit.getConsoleSender().sendMessage("");
            }, 20L);
        }
    }

    @Override
    public void onEnable() {
        // Check to make sure the Bukkit version is compatible.
        checkVersion();
        INSTANCE = this;
        console.sendMessage(TextComponent.formatText("&a============================="));
        console.sendMessage(TextComponent.formatText("&7EpicFurnaces " + this.getDescription().getVersion() + " by &5Brianna <3&7!"));
        console.sendMessage(TextComponent.formatText("&7Action: &aEnabling&7..."));
        settingsManager = new SettingsManager();
        setupConfig();
        dataFile.createNewFile("Loading data file", "EpicFurnaces data file");
        langFile.createNewFile("Loading language file", "EpicFurnaces language file");
        loadDataFile();

        // Locales
        Locale.init(this);
        Locale.saveDefaultLocale("en_US");
        this.locale = Locale.getLocale(this.getConfig().getString("Locale", "en_US"));

        loadLevelManager();

        furnaceManager = new EFurnaceManager();
        playerDataManager = new PlayerDataManager();

        /*
         * Register furnaces into FurnaceManger from configuration
         */
        if (dataFile.getConfig().contains("data.charged")) {
            for (String locationStr : dataFile.getConfig().getConfigurationSection("data.charged").getKeys(false)) {
                Location location = Arconix.pl().getApi().serialize().unserializeLocation(locationStr);
                int level = dataFile.getConfig().getInt("data.charged." + locationStr + ".level");
                int uses = dataFile.getConfig().getInt("data.charged." + locationStr + ".uses");
                int tolevel = dataFile.getConfig().getInt("data.charged." + locationStr + ".tolevel");
                String nickname = dataFile.getConfig().getString("data.charged." + locationStr + ".nickname");
                List<String> accessList = dataFile.getConfig().getStringList("data.charged." + locationStr + ".accesslist");

                EFurnace furnace = new EFurnace(location, levelManager.getLevel(level), nickname, uses, tolevel, accessList);

                furnaceManager.addFurnace(location, furnace);
            }
        }

        setupRecipies();
        references = new References();

        new MassiveStats(this, 900);

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, this::saveToFile, 6000, 6000);

        this.getCommand("EpicFurnaces").setExecutor(new CommandHandler(this));

        PluginManager pluginManager = Bukkit.getPluginManager();

        // Register Listeners
        pluginManager.registerEvents(new BlockListeners(this), this);
        pluginManager.registerEvents(new FurnaceListeners(this), this);
        pluginManager.registerEvents(new InteractListeners(this), this);
        pluginManager.registerEvents(new ChatListeners(this), this);
        pluginManager.registerEvents(new InventoryListeners(this), this);

        // Register default hooks
        if (pluginManager.isPluginEnabled("ASkyBlock")) this.register(HookASkyBlock::new);
        if (pluginManager.isPluginEnabled("Factions")) this.register(HookFactions::new);
        if (pluginManager.isPluginEnabled("GriefPrevention")) this.register(HookGriefPrevention::new);
        if (pluginManager.isPluginEnabled("Kingdoms")) this.register(HookKingdoms::new);
        if (pluginManager.isPluginEnabled("PlotSquared")) this.register(HookPlotSquared::new);
        if (pluginManager.isPluginEnabled("RedProtect")) this.register(HookRedProtect::new);
        if (pluginManager.isPluginEnabled("Towny")) this.register(HookTowny::new);
        if (pluginManager.isPluginEnabled("USkyBlock")) this.register(HookUSkyBlock::new);
        if (pluginManager.isPluginEnabled("WorldGuard")) this.register(HookWorldGuard::new);

        console.sendMessage(TextComponent.formatText("&a============================="));
    }

    public void onDisable() {
        saveToFile();
        dataFile.saveConfig();
        console.sendMessage(TextComponent.formatText("&a============================="));
        console.sendMessage(TextComponent.formatText("&7EpicFurnaces " + this.getDescription().getVersion() + " by &5Brianna <3&7!"));
        console.sendMessage(TextComponent.formatText("&7Action: &cDisabling&7..."));
        console.sendMessage(TextComponent.formatText("&a============================="));
    }

    private void loadLevelManager() {
        // Load an instance of LevelManager
        levelManager = new ELevelManager();
        /*
         * Register Levels into LevelManager from configuration.
         */
        levelManager.clear();
        for (String levelName : getConfig().getConfigurationSection("settings.levels").getKeys(false)) {
            int level = Integer.valueOf(levelName.split("-")[1]);
            int costExperiance = getConfig().getInt("settings.levels." + levelName + ".Cost-xp");
            int costEconomy = getConfig().getInt("settings.levels." + levelName + ".Cost-eco");

            String performanceStr = getConfig().getString("settings.levels." + levelName + ".Performance");
            int performance = performanceStr == null ? 0 : Integer.parseInt(performanceStr.substring(0, performanceStr.length() - 1));

            String reward = getConfig().getString("settings.levels." + levelName + ".Reward");

            String fuelDurationStr = getConfig().getString("settings.levels." + levelName + ".Fuel-duration");
            int fuelDuration = fuelDurationStr == null ? 0 : Integer.parseInt(fuelDurationStr.substring(0, fuelDurationStr.length() - 1));

            levelManager.addLevel(level, costExperiance, costEconomy, performance, reward, fuelDuration);
        }
    }

    /*
     * Saves registered furnaces to file.
     */
    private void saveToFile() {

        // Wipe old kit information
        dataFile.getConfig().set("data.charged", null);

        /*
         * Dump FurnaceManager to file.
         */
        for (Furnace furnace : furnaceManager.getFurnaces().values()) {
            if (furnace == null || furnace.getLocation() == null || furnace.getLocation().getWorld() == null) continue;
            try {
                String locationStr = Arconix.pl().getApi().serialize().serializeLocation(furnace.getLocation());
                dataFile.getConfig().set("data.charged." + locationStr + ".level", furnace.getLevel().getLevel());
                dataFile.getConfig().set("data.charged." + locationStr + ".uses", furnace.getUses());
                dataFile.getConfig().set("data.charged." + locationStr + ".tolevel", furnace.getTolevel());
                dataFile.getConfig().set("data.charged." + locationStr + ".nickname", furnace.getNickname());
                dataFile.getConfig().set("data.charged." + locationStr + ".accesslist", furnace.getAccessList());
            } catch (Exception e) {
                System.out.println("Failed to save furnace.");
            }
        }

        //Save to file
        dataFile.saveConfig();

    }

    private void setupConfig() {
        settingsManager.updateSettings();

        getConfig().addDefault("settings.levels.Level-1.Performance", "10%");
        getConfig().addDefault("settings.levels.Level-1.Reward", "10%:1");
        getConfig().addDefault("settings.levels.Level-1.Cost-xp", 20);
        getConfig().addDefault("settings.levels.Level-1.Cost-eco", 5000);

        getConfig().addDefault("settings.levels.Level-2.Performance", "25%");
        getConfig().addDefault("settings.levels.Level-2.Reward", "20%:1-2");
        getConfig().addDefault("settings.levels.Level-2.Cost-xp", 25);
        getConfig().addDefault("settings.levels.Level-2.Cost-eco", 7500);

        getConfig().addDefault("settings.levels.Level-3.Performance", "40%");
        getConfig().addDefault("settings.levels.Level-3.Reward", "35%:2-3");
        getConfig().addDefault("settings.levels.Level-3.Fuel-duration", "10%");
        getConfig().addDefault("settings.levels.Level-3.Cost-xp", 30);
        getConfig().addDefault("settings.levels.Level-3.Cost-eco", 10000);

        getConfig().addDefault("settings.levels.Level-4.Performance", "55%");
        getConfig().addDefault("settings.levels.Level-4.Reward", "50%:2-4");
        getConfig().addDefault("settings.levels.Level-4.Fuel-duration", "25%");
        getConfig().addDefault("settings.levels.Level-4.Cost-xp", 35);
        getConfig().addDefault("settings.levels.Level-4.Cost-eco", 12000);

        getConfig().addDefault("settings.levels.Level-5.Performance", "75%");
        getConfig().addDefault("settings.levels.Level-5.Reward", "70%:3-4");
        getConfig().addDefault("settings.levels.Level-5.Fuel-duration", "45%");
        getConfig().addDefault("settings.levels.Level-5.Cost-xp", 40);
        getConfig().addDefault("settings.levels.Level-5.Cost-eco", 15000);

        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    public void reload() {
        locale.reloadMessages();
        references = new References();
        reloadConfig();
        saveConfig();
    }

    private void loadDataFile() {
        dataFile.getConfig().options().copyDefaults(true);
        dataFile.saveConfig();
    }

    private void setupRecipies() {
        File config = new File(getDataFolder(), "Furnace Recipes.yml");
        if (config != null) {
            saveResource("Furnace Recipes.yml", false);
        }

        if (getConfig().getBoolean("settings.Custom-recipes")) {
            ConfigurationSection cs = furnaceRecipeFile.getConfig().getConfigurationSection("Recipes");
            for (String key : cs.getKeys(false)) {
                Material item = Material.valueOf(key.toUpperCase());
                Material result = Material.valueOf(furnaceRecipeFile.getConfig().getString("Recipes." + key.toUpperCase() + ".result"));
                int amount = furnaceRecipeFile.getConfig().getInt("Recipes." + key.toUpperCase() + ".amount");

                getServer().addRecipe(new FurnaceRecipe(new ItemStack(result, amount), item));
            }
        }
    }


    private void register(Supplier<ProtectionPluginHook> hookSupplier) {
        this.registerProtectionHook(hookSupplier.get());
    }

    @Override
    public void registerProtectionHook(ProtectionPluginHook hook) {
        Preconditions.checkNotNull(hook, "Cannot register null hook");
        Preconditions.checkNotNull(hook.getPlugin(), "Protection plugin hook returns null plugin instance (#getPlugin())");

        JavaPlugin hookPlugin = hook.getPlugin();
        for (ProtectionPluginHook existingHook : protectionHooks) {
            if (existingHook.getPlugin().equals(hookPlugin)) {
                throw new IllegalArgumentException("Hook already registered");
            }
        }

        this.hooksFile.getConfig().addDefault("hooks." + hookPlugin.getName(), true);
        if (!hooksFile.getConfig().getBoolean("hooks." + hookPlugin.getName(), true)) return;
        this.hooksFile.getConfig().options().copyDefaults(true);
        this.hooksFile.saveConfig();

        this.protectionHooks.add(hook);
        this.getLogger().info("Registered protection hook for plugin: " + hook.getPlugin().getName());
    }

    public ConfigWrapper getDataFile() {
        return dataFile;
    }

    public boolean canBuild(Player player, Location location) {
        if (player.hasPermission(getDescription().getName() + ".bypass")) {
            return true;
        }

        for (ProtectionPluginHook hook : protectionHooks)
            if (!hook.canBuild(player, location)) return false;
        return true;
    }


    public int getFurnceLevel(ItemStack item) {
        try  {
                if (item.getItemMeta().getDisplayName().contains(":")) {
                    String arr[] = (item.getItemMeta().getDisplayName().replace("§", "")).split(":");
                    return Integer.parseInt(arr[0]);
                } else {
                    return 1;
                }
            
        } catch (Exception e) {
            Debugger.runReport(e);
        }
        return 9999;
    }

    public int getFurnaceUses(ItemStack item) {
        try {
            if (item.getItemMeta().getDisplayName().contains(":")) {
                String arr[] = (item.getItemMeta().getDisplayName().replace("§", "")).split(":");
                return Integer.parseInt(arr[1]);
            } else {
                return 0;
            }
        } catch (Exception e) {
            Debugger.runReport(e);
        }
        return 9999;
    }

    public ConfigWrapper getFurnaceRecipeFile() {
        return furnaceRecipeFile;
    }

    public EFurnaceManager getFurnaceManager() {
        return furnaceManager;
    }

    public ELevelManager getLevelManager() {
        return levelManager;
    }

    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }

    public Locale getLocale() {
        return locale;
    }

    public static EpicFurnacesPlugin getInstance() {
        return INSTANCE;
    }
}