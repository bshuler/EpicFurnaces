package com.songoda.epicfurnaces.listeners;

import com.songoda.arconix.plugin.Arconix;
import com.songoda.epicfurnaces.EpicFurnaces;
import com.songoda.epicfurnaces.furnace.Furnace;
import com.songoda.epicfurnaces.utils.Debugger;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by songoda on 2/26/2017.
 */
public class InventoryListeners implements Listener {

    private final EpicFurnaces instance;

    public InventoryListeners(EpicFurnaces instance) {
        this.instance = instance;
    }

    @EventHandler
    public void onInventoryMove(InventoryMoveItemEvent e) {
        if (!e.getDestination().getType().equals(InventoryType.FURNACE)
                || instance.v1_7
                || instance.v1_8
                || e.getDestination().getItem(0) == null
                || e.getDestination().getItem(0).getAmount() != 1) {
            return;
        }
        instance.getFurnaceManager().getFurnace(e.getDestination().getLocation()).updateCook();
    }

    @SuppressWarnings("unchecked")
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        try {
            Player p = (Player) e.getWhoClicked();
            if (e.getInventory().getType().equals(InventoryType.FURNACE)
                    && e.getInventory().getHolder() != null
                    && e.getSlotType() == InventoryType.SlotType.CRAFTING) {
                Block block;
                if (!instance.v1_7 && !instance.v1_8) {
                    block = e.getInventory().getLocation().getBlock();
                } else if (instance.blockLoc.containsKey(p.getName())) {
                    block = instance.blockLoc.get(p.getName());
                } else return;
                instance.getFurnaceManager().getFurnace(block).updateCook();
            } else if (instance.inShow.containsKey(p)) {
                e.setCancelled(true);
                Furnace furnace = instance.inShow.get(p);
                if (e.getSlot() == 11) {
                    if (!e.getCurrentItem().getItemMeta().getDisplayName().equals("§l")) {
                        furnace.upgrade("XP", p);
                        p.closeInventory();
                    }
                } else if (e.getSlot() == 15) {
                    if (!e.getCurrentItem().getItemMeta().getDisplayName().equals("§l")) {
                        furnace.upgrade("ECO", p);
                        p.closeInventory();
                    }
                } else if (e.getSlot() == 4) {
                    if (!e.getCurrentItem().getItemMeta().getDisplayName().equals("§l")) {
                        if (e.getClick().isLeftClick()) {
                            p.sendMessage(instance.references.getPrefix() + instance.getLocale().getMessage("event.remote.enter"));
                            instance.nicknameQ.put(p, furnace.getLocation());
                            p.closeInventory();
                        } else if (e.getClick().isRightClick()) {
                            List<String> list = new ArrayList<>();
                            String key = Arconix.pl().getApi().serialize().serializeLocation(furnace.getLocation());
                            String id = p.getUniqueId().toString() + ":" + p.getName();
                            if (instance.dataFile.getConfig().contains("data.charged." + key + ".remoteAccessList")) {
                                list = (List<String>) instance.dataFile.getConfig().getList("data.charged." + key + ".remoteAccessList");
                                for (String line : (List<String>) instance.dataFile.getConfig().getList("data.charged." + key + ".remoteAccessList")) {
                                    if (id.equals(line)) {
                                        e.setCancelled(true);
                                        return;
                                    }
                                }
                            }
                            list.add(id);
                            instance.dataFile.getConfig().set("data.charged." + key + ".remoteAccessList", list);
                            furnace.openOverview(p);
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
            instance.inShow.remove(player);
        } catch (Exception e) {
            Debugger.runReport(e);
        }
    }
}