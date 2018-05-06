package com.songoda.epicfurnaces.furnace;

import com.songoda.arconix.plugin.Arconix;
import com.songoda.epicfurnaces.EpicFurnaces;
import com.songoda.epicfurnaces.Lang;
import com.songoda.epicfurnaces.utils.Debugger;
import com.songoda.epicfurnaces.utils.Methods;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredServiceProvider;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by songoda on 3/7/2017.
 */
public class Furnace {
    public Location location = null;
    private String locationStr = null;
    private Block block = null;

    private EpicFurnaces plugin = EpicFurnaces.pl();

    public Furnace(String loc) {
        try {
            defineBlockInformation(loc);
        } catch (Exception e) {
            Debugger.runReport(e);
        }
    }

    private void defineBlockInformation(String loc) {
        try {
            //String name = plugin.getConfig().getString("data.block." + loc);
            locationStr = loc;
            location = Arconix.pl().getApi().serialize().unserializeLocation(loc);
            block = location.getBlock();
        } catch (Exception e) {
            Debugger.runReport(e);
        }
    }


    @SuppressWarnings("unchecked")
    public void open(Player p) {
        try {
            plugin.blockLoc.put(p.getName(), location.getBlock());

            int xpCost = 1;
            int ecoCost = 1;

            String nickname = "Unset";

            if (plugin.dataFile.getConfig().contains("data.charged." + locationStr + ".nickname")) {
                nickname = plugin.dataFile.getConfig().getString("data.charged." + locationStr + ".nickname");
            }

            int uses = plugin.dataFile.getConfig().getInt("data.charged." + locationStr + ".uses");
            int tolevel = plugin.dataFile.getConfig().getInt("data.charged." + locationStr + ".tolevel");
            int level = plugin.dataFile.getConfig().getInt("data.charged." + locationStr + ".level");

            if (level == 0)
                level = 1;

            String performance = "0%";
            if (plugin.getConfig().contains("settings.levels.Level-" + level + ".Performance")) {
                performance = plugin.getConfig().getString("settings.levels.Level-" + level + ".Performance");
            }
            String reward = "0%";
            if (plugin.getConfig().contains("settings.levels.Level-" + level + ".Reward")) {
                reward = plugin.getConfig().getString("settings.levels.Level-" + level + ".Reward");
            }
            String fuelDuration = "0%";
            if (plugin.getConfig().contains("settings.levels.Level-" + level + ".Fuel-duration")) {
                fuelDuration = plugin.getConfig().getString("settings.levels.Level-" + level + ".Fuel-duration");
            }

            boolean showPerformance = false;
            boolean showReward = false;
            boolean showFuelDuration = false;

            for (int l = 1; plugin.getConfig().contains("settings.levels.Level-" + l); l++) {
                if (plugin.getConfig().contains("settings.levels.Level-" + l + ".Performance")) {
                    showPerformance = true;
                }
                if (plugin.getConfig().contains("settings.levels.Level-" + l + ".Reward")) {
                    showReward = true;
                }
                if (plugin.getConfig().contains("settings.levels.Level-" + l + ".Fuel-duration")) {
                    showFuelDuration = true;
                }
            }

            String nextPerformance = null;
            String nextReward = null;
            String nextFuelDuration = null;

            boolean maxed = true;

            if (plugin.getConfig().contains("settings.levels.Level-" + (level + 1))) {
                xpCost = plugin.getConfig().getInt("settings.levels.Level-" + (level + 1) + ".Cost-xp");
                ecoCost = plugin.getConfig().getInt("settings.levels.Level-" + (level + 1) + ".Cost-eco");
                nextPerformance = plugin.getConfig().getString("settings.levels.Level-" + (level + 1) + ".Performance");
                nextReward = plugin.getConfig().getString("settings.levels.Level-" + (level + 1) + ".Reward");
                nextFuelDuration = plugin.getConfig().getString("settings.levels.Level-" + (level + 1) + ".Fuel-duration");
                maxed = false;
            }

            int multi = plugin.getConfig().getInt("settings.Turbo-level-multiplier");

            if (plugin.dataFile.getConfig().getInt("data.charged." + locationStr + ".level") == 0) {
                level = 1;
            }

            int needed = (multi * level) - tolevel;

            ItemStack item = new ItemStack(Material.FURNACE, 1);

            ItemMeta itemmeta = item.getItemMeta();
            itemmeta.setDisplayName(Arconix.pl().getApi().format().formatText(Lang.LEVEL.getConfigValue(level)));
            ArrayList<String> lore = new ArrayList<>();
            lore.add(Arconix.pl().getApi().format().formatText(Lang.SMELTED_X.getConfigValue(Integer.toString(uses), null)));
            lore.add("");
            if (maxed) {
                lore.add(Arconix.pl().getApi().format().formatText(Lang.MAXED.getConfigValue(null, null)));
            } else {
                lore.add(Lang.NEXT_LEVEL.getConfigValue(level + 1));
                lore.add(Arconix.pl().getApi().format().formatText(Lang.PERFORMANCE_AMT.getConfigValue(nextPerformance, null)));
                lore.add(Arconix.pl().getApi().format().formatText(Lang.REWARD_AMT.getConfigValue(nextReward, null)));
                if (nextFuelDuration != null) {
                    lore.add(Arconix.pl().getApi().format().formatText(Lang.FuelDuration_AMT.getConfigValue(nextFuelDuration, null)));
                }
                if (plugin.getConfig().getBoolean("settings.Upgrade-with-material")) {
                    lore.add(Arconix.pl().getApi().format().formatText(Lang.TO_LEVELUP.getConfigValue(Integer.toString(needed), Methods.cleanString(plugin.getConfig().getString("settings.Furnace-upgrade-cost")))));
                }
            }
            itemmeta.setLore(lore);
            item.setItemMeta(itemmeta);

            Inventory i = Bukkit.createInventory(null, 27, Arconix.pl().getApi().format().formatText(Methods.formatName(level, 0, false)));

            int nu = 0;
            while (nu != 27) {
                i.setItem(nu, Methods.getGlass());
                nu++;
            }


            ItemStack item2 = new ItemStack(Material.valueOf(plugin.getConfig().getString("settings.Performance-Icon")), 1);
            ItemMeta itemmeta2 = item2.getItemMeta();
            itemmeta2.setDisplayName(Arconix.pl().getApi().format().formatText(Lang.PERFORMANCE_TITLE.getConfigValue(null, null))); //greyed out until available
            ArrayList<String> lore2 = new ArrayList<>();

            String[] parts = Lang.PERFORMANCE_INFO.getConfigValue(performance, null).split("\\|");
            lore.add("");
            for (String line : parts) {
                lore2.add(Arconix.pl().getApi().format().formatText(line));
            }
            itemmeta2.setLore(lore2);
            item2.setItemMeta(itemmeta2);

            ItemStack item3 = new ItemStack(Material.valueOf(plugin.getConfig().getString("settings.Reward-Icon")), 1);
            ItemMeta itemmeta3 = item3.getItemMeta();
            itemmeta3.setDisplayName(Arconix.pl().getApi().format().formatText(Lang.REWARD_TITLE.getConfigValue(null, null))); //greyed out until available
            ArrayList<String> lore3 = new ArrayList<>();

            parts = Lang.REWARD_INFO.getConfigValue(reward, null).split("\\|");
            lore.add("");
            for (String line : parts) {
                lore3.add(Arconix.pl().getApi().format().formatText(line));
            }
            itemmeta3.setLore(lore3);
            item3.setItemMeta(itemmeta3);


            ItemStack item4 = new ItemStack(Material.valueOf(plugin.getConfig().getString("settings.FuelDuration-Icon")), 1);
            ItemMeta itemmeta4 = item4.getItemMeta();
            itemmeta4.setDisplayName(Arconix.pl().getApi().format().formatText(Lang.FUELDURATION_TITLE.getConfigValue(null, null))); //greyed out until available
            ArrayList<String> lore4 = new ArrayList<>();
            parts = Lang.FUELDURATION_INFO.getConfigValue(fuelDuration, null).split("\\|");
            lore.add("");
            for (String line : parts) {
                lore4.add(Arconix.pl().getApi().format().formatText(line));
            }
            itemmeta4.setLore(lore4);
            item4.setItemMeta(itemmeta4);

            ItemStack itemXP = new ItemStack(Material.valueOf(plugin.getConfig().getString("settings.XP-Icon")), 1);
            ItemMeta itemmetaXP = itemXP.getItemMeta();
            itemmetaXP.setDisplayName(Lang.XPTITLE.getConfigValue(null));
            ArrayList<String> loreXP = new ArrayList<>();
            if (!maxed) {
                loreXP.add(Lang.XPLORE.getConfigValue(xpCost + ""));
            } else {
                loreXP.add(Lang.MAXED.getConfigValue(null));
            }
            itemmetaXP.setLore(loreXP);
            itemXP.setItemMeta(itemmetaXP);

            ItemStack itemECO = new ItemStack(Material.valueOf(plugin.getConfig().getString("settings.ECO-Icon")), 1);
            ItemMeta itemmetaECO = itemECO.getItemMeta();
            itemmetaECO.setDisplayName(Lang.ECOTITLE.getConfigValue(null));
            ArrayList<String> loreECO = new ArrayList<>();
            if (!maxed) {
                loreECO.add(Lang.ECOLORE.getConfigValue(Arconix.pl().getApi().format().formatEconomy(ecoCost)));
            } else {
                loreECO.add(Lang.MAXED.getConfigValue(null));
            }
            itemmetaECO.setLore(loreECO);
            itemECO.setItemMeta(itemmetaECO);

            i.setItem(13, item);

            if (showPerformance) {
                i.setItem(21, item2);
            }
            if (showReward) {
                i.setItem(22, item3);
            }
            if (showFuelDuration) {
                i.setItem(23, item4);
            }

            i.setItem(0, Methods.getBackgroundGlass(true));
            i.setItem(1, Methods.getBackgroundGlass(true));
            i.setItem(2, Methods.getBackgroundGlass(false));
            i.setItem(6, Methods.getBackgroundGlass(false));
            i.setItem(7, Methods.getBackgroundGlass(true));
            i.setItem(8, Methods.getBackgroundGlass(true));
            i.setItem(9, Methods.getBackgroundGlass(true));
            i.setItem(10, Methods.getBackgroundGlass(false));
            i.setItem(16, Methods.getBackgroundGlass(false));
            i.setItem(17, Methods.getBackgroundGlass(true));
            i.setItem(18, Methods.getBackgroundGlass(true));
            i.setItem(19, Methods.getBackgroundGlass(true));
            i.setItem(20, Methods.getBackgroundGlass(false));
            i.setItem(24, Methods.getBackgroundGlass(false));
            i.setItem(25, Methods.getBackgroundGlass(true));
            i.setItem(26, Methods.getBackgroundGlass(true));

            ItemStack hook = new ItemStack(Material.TRIPWIRE_HOOK, 1);
            ItemMeta hookmeta = hook.getItemMeta();
            hookmeta.setDisplayName(Arconix.pl().getApi().format().formatText(Lang.REMOTE_FURNACE.getConfigValue(null)));
            ArrayList<String> lorehook = new ArrayList<>();
            parts = Lang.REMOTE_FURNACE_LORE.getConfigValue(nickname).split("\\|");
            for (String line : parts) {
                lorehook.add(Arconix.pl().getApi().format().formatText(line));
            }
            if (!nickname.equals("Unset")) {
                parts = Lang.REMOTE_UTIL.getConfigValue(nickname).split("\\|");
                for (String line : parts) {
                    lorehook.add(Arconix.pl().getApi().format().formatText(line));
                }
            }

            if (plugin.dataFile.getConfig().contains("data.charged." + locationStr + ".remoteAccessList")) {
                for (String line : (List<String>) plugin.dataFile.getConfig().getList("data.charged." + locationStr + ".remoteAccessList")) {
                    lorehook.add("");
                    lorehook.add(Arconix.pl().getApi().format().formatText(Lang.REMOTE_LIST.getConfigValue()));
                    String[] halfs = line.split(":");
                    String name = halfs[1];
                    Player player = Bukkit.getPlayer(halfs[0]);
                    if (player != null) {
                        name = player.getDisplayName();
                    }
                    lorehook.add(Arconix.pl().getApi().format().formatText("&6" + name));
                }
            }
            hookmeta.setLore(lorehook);
            hook.setItemMeta(hookmeta);

            if (plugin.getConfig().getBoolean("settings.Remote-Furnaces") && p.hasPermission("EpicFurnaces.Remote")) {
                i.setItem(4, hook);
            }

            i.setItem(13, item);

            if (plugin.getConfig().getBoolean("settings.Upgrade-with-xp") && p.hasPermission("EpicFurnaces.Upgrade.XP")) {
                i.setItem(11, itemXP);
            }
            if (plugin.getConfig().getBoolean("settings.Upgrade-with-eco") && p.hasPermission("EpicFurnaces.Upgrade.ECO")) {
                i.setItem(15, itemECO);
            }

            p.openInventory(i);
            plugin.inShow.put(p, locationStr);
        } catch (Exception e) {
            Debugger.runReport(e);
        }
    }

