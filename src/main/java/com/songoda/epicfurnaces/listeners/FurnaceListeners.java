package com.songoda.epicfurnaces.listeners;

import com.songoda.epicfurnaces.EpicFurnaces;
import com.songoda.epicfurnaces.furnace.Furnace;
import com.songoda.epicfurnaces.furnace.Level;
import com.songoda.epicfurnaces.utils.Debugger;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;

/**
 * Created by songoda on 2/26/2017.
 */
public class FurnaceListeners implements Listener {

    private final EpicFurnaces instance;

    public FurnaceListeners(EpicFurnaces instance) {
        this.instance = instance;
    }

    @EventHandler
    public void onCook(FurnaceSmeltEvent e) {
        try {
            Block b = e.getBlock();
            if ((e.getBlock().isBlockPowered() && instance.getConfig().getBoolean("settings.Redstone-Deactivate")) || e.getResult() == null) {
                e.setCancelled(true);
                return;
            }
            Furnace furnace = instance.getFurnaceManager().getFurnace(b.getLocation());

            if (furnace != null)
                instance.getFurnaceManager().getFurnace(b.getLocation()).plus(e);
        } catch (Exception ee) {
            Debugger.runReport(ee);
        }
    }

    @EventHandler
    public void onFuel(FurnaceBurnEvent e) {
        try {
            if (e.getFuel() != null) {
                Furnace furnace = instance.getFurnaceManager().getFurnace(e.getBlock().getLocation());

                Level level = furnace != null ? furnace.getLevel() : instance.getLevelManager().getLowestLevel();

                if (level.getFuelDuration() != 0) return;

                int num = level.getFuelDuration();
                int per = (e.getBurnTime() / 100) * num;
                e.setBurnTime(e.getBurnTime() + per);

            }
        } catch (Exception ee) {
            Debugger.runReport(ee);
        }
    }
}