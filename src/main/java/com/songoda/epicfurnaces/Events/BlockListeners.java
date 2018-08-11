package com.songoda.epicfurnaces.events;

import com.songoda.arconix.plugin.Arconix;
import com.songoda.epicfurnaces.EpicFurnaces;
import com.songoda.epicfurnaces.furnace.Furnace;
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

import java.util.ArrayList;

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
            if (e.getBlock().getType() != Material.FURNACE || !e.getItemInHand().getItemMeta().hasDisplayName()) return;
                    ItemStack item = e.getItemInHand();

                    Location location = e.getBlock().getLocation();

                    if (instance.getApi().getILevel(item) != 1) {
                        instance.getFurnaceManager().addFurnace(location, new Furnace(location, instance.getLevelManager().getLevel(instance.getApi().getILevel(item)), null, instance.getApi().getIUses(item), 0, new ArrayList<>()));
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
            if (b.getType() != Material.FURNACE) {
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