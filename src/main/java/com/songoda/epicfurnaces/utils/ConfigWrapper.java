package com.songoda.epicfurnaces.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

/**
 * Minimal stand-in for Arconix's {@code ConfigWrapper}: a named YAML file
 * under the plugin's data folder, lazily loaded on first {@link #getConfig()}
 * call and explicitly persisted via {@link #saveConfig()}. See CLAUDE.md for
 * why the Arconix dependency itself was removed.
 *
 * <p>Loading is deliberately lazy rather than eager (at construction time):
 * callers such as {@code EpicFurnacesPlugin.setupRecipies()} construct their
 * wrapper before the backing file exists on disk (it's copied into place via
 * {@code saveResource(...)} later), and only call {@link #getConfig()}
 * afterward.
 */
public class ConfigWrapper {

    private final JavaPlugin plugin;
    private final File file;
    private FileConfiguration config;

    public ConfigWrapper(JavaPlugin plugin, String subFolder, String fileName) {
        this.plugin = plugin;
        File folder = (subFolder == null || subFolder.isEmpty())
                ? plugin.getDataFolder()
                : new File(plugin.getDataFolder(), subFolder);
        this.file = new File(folder, fileName);
    }

    /**
     * Create the backing file on disk with an empty config if it does not
     * already exist. No-op if it already exists.
     *
     * @return true if the file was newly created, false if it already existed or creation failed
     */
    public boolean createNewFile(String loadingMessage, String header) {
        if (file.exists()) return false;

        // No early `return` inside the try body on purpose: javac emits an
        // extra unreachable GOTO instruction (attributed to the catch
        // block's closing brace) to let a try-body return skip past the
        // catch blocks, which JaCoCo then reports as a permanently
        // uncoverable line. Falling through naturally to a single return
        // at the end avoids that compiler artifact entirely.
        boolean created = false;
        try {
            plugin.getLogger().info(loadingMessage);
            File parent = file.getParentFile();
            if (parent != null) parent.mkdirs();
            created = file.createNewFile();
        } catch (IOException e) {
            plugin.getLogger().warning("Could not create " + file.getName() + ": " + e.getMessage());
        }

        if (created) {
            config = new YamlConfiguration();
            saveConfig();
        }
        return created;
    }

    public FileConfiguration getConfig() {
        if (config == null) {
            config = YamlConfiguration.loadConfiguration(file);
        }
        return config;
    }

    public void saveConfig() {
        if (config == null) return;
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().warning("Could not save " + file.getName() + ": " + e.getMessage());
        }
    }
}
