package com.songoda.epicfurnaces;

import com.songoda.arconix.api.utils.ConfigWrapper;
import com.songoda.arconix.plugin.Arconix;
import com.songoda.epicfurnaces.API.EpicFurnacesAPI;
import com.songoda.epicfurnaces.Events.*;
import com.songoda.epicfurnaces.Furnace.Furnace;
import com.songoda.epicfurnaces.Furnace.FurnaceManager;
import com.songoda.epicfurnaces.Furnace.LevelManager;
import com.songoda.epicfurnaces.Handlers.CommandHandler;
import com.songoda.epicfurnaces.Handlers.HookHandler;
import com.songoda.epicfurnaces.Utils.SettingsManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EpicFurnaces extends JavaPlugin implements Listener {
    public static CommandSender console = Bukkit.getConsoleSender();

    public Map<Player, Furnace> inShow = new HashMap<>();

    public boolean v1_7 = Bukkit.getServer().getClass().getPackage().getName().contains("1_7");
    public boolean v1_8 = Bukkit.getServer().getClass().getPackage().getName().contains("1_8");
    public boolean v1_9 = Bukkit.getServer().getClass().getPackage().getName().contains("1_9");
    public boolean v1_10 = Bukkit.getServer().getClass().getPackage().getName().contains("1_10");

    public Map<String, Block> blockLoc = new HashMap<>();
    public Map<Player, Location> nicknameQ = new HashMap<>();

    public ConfigWrapper langFile = new ConfigWrapper(this, "", "lang.yml");
    public ConfigWrapper dataFile = new ConfigWrapper(this, "", "data.yml");
    public ConfigWrapper furnaceRecipeFile;

    private static EpicFurnaces INSTANCE;

    public HookHandler hooks;
    public SettingsManager sm;

    public References references = null;

    public EpicFurnacesAPI api;

    private LevelManager levelManager;
    private FurnaceManager furnaceManager;

    private Locale locale;

    public void onEnable() {
        INSTANCE = this;
        console.sendMessage(Arconix.pl().getApi().format().formatText("&a============================="));
        console.sendMessage(Arconix.pl().getApi().format().formatText("&7EpicFurnaces " + this.getDescription().getVersion() + " by &5Brianna <3&7!"));
        console.sendMessage(Arconix.pl().getApi().format().formatText("&7Action: &aEnabling&7..."));
        Bukkit.getServer().getPluginManager().registerEvents(this, this);
        sm = new SettingsManager();
        setupConfig();
        dataFile.createNewFile("Loading data file", "EpicFurnaces data file");
        langFile.createNewFile("Loading language file", "EpicFurnaces language file");
        loadDataFile();

        // Locales
        Locale.init(this);
        Locale.saveDefaultLocale("en_US");
        this.locale = Locale.getLocale(this.getConfig().getString("Locale", "en_US"));

        loadLevelManager();

        furnaceManager = new FurnaceManager();

        /*
         * Register furnaces into FurnaceManger from configuration
         */
        if (dataFile.getConfig().contains("data.charged")) {
            for (String locationStr : dataFile.getConfig().getConfigurationSection("data.charged").getKeys(false)) {
                System.out.println(locationStr);
                Location location = Arconix.pl().getApi().serialize().unserializeLocation(locationStr);
                int level = dataFile.getConfig().getInt("data.charged." + locationStr + ".level");
                int uses = dataFile.getConfig().getInt("data.charged." + locationStr + ".uses");
                int tolevel = dataFile.getConfig().getInt("data.charged." + locationStr + ".tolevel");
                String nickname = dataFile.getConfig().getString("data.charged." + locationStr + ".nickname");
                List<String> accessList = dataFile.getConfig().getStringList("data.charged." + locationStr + ".accesslist");

                Furnace furnace = new Furnace(location, levelManager.getLevel(level), nickname, uses, tolevel, accessList);

                furnaceManager.addFurnace(location, furnace);
            }
        }

        api = new EpicFurnacesAPI();

        setupRecipies();

        hooks = new HookHandler();
        hooks.hook();
        references = new References();

        new com.massivestats.MassiveStats(this, 900);

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, this::saveToFile, 6000, 6000);

        this.getCommand("EpicFurnaces").setExecutor(new CommandHandler(this));

        getServer().getPluginManager().registerEvents(new BlockListeners(this), this);
        getServer().getPluginManager().registerEvents(new FurnaceListeners(this), this);
        getServer().getPluginManager().registerEvents(new InteractListeners(this), this);
        getServer().getPluginManager().registerEvents(new ChatListeners(this), this);
        getServer().getPluginManager().registerEvents(new InventoryListeners(this), this);
        console.sendMessage(Arconix.pl().getApi().format().formatText("&a============================="));
    }

    public void onDisable() {
        saveToFile();
        dataFile.saveConfig();
        console.sendMessage(Arconix.pl().getApi().format().formatText("&a============================="));
        console.sendMessage(Arconix.pl().getApi().format().formatText("&7EpicFurnaces " + this.getDescription().getVersion() + " by &5Brianna <3&7!"));
        console.sendMessage(Arconix.pl().getApi().format().formatText("&7Action: &cDisabling&7..."));
        console.sendMessage(Arconix.pl().getApi().format().formatText("&a============================="));
    }

    private void loadLevelManager() {
        // Load an instance of LevelManager
        levelManager = new LevelManager();
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
                System.out.println("Failed to save furance.");
            }
        }

        //Save to file
        dataFile.saveConfig();

    }

    private void setupConfig() {
        sm.updateSettings();

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
        hooks.hooksFile.createNewFile("Loading hooks file", "EpicFurnaces hooks file");
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
        furnaceRecipeFile = new ConfigWrapper(this, "", "Furnace Recipes.yml");

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

    public FurnaceManager getFurnaceManager() {
        return furnaceManager;
    }

    public LevelManager getLevelManager() {
        return levelManager;
    }

    public Locale getLocale() {
        return locale;
    }

    public EpicFurnacesAPI getApi() {
        return api;
    }

    public static EpicFurnaces getInstance() {
        return INSTANCE;
    }
}