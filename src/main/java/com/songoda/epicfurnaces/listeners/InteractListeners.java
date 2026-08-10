package com.songoda.epicfurnaces.listeners;

import com.songoda.epicfurnaces.EpicFurnacesPlugin;
import com.songoda.epicfurnaces.furnace.EFurnace;
import com.songoda.epicfurnaces.utils.Debugger;
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

    private final EpicFurnacesPlugin instance;

    public InteractListeners(EpicFurnacesPlugin instance) {
        this.instance = instance;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onClick(PlayerInteractEvent e) {
        try {
            if (e.getClickedBlock() == null) return;

            Player player = e.getPlayer();
            Block block = e.getClickedBlock();
            if (!player.hasPermission("EpicFurnaces.overview")
                    || !instance.canBuild(player, e.getClickedBlock().getLocation())
                    || e.getAction() != Action.LEFT_CLICK_BLOCK
                    || player.isSneaking()
                    || (block.getType() != Material.FURNACE)
                    || player.getInventory().getItemInMainHand().getType().name().contains("PICKAXE")) {
                return;
            }

            e.setCancelled(true);

            ((EFurnace)instance.getFurnaceManager().getFurnace(block.getLocation())).openOverview(player);

        } catch (Exception ee) {
            Debugger.runReport(ee);
        }
    }
}