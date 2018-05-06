package com.songoda.epicfurnaces.listeners;

import com.songoda.arconix.plugin.Arconix;
import com.songoda.epicfurnaces.EpicFurnaces;
import com.songoda.epicfurnaces.furnace.Furnace;
import com.songoda.epicfurnaces.utils.Debugger;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;

/**
 * Created by songoda on 2/26/2017.
 */
public class FurnaceListeners implements Listener {

    private EpicFurnaces plugin = EpicFurnaces.pl();

    @EventHandler
    public void onCook(FurnaceSmeltEvent e) {
        try {
            Block b = e.getBlock();
            Material mat;
            if (e.getBlock().isBlockPowered() && plugin.getConfig().getBoolean("settings.Redstone-Deactivate")) {
                e.setCancelled(true);
                return;
            }
            if (e.getResult() != null) {
                mat = e.getResult().getType();
                Furnace furnace = new Furnace(Arconix.pl().getApi().serialize().serializeLocation(b));
                furnace.plus(mat, e.getResult().getAmount());
            }
        } catch (Exception ee) {
            Debugger.runReport(ee);
        }
    }

    @EventHandler
    public void onFuel(FurnaceBurnEvent e) {
        try {
            if (e.getFuel() != null) {
                int level = plugin.dataFile.getConfig().getInt("data.charged." + Arconix.pl().getApi().serialize().serializeLocation(e.getBlock()) + ".level");

                if (level == 0)
                    level = 1;

                if (plugin.getConfig().contains("settings.levels.Level-" + level + ".Fuel-duration")) {
                    String fd = plugin.getConfig().getString("settings.levels.Level-" + level + ".Fuel-duration");
                    fd = fd.substring(0, fd.length() - 1);

                    int num = Integer.parseInt(fd);
                    int per = (e.getBurnTime() / 100) * num;
                    e.setBurnTime(e.getBurnTime() + per);
                }
            }
        } catch (Exception ee) {
            Debugger.runReport(ee);
        }
    }
}
