package com.songoda.epicfurnaces.furnace;

import com.songoda.arconix.plugin.Arconix;
import com.songoda.epicfurnaces.EpicFurnaces;
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
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredServiceProvider;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.*;

/**
 * Created by songoda on 3/7/2017.
 */
public class Furnace {

    private Location location;
    private Level level;

    private String nickname;

    private int uses, tolevel;

    private List<String> accessList = new ArrayList<>();

    private final EpicFurnaces instance = EpicFurnaces.getInstance();

    public Furnace(Location location, Level level, String nickname, int uses, int tolevel, List<String> accessList) {
        this.location = location;
        this.level = level;
        this.uses = uses;
        this.tolevel = tolevel;
        this.nickname = nickname;
        this.accessList = accessList;
    }

    public Furnace(Block block, Level level, String nickname, int uses, int tolevel, List<String> accessList) {
        this(block.getLocation(), level, nickname, uses, tolevel, accessList);
    }


    public void openOverview(Player p) {
        try {
            EpicFurnaces instance = EpicFurnaces.getInstance();
            if (!p.hasPermission("epicdispensers.overview")) return;
            instance.blockLoc.put(p.getName(), location.getBlock());

            Level nextLevel = instance.getLevelManager().getHighestLevel().getLevel() > level.getLevel() ? instance.getLevelManager().getLevel(level.getLevel() + 1) : null;

            int multi = instance.getConfig().getInt("settings.Turbo-level-multiplier");

            int needed = (multi * level.getLevel()) - tolevel;

            ItemStack item = new ItemStack(Material.FURNACE, 1);

            ItemMeta itemmeta = item.getItemMeta();
            itemmeta.setDisplayName(instance.getLocale().getMessage("interface.furnace.currentlevel", level.getLevel()));
            ArrayList<String> lore = new ArrayList<>();
            lore.add(instance.getLocale().getMessage("interface.furnace.smeltedx", uses));
            lore.addAll(this.level.getDescription());
            lore.add("");
            if (nextLevel == null)
                lore.add(instance.getLocale().getMessage("interface.furnace.alreadymaxed"));
            else {
                lore.add(instance.getLocale().getMessage("interface.furnace.level", nextLevel.getLevel()));
                lore.addAll(nextLevel.getDescription());

                if (instance.getConfig().getBoolean("settings.Upgrade-with-material")) {
                    lore.add(instance.getLocale().getMessage("interface.furnace.tolevel", needed, Methods.cleanString(instance.getConfig().getString("settings.furnace-upgrade-cost"))));
                }
            }

            itemmeta.setLore(lore);
            item.setItemMeta(itemmeta);

            Inventory i = Bukkit.createInventory(null, 27, Arconix.pl().getApi().format().formatText(Methods.formatName(level.getLevel(), 0, false)));

            int nu = 0;
            while (nu != 27) {
                i.setItem(nu, Methods.getGlass());
                nu++;
            }


            ItemStack item2 = new ItemStack(Material.valueOf(instance.getConfig().getString("settings.Performance-Icon")), 1);
            ItemMeta itemmeta2 = item2.getItemMeta();
            itemmeta2.setDisplayName(instance.getLocale().getMessage("interface.furnace.performancetitle")); //greyed out until available
            ArrayList<String> lore2 = new ArrayList<>();

            String[] parts = instance.getLocale().getMessage("interface.furnace.performanceinfo", level.getPerformance()).split("\\|");
            lore.add("");
            for (String line : parts) {
                lore2.add(Arconix.pl().getApi().format().formatText(line));
            }
            itemmeta2.setLore(lore2);
            item2.setItemMeta(itemmeta2);

            ItemStack item3 = new ItemStack(Material.valueOf(instance.getConfig().getString("settings.Reward-Icon")), 1);
            ItemMeta itemmeta3 = item3.getItemMeta();
            itemmeta3.setDisplayName(instance.getLocale().getMessage("interface.furnace.rewardtitle"));
            ArrayList<String> lore3 = new ArrayList<>();

            parts = instance.getLocale().getMessage("interface.furnace.rewardinfo", level.getPerformance()).split("\\|");
            lore.add("");
            for (String line : parts) {
                lore3.add(Arconix.pl().getApi().format().formatText(line));
            }
            itemmeta3.setLore(lore3);
            item3.setItemMeta(itemmeta3);


            ItemStack item4 = new ItemStack(Material.valueOf(instance.getConfig().getString("settings.FuelDuration-Icon")), 1);
            ItemMeta itemmeta4 = item4.getItemMeta();
            itemmeta4.setDisplayName(instance.getLocale().getMessage("interface.furnace.fueldurationtitle"));
            ArrayList<String> lore4 = new ArrayList<>();

            parts = instance.getLocale().getMessage("interface.furnace.fueldurationinfo", level.getFuelDuration()).split("\\|");
            lore.add("");
            for (String line : parts) {
                lore4.add(Arconix.pl().getApi().format().formatText(line));
            }
            itemmeta4.setLore(lore4);
            item4.setItemMeta(itemmeta4);

            ItemStack itemXP = new ItemStack(Material.valueOf(instance.getConfig().getString("settings.XP-Icon")), 1);
            ItemMeta itemmetaXP = itemXP.getItemMeta();
            itemmetaXP.setDisplayName(instance.getLocale().getMessage("interface.furnace.upgradewithxp"));
            ArrayList<String> loreXP = new ArrayList<>();
            if (nextLevel != null)
                loreXP.add(instance.getLocale().getMessage("interface.furnace.upgradewithxplore", level.getCostExperiance()));
            else
                loreXP.add(instance.getLocale().getMessage("interface.furnace.alreadymaxed"));
            itemmetaXP.setLore(loreXP);
            itemXP.setItemMeta(itemmetaXP);

            ItemStack itemECO = new ItemStack(Material.valueOf(instance.getConfig().getString("settings.ECO-Icon")), 1);
            ItemMeta itemmetaECO = itemECO.getItemMeta();
            itemmetaECO.setDisplayName(instance.getLocale().getMessage("interface.furnace.upgradewitheconomy"));
            ArrayList<String> loreECO = new ArrayList<>();
            if (nextLevel != null)
                loreECO.add(instance.getLocale().getMessage("interface.furnace.upgradewitheconomylore", Arconix.pl().getApi().format().formatEconomy(level.getCostEconomy())));
            else
                loreECO.add(instance.getLocale().getMessage("interface.furnace.alreadymaxed"));
            itemmetaECO.setLore(loreECO);
            itemECO.setItemMeta(itemmetaECO);

            i.setItem(13, item);

            if (level.getPerformance() != 0) {
                i.setItem(21, item2);
            }
            if (level.getReward() != null) {
                i.setItem(22, item3);
            }
            if (level.getFuelDuration() != 0) {
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
            hookmeta.setDisplayName(instance.getLocale().getMessage("interface.furnace.remotefurnace"));
            ArrayList<String> lorehook = new ArrayList<>();

            parts = instance.getLocale().getMessage("interface.furnace.remotefurnacelore", nickname == null ? "Unset" : nickname).split("\\|");

            for (String line : parts) {
                lorehook.add(Arconix.pl().getApi().format().formatText(line));
            }
            if (nickname != null) {
                parts = instance.getLocale().getMessage("interface.furnace.utilize", nickname).split("\\|");
                for (String line : parts) {
                    lorehook.add(Arconix.pl().getApi().format().formatText(line));
                }
            }

            for (String line : accessList) {
                lorehook.add("");
                lorehook.add(instance.getLocale().getMessage("interface.furnace.remotelist"));
                String[] halfs = line.split(":");
                String name = halfs[1];
                Player player = Bukkit.getPlayer(halfs[0]);
                if (player != null) {
                    name = player.getDisplayName();
                }
                lorehook.add(Arconix.pl().getApi().format().formatText("&6" + name));
            }
            hookmeta.setLore(lorehook);
            hook.setItemMeta(hookmeta);

            if (instance.getConfig().getBoolean("settings.Remote-Furnaces") && p.hasPermission("EpicFurnaces.Remote")) {
                i.setItem(4, hook);
            }

            i.setItem(13, item);

            if (instance.getConfig().getBoolean("settings.Upgrade-with-xp") && p.hasPermission("EpicFurnaces.Upgrade.XP")) {
                i.setItem(11, itemXP);
            }
            if (instance.getConfig().getBoolean("settings.Upgrade-with-eco") && p.hasPermission("EpicFurnaces.Upgrade.ECO")) {
                i.setItem(15, itemECO);
            }

            p.openInventory(i);
            instance.inShow.put(p, this);
        } catch (Exception e) {
            Debugger.runReport(e);
        }
    }

    public void plus(FurnaceSmeltEvent e) {
        try {
            Block block = location.getBlock();
            if (block.getType() == Material.FURNACE && block.getType() == Material.BURNING_FURNACE) {
                return;
            }

            uses++;
            tolevel++;

            int multi = instance.getConfig().getInt("settings.Turbo-level-multiplier");

            if (level.getReward() == null) return;

            String reward = level.getReward();
            String amt[] = {"1", "1"};
            if (reward.contains(":")) {
                String[] rewardSplit = reward.split(":");
                reward = rewardSplit[0].substring(0, rewardSplit[0].length() - 1);
                if (rewardSplit[1].contains("-"))
                    amt = rewardSplit[1].split("-");
                else {
                    amt[0] = rewardSplit[1];
                    amt[1] = rewardSplit[0];
                }
            }

            int needed = ((multi * level.getLevel()) - tolevel) - 1;

            if (instance.getConfig().getBoolean("settings.Upgrade-with-material")
                    && needed <= 0
                    && instance.getConfig().contains("settings.levels.Level-" + (level.getLevel() + 1))) {
                tolevel = 0;
                level = instance.getLevelManager().getLevel(this.level.getLevel() + 1);

            }
            updateCook();

            FurnaceInventory i = (FurnaceInventory) ((InventoryHolder) block.getState()).getInventory();

            int num = Integer.parseInt(reward);
            double rand = Math.random() * 100;
            if (rand >= num
                    || e.getResult().equals(Material.SPONGE)
                    || instance.getConfig().getBoolean("settings.Ignore-custom-recipes-for-rewards")
                    && instance.furnaceRecipeFile.getConfig().contains("Recipes." + i.getSmelting().getType().toString())) {
                return;
            }

            int r = Integer.parseInt(amt[0]);
            if (Integer.parseInt(amt[0]) != Integer.parseInt(amt[1]))
                r = (int) (Math.random() * ((Integer.parseInt(amt[1]) - Integer.parseInt(amt[0])) + 1)) + Integer.parseInt(amt[0]);


            if (e.getResult() != null) {
                e.getResult().setAmount(e.getResult().getAmount() + r);
                return;
            }

            e.setResult(new ItemStack(e.getResult().getType(), r));
        } catch (Exception ex) {
            Debugger.runReport(ex);
        }
    }


    public void upgrade(String type, Player player) {
        try {
            EpicFurnaces instance = EpicFurnaces.getInstance();
            if (instance.getLevelManager().getLevels().containsKey(this.level.getLevel() + 1)) {

                Level level = instance.getLevelManager().getLevel(this.level.getLevel() + 1);
                int cost;
                if (type.equals("XP")) {
                    cost = level.getCostExperiance();
                } else {
                    cost = level.getCostEconomy();
                }

                if (type.equals("ECO")) {
                    if (instance.getServer().getPluginManager().getPlugin("Vault") != null) {
                        RegisteredServiceProvider<Economy> rsp = instance.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
                        net.milkbowl.vault.economy.Economy econ = rsp.getProvider();
                        if (econ.has(player, cost)) {
                            econ.withdrawPlayer(player, cost);
                            upgradeFinal(level, player);
                        } else {
                            player.sendMessage(instance.getLocale().getMessage("event.upgrade.cannotafford"));
                        }
                    } else {
                        player.sendMessage("Vault is not installed.");
                    }
                } else if (type.equals("XP")) {
                    if (player.getLevel() >= cost || player.getGameMode() == GameMode.CREATIVE) {
                        if (player.getGameMode() != GameMode.CREATIVE) {
                            player.setLevel(player.getLevel() - cost);
                        }
                        upgradeFinal(level, player);
                    } else {
                        player.sendMessage(instance.getLocale().getMessage("event.upgrade.cannotafford"));
                    }
                }
            }
        } catch (Exception ex) {
            Debugger.runReport(ex);
        }
    }

    public boolean isMaxed(int level) {
        return EpicFurnaces.getInstance().getConfig().contains("settings.levels.Level-" + (level + 1));
    }


    public void upgradeFinal(Level level, Player player) {
        try {
            EpicFurnaces instance = EpicFurnaces.getInstance();
            this.level = level;
            if (instance.getLevelManager().getHighestLevel() != level) {
                player.sendMessage(instance.getLocale().getMessage("event.upgrade.success", level.getLevel()));
            } else {
                player.sendMessage(instance.getLocale().getMessage("event.upgrade.maxed", level.getLevel()));
            }
            Location loc = location.clone().add(.5, .5, .5);
            if (!instance.v1_8 && !instance.v1_7) {
                player.getWorld().spawnParticle(org.bukkit.Particle.valueOf(instance.getConfig().getString("settings.Upgrade-particle-type")), loc, 200, .5, .5, .5);
            } else {
                //Doesn't resolve --Nova
                player.getWorld().playEffect(loc, org.bukkit.Effect.valueOf(instance.getConfig().getString("settings.Upgrade-particle-type")), 1, 0);
                //player.getWorld().spigot().playEffect(loc, org.bukkit.Effect.valueOf(instance.getConfig().getString("settings.Upgrade-particle-type")), 1, 0, (float) 1, (float) 1, (float) 1, 1, 200, 10);
            }
            if (instance.getConfig().getBoolean("settings.sounds")) {
                if (instance.getLevelManager().getHighestLevel() == level) {
                    if (!instance.v1_8 && !instance.v1_7) {
                        player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 0.6F, 15.0F);
                    } else {
                        player.playSound(player.getLocation(), org.bukkit.Sound.valueOf("LEVEL_UP"), 2F, 15.0F);
                    }
                } else {
                    if (!instance.v1_10 && !instance.v1_9 && !instance.v1_8 && !instance.v1_7) {
                        player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 2F, 25.0F);
                        player.playSound(player.getLocation(), org.bukkit.Sound.BLOCK_NOTE_CHIME, 2F, 25.0F);
                        Bukkit.getScheduler().scheduleSyncDelayedTask(instance, () -> player.playSound(player.getLocation(), org.bukkit.Sound.BLOCK_NOTE_CHIME, 1.2F, 35.0F), 5L);
                        Bukkit.getScheduler().scheduleSyncDelayedTask(instance, () -> player.playSound(player.getLocation(), org.bukkit.Sound.BLOCK_NOTE_CHIME, 1.8F, 35.0F), 10L);
                    } else {
                        player.playSound(player.getLocation(), org.bukkit.Sound.valueOf("LEVEL_UP"), 2F, 25.0F);
                    }
                }
            }
        } catch (Exception ex) {
            Debugger.runReport(ex);
        }
    }

    private Map<String, Integer> cache = new HashMap<>();

    public void updateCook() {
        try {
            Block block = location.getBlock();
            if (block != null && block.getType() != Material.AIR) {
                if (block.getType() == Material.FURNACE || block.getType() == Material.BURNING_FURNACE) {
                    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(instance, () -> {


                        int performance = level.getPerformance();

                        int num = performance * 2;
                        try {
                            String equation = "(" + performance + " / 100) * 200";
                            if (!cache.containsKey(equation)) {
                                num = cache.get(equation);
                                ScriptEngineManager mgr = new ScriptEngineManager();
                                ScriptEngine engine = mgr.getEngineByName("JavaScript");
                                num = (int) Math.round(Double.parseDouble(engine.eval("(" + performance + " / 100) * 200").toString()));
                                cache.put(equation, num);
                            }

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

    public Level getLevel() {
        return level;
    }

    public List<String> getAccessList() {
        return Collections.unmodifiableList(accessList);
    }

    public boolean addToAccessList(String string) {
        return accessList.add(string);
    }

    public boolean removeFromAccessList(String string) {
        return accessList.remove(string);
    }

    public void clearAccessList() {
        accessList.clear();
    }

    public Location getLocation() {
        return location;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }

    public int getUses() {
        return uses;
    }

    public int getTolevel() {
        return tolevel;
    }
}
