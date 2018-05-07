package com.songoda.epicfurnaces.listeners;

import com.songoda.epicfurnaces.EpicFurnaces;
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

    private final EpicFurnaces instance;

    public InteractListeners(EpicFurnaces instance) {
        this.instance = instance;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onClick(PlayerInteractEvent e) {
        try {
            if (e.getClickedBlock() == null) return;

            Player player = e.getPlayer();
            Block block = e.getClickedBlock();
            if (!player.hasPermission("EpicFurnaces.overview")
                    || !instance.hooks.canBuild(player, e.getClickedBlock().getLocation())
                    || e.getAction() != Action.LEFT_CLICK_BLOCK
                    || player.isSneaking()
                    || (block.getType() != Material.FURNACE && block.getType() != Material.BURNING_FURNACE)
                    || player.getItemInHand().getType().name().contains("PICKAXE")) {
                return;
            }

            e.setCancelled(true);

            instance.getFurnaceManager().getFurnace(block.getLocation()).openOverview(player);

        } catch (Exception ee) {
            Debugger.runReport(ee);
        }
    }
}