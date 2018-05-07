package com.songoda.epicfurnaces.api;

import com.songoda.epicfurnaces.utils.Debugger;
import org.bukkit.inventory.ItemStack;

/**
 * Created by songo on 5/27/2017.
 */
public class EpicFurnacesAPI {

    public int getILevel(ItemStack item) {
        try {
            if (item.getItemMeta().getDisplayName().contains("§9Fur§9nace")) {
                String lev = item.getItemMeta().getDisplayName().replace("§eLevel ", "");
                return Integer.parseInt(lev.replace(" §9Fur§9nace", ""));
            } else {
                if (item.getItemMeta().getDisplayName().contains(":")) {
                    String arr[] = (item.getItemMeta().getDisplayName().replace("§", "")).split(":");
                    return Integer.parseInt(arr[0]);
                } else {
                    return 1;
                }
            }
        } catch (Exception e) {
            Debugger.runReport(e);
        }
        return 9999;
    }

    public int getIUses(ItemStack item) {
        try {
            if (item.getItemMeta().getDisplayName().contains(":")) {
                String arr[] = (item.getItemMeta().getDisplayName().replace("§", "")).split(":");
                return Integer.parseInt(arr[1]);
            } else {
                return 0;
            }
        } catch (Exception e) {
            Debugger.runReport(e);
        }
        return 9999;
    }
}