    public void plus(Material mat, int amt) {
        try {
            if (block.getType() == Material.FURNACE || block.getType() == Material.BURNING_FURNACE) {
                if (plugin.dataFile.getConfig().getInt("data.charged." + locationStr + ".uses") == 0) {
                    plugin.dataFile.getConfig().set("data.charged." + locationStr + ".uses", 1);
                } else {
                    int uses = plugin.dataFile.getConfig().getInt("data.charged." + locationStr + ".uses");
                    plugin.dataFile.getConfig().set("data.charged." + locationStr + ".uses", uses + 1);
                }

                int tolevel = 1;
                if (mat == Material.valueOf(plugin.getConfig().getString("settings.Furnace-upgrade-cost"))) {
                    if (plugin.dataFile.getConfig().getInt("data.charged." + locationStr + ".tolevel") == 0) {
                        plugin.dataFile.getConfig().set("data.charged." + locationStr + ".tolevel", 1);
                    } else {
                        tolevel = plugin.dataFile.getConfig().getInt("data.charged." + locationStr + ".tolevel");
                        plugin.dataFile.getConfig().set("data.charged." + locationStr + ".tolevel", tolevel + 1);
                    }
                }

                int multi = plugin.getConfig().getInt("settings.Turbo-level-multiplier");

                int level = plugin.dataFile.getConfig().getInt("data.charged." + locationStr + ".level");

                if (level == 0)
                    level = 1;
                if (plugin.getConfig().contains("settings.levels.Level-" + level + ".Reward")) {
                    String reward = plugin.getConfig().getString("settings.levels.Level-" + level + ".Reward");
                    reward = reward.substring(0, reward.length() - 1);

                    int needed = ((multi * level) - tolevel) - 1;

                    if (plugin.getConfig().getBoolean("settings.Upgrade-with-material")) {
                        if (needed <= 0 && plugin.getConfig().contains("settings.levels.Level-" + (level + 1))) {
                            plugin.dataFile.getConfig().set("data.charged." + locationStr + ".tolevel", 0);
                            plugin.dataFile.getConfig().set("data.charged." + locationStr + ".level", level + 1);
                        }
                    }
                    updateCook();

                    if (plugin.dataFile.getConfig().contains("data.charged." + locationStr)) {
                        FurnaceInventory i = (FurnaceInventory) ((InventoryHolder) block.getState()).getInventory();

                        try {
                            int num = Integer.parseInt(reward);
                            double rand = Math.random() * 100;
                            if (rand <= num) {
                                if (plugin.getConfig().getBoolean("settings.Ignore-custom-recipes-for-rewards") && plugin.furnaceRecipeFile.getConfig().contains("Recipes." + i.getSmelting().getType().toString())) {
                                } else {
                                    if (!mat.equals(Material.SPONGE))
                                        i.getResult().setAmount(i.getResult().getAmount() + 1);
                                }
                            }
                        } catch (Exception ignore) {
                        }
                    }
                }
            }
        } catch (Exception e) {
            Debugger.runReport(e);
        }
    }


