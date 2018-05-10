package com.songoda.epicfurnaces.Events;

import com.songoda.arconix.plugin.Arconix;
import com.songoda.epicfurnaces.EpicFurnaces;
import com.songoda.epicfurnaces.Furnace.Furnace;
import com.songoda.epicfurnaces.Utils.Debugger;
import com.songoda.epicfurnaces.Utils.Methods;
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

    private final EpicFurnaces instance;

    public BlockListeners(EpicFurnaces instance) {
        this.instance = instance;
    }

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

                    if (instance.getApi().getILevel(item) != 1) {
                        instance.dataFile.getConfig().set("data.charged." + Arconix.pl().getApi().serialize().serializeLocation(location.getBlock()) + ".level", instance.getApi().getILevel(item));
                        instance.dataFile.getConfig().set("data.charged." + Arconix.pl().getApi().serialize().serializeLocation(location.getBlock()) + ".uses", instance.getApi().getIUses(item));
                    }
                }
            }
        } catch (Exception ee) {
            Debugger.runReport(ee);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent e) {
        try {
            if (!e.getPlayer().hasPermission("EpicFurnaces.overview") && !e.getPlayer().hasPermission("epicfurnaces.*")) {
                return;
            }
            Block b = e.getBlock();
            if (b.getType() != Material.FURNACE && b.getType() != Material.BURNING_FURNACE) {
                return;
            }
            Furnace furnace = instance.getFurnaceManager().getFurnace(b);
            int level = instance.getFurnaceManager().getFurnace(b).getLevel().getLevel();

            if (level != 0) {
                e.setCancelled(true);
                ItemStack item = new ItemStack(Material.FURNACE, 1);
                ItemMeta itemmeta = item.getItemMeta();

                if (instance.getConfig().getBoolean("settings.Remember-furnace-Levels"))
                    itemmeta.setDisplayName(Arconix.pl().getApi().format().formatText(Methods.formatName(level, furnace.getUses(), true)));

                item.setItemMeta(itemmeta);

                e.getBlock().setType(Material.AIR);
                e.getBlock().getLocation().getWorld().dropItemNaturally(e.getBlock().getLocation(), item);
            }
            instance.getFurnaceManager().removeFurnace(b.getLocation());

        } catch (Exception ee) {
            Debugger.runReport(ee);
        }
    }
}