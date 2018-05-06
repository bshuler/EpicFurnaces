package com.songoda.epicfurnaces.listeners;

import com.songoda.arconix.plugin.Arconix;
import com.songoda.epicfurnaces.EpicFurnaces;
import com.songoda.epicfurnaces.utils.Debugger;
import com.songoda.epicfurnaces.utils.Methods;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Created by songoda on 2/26/2017.
 */
public class BlockListeners implements Listener {

    private EpicFurnaces plugin = EpicFurnaces.pl();

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        try {
            if (e.getBlock().getType() == Material.FURNACE) {
                if (e.getItemInHand().getItemMeta().hasDisplayName()) {
                    ItemStack item = e.getItemInHand();

                    byte b = e.getBlock().getData();

                    e.getBlock().setType(Material.AIR);

                    Location location = e.getBlock().getLocation();
                    location.getBlock().setType(Material.FURNACE);
                    location.getBlock().setData(b);

                    if (plugin.getApi().getILevel(item) != 1) {
                        plugin.dataFile.getConfig().set("data.charged." + Arconix.pl().getApi().serialize().serializeLocation(location.getBlock()) + ".level", plugin.getApi().getILevel(item));
                        plugin.dataFile.getConfig().set("data.charged." + Arconix.pl().getApi().serialize().serializeLocation(location.getBlock()) + ".uses", plugin.getApi().getIUses(item));
                    }
                }
            }
        } catch (Exception ee) {
            Debugger.runReport(ee);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent e) {
        try {
            if (!e.isCancelled()) {
                if (e.getPlayer().hasPermission("epicfurnaces.furnace") || e.getPlayer().hasPermission("epicfurnaces.*")) {
                    Block b = e.getBlock();
                    if (b.getType() == Material.FURNACE || b.getType() == Material.BURNING_FURNACE) {
                        int uses = plugin.dataFile.getConfig().getInt("data.charged." + Arconix.pl().getApi().serialize().serializeLocation(b) + ".uses");

                        int level = plugin.dataFile.getConfig().getInt("data.charged." + Arconix.pl().getApi().serialize().serializeLocation(b) + ".level");

                        if (level != 0) {
                            e.setCancelled(true);
                            ItemStack item = new ItemStack(Material.FURNACE, 1);
                            ItemMeta itemmeta = item.getItemMeta();
                            itemmeta.setDisplayName(Arconix.pl().getApi().format().formatText(Methods.formatName(level, uses, true)));

                            item.setItemMeta(itemmeta);

                            e.getBlock().setType(Material.AIR);
                            e.getBlock().getLocation().getWorld().dropItemNaturally(e.getBlock().getLocation(), item);
                        }
                    }
                    plugin.dataFile.getConfig().set("data.charged." + Arconix.pl().getApi().serialize().serializeLocation(b), null);
                }
            }
        } catch (Exception ee) {
            Debugger.runReport(ee);
        }
    }
}