    public void upgrade(String type, Player p) {
        try {
            int level = plugin.dataFile.getConfig().getInt("data.charged." + locationStr + ".level");
            if (level == 0) {
                level = 1;
            }
            if (plugin.getConfig().contains("settings.levels.Level-" + (level + 1))) {

                int cost;
                if (type.equals("XP")) {
                    cost = plugin.getConfig().getInt("settings.levels.Level-" + (level + 1) + ".Cost-xp");
                } else {
                    cost = plugin.getConfig().getInt("settings.levels.Level-" + (level + 1) + ".Cost-eco");
                }

                if (type.equals("ECO")) {
                    if (plugin.getServer().getPluginManager().getPlugin("Vault") != null) {
                        RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
                        net.milkbowl.vault.economy.Economy econ = rsp.getProvider();
                        if (econ.has(p, cost)) {
                            econ.withdrawPlayer(p, cost);
                            upgradeFinal(level + 1, p);
                        } else {
                            p.sendMessage(plugin.references.getPrefix() + Lang.CANT_AFFORD.getConfigValue(null));
                        }
                    } else {
                        p.sendMessage("Vault is not installed.");
                    }
                } else if (type.equals("XP")) {
                    if (p.getLevel() >= cost || p.getGameMode() == GameMode.CREATIVE) {
                        if (p.getGameMode() != GameMode.CREATIVE) {
                            p.setLevel(p.getLevel() - cost);
                        }
                        upgradeFinal(level + 1, p);
                    } else {
                        p.sendMessage(plugin.references.getPrefix() + Lang.CANT_AFFORD.getConfigValue(null));
                    }
                }
            }
        } catch (Exception e) {
            Debugger.runReport(e);
        }
    }

