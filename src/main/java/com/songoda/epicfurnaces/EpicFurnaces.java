package com.songoda.epicfurnaces;

import com.songoda.arconix.api.mcupdate.MCUpdate;
import com.songoda.arconix.api.utils.ConfigWrapper;
import com.songoda.arconix.plugin.Arconix;
import com.songoda.epicfurnaces.api.EpicFurnacesAPI;
import com.songoda.epicfurnaces.handlers.CommandHandler;
import com.songoda.epicfurnaces.handlers.HookHandler;
import com.songoda.epicfurnaces.listeners.*;
import com.songoda.epicfurnaces.utils.SettingsManager;
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
import java.util.Map;

public class EpicFurnaces extends JavaPlugin implements Listener {
    private static EpicFurnaces INSTANCE;

    public static CommandSender console = Bukkit.getConsoleSender();

    public Map<Player, String> inShow = new HashMap<>();

    public boolean v1_7 = Bukkit.getServer().getClass().getPackage().getName().contains("1_7");
    public boolean v1_8 = Bukkit.getServer().getClass().getPackage().getName().contains("1_8");

    public Map<String, Block> blockLoc = new HashMap<>();
    public Map<Player, Location> nicknameQ = new HashMap<>();

    public ConfigWrapper langFile = new ConfigWrapper(this, "", "lang.yml");
    public ConfigWrapper dataFile = new ConfigWrapper(this, "", "data.yml");
    public ConfigWrapper furnaceRecipeFile;

    public HookHandler hooks;
    public SettingsManager sm;

    public References references = null;

    public EpicFurnacesAPI api;

    public void onDisable() {
        dataFile.saveConfig();
        console.sendMessage(Arconix.pl().getApi().format().formatText("&a============================="));
        console.sendMessage(Arconix.pl().getApi().format().formatText("&7EpicFurnaces " + this.getDescription().getVersion() + " by &5Brianna <3&7!"));
        console.sendMessage(Arconix.pl().getApi().format().formatText("&7Action: &cDisabling&7..."));
        console.sendMessage(Arconix.pl().getApi().format().formatText("&a============================="));
    }

    public void onEnable() {
        INSTANCE = this;

        Arconix.pl().hook(this);

        console.sendMessage(Arconix.pl().getApi().format().formatText("&a============================="));
        console.sendMessage(Arconix.pl().getApi().format().formatText("&7EpicFurnaces " + this.getDescription().getVersion() + " by &5Brianna <3&7!"));
        console.sendMessage(Arconix.pl().getApi().format().formatText("&7Action: &aEnabling&7..."));
        Bukkit.getServer().getPluginManager().registerEvents(this, this);
        sm = new SettingsManager();
		setupConfig();
        dataFile.createNewFile("Loading data file", "EpicFurnaces data file");
        langFile.createNewFile("Loading language file", "EpicFurnaces language file");
        loadLanguageFile();
        loadDataFile();

        api = new EpicFurnacesAPI();

        setupRecipies();

        hooks = new HookHandler();
        hooks.hook();
        references = new References();

        new MCUpdate(this, true);

        this.getCommand("EpicFurnaces").setExecutor(new CommandHandler(this));

		getServer().getPluginManager().registerEvents(new BlockListeners(), this);
		getServer().getPluginManager().registerEvents(new FurnaceListeners(), this);
        getServer().getPluginManager().registerEvents(new InteractListeners(), this);
        getServer().getPluginManager().registerEvents(new ChatListeners(), this);
		getServer().getPluginManager().registerEvents(new InventoryListeners(), this);
        getServer().getPluginManager().registerEvents(new LoginListeners(), this);
        console.sendMessage(Arconix.pl().getApi().format().formatText("&a============================="));
    }

    public static EpicFurnaces pl() {
        return (EpicFurnaces) Bukkit.getServer().getPluginManager().getPlugin("EpicFurnaces");
    }

    private void setupConfig() {
        sm.updateSettings();

        getConfig().addDefault("settings.levels.Level-1.Performance", "10%");
        getConfig().addDefault("settings.levels.Level-1.Reward", "10%");
        getConfig().addDefault("settings.levels.Level-1.Cost-xp", 20);
        getConfig().addDefault("settings.levels.Level-1.Cost-eco", 5000);

        getConfig().addDefault("settings.levels.Level-2.Performance", "25%");
        getConfig().addDefault("settings.levels.Level-2.Reward", "20%");
        getConfig().addDefault("settings.levels.Level-2.Cost-xp", 25);
        getConfig().addDefault("settings.levels.Level-2.Cost-eco", 7500);

        getConfig().addDefault("settings.levels.Level-3.Performance", "40%");
        getConfig().addDefault("settings.levels.Level-3.Reward", "35%");
        getConfig().addDefault("settings.levels.Level-3.Fuel-duration", "10%");
        getConfig().addDefault("settings.levels.Level-3.Cost-xp", 30);
        getConfig().addDefault("settings.levels.Level-3.Cost-eco", 10000);

        getConfig().addDefault("settings.levels.Level-4.Performance", "55%");
        getConfig().addDefault("settings.levels.Level-4.Reward", "50%");
        getConfig().addDefault("settings.levels.Level-4.Fuel-duration", "25%");
        getConfig().addDefault("settings.levels.Level-4.Cost-xp", 35);
        getConfig().addDefault("settings.levels.Level-4.Cost-eco", 12000);

        getConfig().addDefault("settings.levels.Level-5.Performance", "75%");
        getConfig().addDefault("settings.levels.Level-5.Reward", "70%");
        getConfig().addDefault("settings.levels.Level-5.Fuel-duration", "45%");
        getConfig().addDefault("settings.levels.Level-5.Cost-xp", 40);
        getConfig().addDefault("settings.levels.Level-5.Cost-eco", 15000);

        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    public void loadLanguageFile() {
        Lang.setFile(langFile.getConfig());

        for (final Lang value : Lang.values()) {
            langFile.getConfig().addDefault(value.getPath(), value.getDefault());
        }

        langFile.getConfig().options().copyDefaults(true);
        langFile.saveConfig();
    }

    private void loadDataFile() {
        dataFile.getConfig().options().copyDefaults(true);
        dataFile.saveConfig();
    }

    private void setupRecipies() {
        File config = new File(getDataFolder(), "Furnace Recipes.yml");
        saveResource("Furnace Recipes.yml", false);
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

    public EpicFurnacesAPI getApi() {
        return api;
    }

    public static EpicFurnaces getInstance() {
        return INSTANCE;
    }
}