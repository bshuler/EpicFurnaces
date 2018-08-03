package com.songoda.epicfurnaces.utils;

import com.songoda.arconix.api.utils.ConfigWrapper;
import com.songoda.arconix.plugin.Arconix;
import com.songoda.epicfurnaces.EpicFurnaces;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by songo on 6/4/2017.
 */
public class SettingsManager implements Listener {

    private EpicFurnaces plugin = EpicFurnaces.getInstance();

    private static ConfigWrapper defs;

    public SettingsManager() {
        plugin.saveResource("SettingDefinitions.yml", true);
        defs = new ConfigWrapper(plugin, "", "SettingDefinitions.yml");
        defs.createNewFile("Loading data file", "EpicFurnaces SettingDefinitions file");
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public Map<Player, String> current = new HashMap<>();

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getCurrentItem() != null) {
            if (e.getInventory().getTitle().equals("EpicFurnaces Settings Editor")) {
                e.setCancelled(true);

                String key = e.getCurrentItem().getItemMeta().getDisplayName().substring(2);

                Player p = (Player) e.getWhoClicked();

                if (plugin.getConfig().get("settings." + key).getClass().getName().equals("java.lang.Boolean")) {
                    boolean bool = (Boolean) plugin.getConfig().get("settings." + key);
                    if (!bool)
                        plugin.getConfig().set("settings." + key, true);
                    else
                        plugin.getConfig().set("settings." + key, false);
                    finishEditing(p);
                } else {
                    editObject(p, key);
                }
            }
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        final Player p = e.getPlayer();
        if (current.containsKey(p)) {
            if (plugin.getConfig().get("settings." + current.get(p)).getClass().getName().equals("java.lang.Integer")) {
                plugin.getConfig().set("settings." + current.get(p), Integer.parseInt(e.getMessage()));
            } else if (plugin.getConfig().get("settings." + current.get(p)).getClass().getName().equals("java.lang.Double")) {
                plugin.getConfig().set("settings." + current.get(p), Double.parseDouble(e.getMessage()));
            } else if (plugin.getConfig().get("settings." + current.get(p)).getClass().getName().equals("java.lang.String")) {
                plugin.getConfig().set("settings." + current.get(p), e.getMessage());
            }
            finishEditing(p);
            e.setCancelled(true);
        }
    }

    public void finishEditing(Player p) {
        current.remove(p);
        plugin.saveConfig();
        openEditor(p);
    }


    public void editObject(Player p, String current) {
        this.current.put(p, current);
        p.closeInventory();
        p.sendMessage("");
        p.sendMessage(Arconix.pl().getApi().format().formatText("&7Please enter a value for &6" + current + "&7."));
        if (plugin.getConfig().get("settings." + current).getClass().getName().equals("java.lang.Integer")) {
            p.sendMessage(Arconix.pl().getApi().format().formatText("&cUse only numbers."));
        }
        p.sendMessage("");
    }

    public static void openEditor(Player p) {
        EpicFurnaces plugin = EpicFurnaces.getInstance();
        Inventory i = Bukkit.createInventory(null, 54, "EpicFurnaces Settings Editor");

        int num = 0;
        ConfigurationSection cs = plugin.getConfig().getConfigurationSection("settings");
        for (String key : cs.getKeys(true)) {

            if (!key.contains("levels")) {
                ItemStack item = new ItemStack(Material.DIAMOND_HELMET);
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(Arconix.pl().getApi().format().formatText("&6" + key));
                ArrayList<String> lore = new ArrayList<>();
                switch (plugin.getConfig().get("settings." + key).getClass().getName()) {
                    case "java.lang.Boolean":

                        item.setType(Material.LEVER);
                        boolean bool = (Boolean) plugin.getConfig().get("settings." + key);

                        if (!bool)
                            lore.add(Arconix.pl().getApi().format().formatText("&c" + false));
                        else
                            lore.add(Arconix.pl().getApi().format().formatText("&a" + true));

                        break;
                    case "java.lang.String":
                        item.setType(Material.PAPER);
                        String str = (String) plugin.getConfig().get("settings." + key);
                        lore.add(Arconix.pl().getApi().format().formatText("&9" + str));
                        break;
                    case "java.lang.Integer":
                        item.setType(Material.CLOCK);

                        int in = (Integer) plugin.getConfig().get("settings." + key);
                        lore.add(Arconix.pl().getApi().format().formatText("&5" + in));
                        break;
                }
                if (defs.getConfig().contains(key)) {
                    String text = defs.getConfig().getString(key);
                    int index = 0;
                    while (index < text.length()) {
                        lore.add(Arconix.pl().getApi().format().formatText("&7" + text.substring(index, Math.min(index + 50, text.length()))));
                        index += 50;
                    }
                }
                meta.setLore(lore);
                item.setItemMeta(meta);

                i.setItem(num, item);
                num++;
            }
        }
        p.openInventory(i);
    }

    public void updateSettings() {
        for (settings s : settings.values()) {
            if (s.setting.equals("Upgrade-particle-type")) {
                plugin.getConfig().addDefault("settings." + s.setting, s.option);
            } else
                plugin.getConfig().addDefault("settings." + s.setting, s.option);
        }

        ConfigurationSection cs = plugin.getConfig().getConfigurationSection("settings");
        for (String key : cs.getKeys(true)) {
            if (!key.contains("levels")) {
                if (!contains(key)) {
                    plugin.getConfig().set("settings." + key, null);
                }
            }
        }
    }

    public static boolean contains(String test) {
        for (settings c : settings.values()) {
            if (c.setting.equals(test)) {
                return true;
            }
        }
        return false;
    }

    public enum settings {

        o1("ECO-Icon", "SUNFLOWER"),
        o2("XP-Icon", "EXPERIENCE_BOTTLE"),

        o3("Upgrade-with-material", true),
        o4("Upgrade-with-eco", true),
        o5("Upgrade-with-xp", true),

        o6("Turbo-level-multiplier", 50),
        o7("On-upgrade-particles", true),

        o8("sounds", true),

        o83("Helpful-Tips", true),
        o9("Debug-Mode", false),
        o102("Remember-furnace-Levels", true),


        o116("Glass-Type-1", 7),

        o112("Glass-Type-2", 11),

        o113("Glass-Type-3", 3),

        o10("Rainbow-Glass", false),

        o324("Redstone-Deactivate", true),

        o11("furnace-upgrade-cost", "IRON_INGOT"),
        o12("Custom-recipes", true),
        o13("Ignore-custom-recipes-for-rewards", true),

        o14("Reward-Icon", "GOLDEN_APPLE"),
        o15("Performance-Icon", "REDSTONE"),
        o16("FuelDuration-Icon", "COAL"),

        o17("Upgrade-particle-type", "SPELL_WITCH"),

        o18("Remote-Furnaces", true);

        private String setting;
        private Object option;

        settings(String setting, Object option) {
            this.setting = setting;
            this.option = option;
        }

    }
}