    public void upgradeFinal(int level, Player p) {
        try {
            plugin.dataFile.getConfig().set("data.charged." + locationStr + ".level", level);
            p.sendMessage(plugin.references.getPrefix() + Lang.UPGRADE_MESSAGE.getConfigValue(level));
            if (plugin.getConfig().getBoolean("settings.On-upgrade-particles")) {
                Location loc = location;
                loc.setX(loc.getX() + .5);
                loc.setY(loc.getY() + .5);
                loc.setZ(loc.getZ() + .5);
                if (!plugin.v1_8 && !plugin.v1_7) {
                    p.getWorld().spawnParticle(org.bukkit.Particle.valueOf(plugin.getConfig().getString("settings.Upgrade-particle-type")), loc, 200, .5, .5, .5);
                } else {
                    p.getWorld().playEffect(loc, org.bukkit.Effect.valueOf(plugin.getConfig().getString("settings.Upgrade-particle-type")), 1, 0);
                    //Not resolving --Nova
                    //p.getWorld().spigot().playEffect(loc, org.bukkit.Effect.valueOf(plugin.getConfig().getString("settings.Upgrade-particle-type")), 1, 0, (float) 1, (float) 1, (float) 1, 1, 200, 10);
                }
            }
        } catch (Exception e) {
            Debugger.runReport(e);
        }
    }

    public void updateCook() {
        try {
            if (block != null && block.getType() != Material.AIR) {
                if (block.getType() == Material.FURNACE || block.getType() == Material.BURNING_FURNACE) {
                    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {

                        int level = plugin.dataFile.getConfig().getInt("data.charged." + locationStr + ".level");

                        if (level == 0)
                            level = 1;

                        String performance = plugin.getConfig().getString("settings.levels.Level-" + level + ".Performance");
                        performance = performance.substring(0, performance.length() - 1);

                        int num = Integer.parseInt(performance) * 2;
                        try {
                            ScriptEngineManager mgr = new ScriptEngineManager();
                            ScriptEngine engine = mgr.getEngineByName("JavaScript");
                            num = (int) Math.round(Double.parseDouble(engine.eval("(" + performance + " / 100) * 200").toString()));

                        } catch (Exception ignore) {
                        }
                        if (num > 200) {
                            num = 200;
                        }

                        if (num != 0) {
                            BlockState bs = (block.getState()); // max is 200
                            ((org.bukkit.block.Furnace) bs).setCookTime(Short.parseShort(Integer.toString(num)));
                            bs.update();
                        }
                    }, 1L);
                }
            }
        } catch (Exception e) {
            Debugger.runReport(e);
        }
    }
}
