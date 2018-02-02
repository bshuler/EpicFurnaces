package com.songoda.epicfurnaces.Events;

import com.songoda.arconix.Arconix;
import com.songoda.epicfurnaces.Furnace.Furnace;
import com.songoda.epicfurnaces.EpicFurnaces;
import com.songoda.epicfurnaces.Lang;
import com.songoda.epicfurnaces.Utils.Debugger;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by songoda on 2/26/2017.
 */
public class InventoryListeners implements Listener {

    EpicFurnaces plugin = EpicFurnaces.pl();

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        try {
            Player p = (Player) e.getWhoClicked();
            if (e.getInventory().getType().equals(InventoryType.FURNACE)) {
                if (e.getInventory().getHolder() != null) {
                    if (e.getSlotType() == InventoryType.SlotType.CRAFTING) {
                        if (plugin.blockLoc.containsKey(p.getName())) {
                            Block b = plugin.blockLoc.get(p.getName());
                            com.songoda.epicfurnaces.Furnace.Furnace furnace = new com.songoda.epicfurnaces.Furnace.Furnace(Arconix.pl().serialize().serializeLocation(b));
                            furnace.updateCook();
                        }
                    }
                }
            } else if (plugin.inShow.containsKey(p)) {
                e.setCancelled(true);
                Furnace furnace = new Furnace(plugin.inShow.get(p));
                if (e.getSlot() == 11) {
                    if (e.getCurrentItem().getItemMeta().getDisplayName() != "§l") {
                        furnace.upgrade("XP", p);
                        p.closeInventory();
                    }
                } else if (e.getSlot() == 15) {
                    if (e.getCurrentItem().getItemMeta().getDisplayName() != "§l") {
                        furnace.upgrade("ECO", p);
                        p.closeInventory();
                    }
                } else if (e.getSlot() == 4) {
                    if (e.getCurrentItem().getItemMeta().getDisplayName() != "§l") {
                        if (e.getClick().isLeftClick()) {
                            p.sendMessage(plugin.references.getPrefix() + Lang.ENTER.getConfigValue());
                            plugin.nicknameQ.put(p, furnace.location);
                            p.closeInventory();
                        } else if (e.getClick().isRightClick()) {
                            List<String> list = new ArrayList<>();
                            String key = Arconix.pl().serialize().serializeLocation(furnace.location);
                            String id = p.getUniqueId().toString() + ":" + p.getName();
                            if (plugin.dataFile.getConfig().contains("data.charged." + key + ".remoteAccessList")) {
                                list = (List<String>) plugin.dataFile.getConfig().getList("data.charged." + key + ".remoteAccessList");
                                for (String line : (List<String>) plugin.dataFile.getConfig().getList("data.charged." + key + ".remoteAccessList")) {
                                    if (id.equals(line)) {
                                        e.setCancelled(true);
                                        return;
                                    }
                                }
                            }
                            list.add(id);
                            plugin.dataFile.getConfig().set("data.charged." + key + ".remoteAccessList", list);
                            furnace.open(p);
                        }
                    }
                }
                if (e.getSlot() != 64537) {
                    if (e.getInventory().getType() == InventoryType.ANVIL) {
                        if (e.getAction() != InventoryAction.NOTHING) {
                            if (e.getCurrentItem().getType() != Material.AIR) {
                                ItemStack item = e.getCurrentItem();
                                if (item.getType() == Material.FURNACE) {
                                    e.setCancelled(true);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception ee) {
            Debugger.runReport(ee);
        }
    }


    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        try {
            final Player player = (Player) event.getPlayer();
            plugin.inShow.remove(player);
        } catch (Exception e) {
            Debugger.runReport(e);
        }
    }

}
