package com.songoda.epicfurnaces.Events;

import com.songoda.arconix.Arconix;
import com.songoda.epicfurnaces.EpicFurnaces;
import com.songoda.epicfurnaces.Furnace.Furnace;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Created by songoda on 2/26/2017.
 */
public class InteractListeners implements Listener {

    EpicFurnaces plugin = EpicFurnaces.pl();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onClick(PlayerInteractEvent e) {
        if (!e.isCancelled() && e.getClickedBlock() != null) {
            Player p = e.getPlayer();
            if (p.hasPermission("turbocharged.furnace") || p.hasPermission("turbocharged.*")) {
                if (plugin.hooks.canBuild(p, e.getClickedBlock().getLocation())) {
                    if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
                        Block b = e.getClickedBlock();
                        if (b.getType() == Material.FURNACE || b.getType() == Material.BURNING_FURNACE) {
                            if (!p.isSneaking()) {
                                if (p.getItemInHand().getType() != Material.WOOD_PICKAXE && p.getItemInHand().getType() != Material.STONE_PICKAXE &&
                                        p.getItemInHand().getType() != Material.IRON_PICKAXE && p.getItemInHand().getType() != Material.DIAMOND_PICKAXE) {
                                    e.setCancelled(true);
                                    Furnace furnace = new Furnace(Arconix.pl().serialize().serializeLocation(b));
                                    furnace.open(e.getPlayer());
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
