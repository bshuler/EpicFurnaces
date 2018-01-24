package com.songoda.epicfurnaces.Events;

import com.songoda.arconix.Arconix;
import com.songoda.epicfurnaces.Furnace.Furnace;
import com.songoda.epicfurnaces.EpicFurnaces;
import org.bukkit.Bukkit;
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

    EpicFurnaces plugin = EpicFurnaces.pl();

    @EventHandler
    public void onCook(FurnaceSmeltEvent e){
        Block b = e.getBlock();
        Material mat;
        if (e.getBlock().isBlockPowered() && plugin.getConfig().getBoolean("settings.Redstone-Deactivate")) {
            e.setCancelled(true);
            return;
        }
        if (e.getResult() != null) {
            mat = e.getResult().getType();
            Furnace furnace = new Furnace(Arconix.pl().serialize().serializeLocation(b));
            furnace.plus(mat, e.getResult().getAmount());
        }
    }

    @EventHandler
    public void onFuel(FurnaceBurnEvent e) {
        if (e.getFuel() != null) {
            int level = plugin.dataFile.getConfig().getInt("data.charged." + Arconix.pl().serialize().serializeLocation(e.getBlock()) + ".level");

            if (level == 0)
                level = 1;

            if (plugin.getConfig().contains("settings.levels.Level-" + level + ".Fuel-duration")) {
                String fd = plugin.getConfig().getString("settings.levels.Level-" + level + ".Fuel-duration");
                fd = fd.substring(0, fd.length()-1);

                int num = Integer.parseInt(fd);
                int per = (e.getBurnTime() / 100) * num;
                e.setBurnTime(e.getBurnTime() + per);
            }
        }
    }
}
