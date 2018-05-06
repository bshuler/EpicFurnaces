package com.songoda.epicfurnaces.listeners;

import com.songoda.arconix.plugin.Arconix;
import com.songoda.epicfurnaces.EpicFurnaces;
import com.songoda.epicfurnaces.Lang;
import com.songoda.epicfurnaces.utils.Debugger;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by songoda on 2/25/2017.
 */
public class ChatListeners implements Listener {

    private EpicFurnaces plugin = EpicFurnaces.pl();

    @EventHandler
    public void chatListeners(AsyncPlayerChatEvent e) {
        try {
            if (!e.isCancelled()) {
                if (plugin.nicknameQ.containsKey(e.getPlayer())) {
                    e.setCancelled(true);

                    Location loc = plugin.nicknameQ.get(e.getPlayer());
                    plugin.nicknameQ.remove(e.getPlayer());

                    boolean match = false;
                    if (plugin.dataFile.getConfig().contains("data.charged")) {
                        ConfigurationSection cs = plugin.dataFile.getConfig().getConfigurationSection("data.charged");

                        for (String key : cs.getKeys(false)) {
                            if (plugin.dataFile.getConfig().contains("data.charged." + key + ".nickname")) {
                                if (plugin.dataFile.getConfig().getString("data.charged." + key + ".nickname").equalsIgnoreCase(e.getMessage())) {
                                    match = true;
                                }
                            }
                        }
                    }
                    if (match) {
                        e.getPlayer().sendMessage(plugin.references.getPrefix() + Lang.NICKNAME_MATCH.getConfigValue());
                    } else {
                        plugin.dataFile.getConfig().set("data.charged." + Arconix.pl().getApi().serialize().serializeLocation(loc) + ".nickname", e.getMessage());

                        List<String> list = new ArrayList<>();
                        list.add(e.getPlayer().getUniqueId().toString() + ":" + e.getPlayer().getName());
                        plugin.dataFile.getConfig().set("data.charged." + Arconix.pl().getApi().serialize().serializeLocation(loc) + ".remoteAccessList", list);

                        e.getPlayer().sendMessage(plugin.references.getPrefix() + Lang.NICKNAME_SUCCESS.getConfigValue());
                    }


                }
            }
        } catch (Exception ee) {
            Debugger.runReport(ee);
        }
    }
}