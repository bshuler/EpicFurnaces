package com.songoda.epicfurnaces.Events;

import com.songoda.arconix.plugin.Arconix;
import com.songoda.epicfurnaces.EpicFurnaces;
import com.songoda.epicfurnaces.Furnace.Furnace;
import com.songoda.epicfurnaces.Utils.Debugger;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by songoda on 2/25/2017.
 */
public class ChatListeners implements Listener {

    private final EpicFurnaces instance;

    public ChatListeners(EpicFurnaces instance) {
        this.instance = instance;
    }

    @EventHandler
    public void chatListeners(AsyncPlayerChatEvent e) {
        try {
            if (!e.isCancelled()) {
                if (instance.nicknameQ.containsKey(e.getPlayer())) {
                    e.setCancelled(true);

                    Location loc = instance.nicknameQ.get(e.getPlayer());
                    instance.nicknameQ.remove(e.getPlayer());

                    for (Furnace furnace : instance.getFurnaceManager().getFurnaces().values()) {
                        if (furnace.getNickname() == null) continue;
                        if (furnace.getNickname().equalsIgnoreCase(e.getMessage())) {
                            e.getPlayer().sendMessage(instance.references.getPrefix() + instance.getLocale().getMessage("event.remote.nicknameinuse"));
                            return;
                        }
                    }

                    Furnace furnace = instance.getFurnaceManager().getFurnace(loc);

                    furnace.setNickname(e.getMessage());

                    List<String> list = new ArrayList<>();

                    furnace.clearAccessList();
                    furnace.addToAccessList(e.getPlayer().getUniqueId().toString() + ":" + e.getPlayer().getName());
                    instance.dataFile.getConfig().set("data.charged." + Arconix.pl().getApi().serialize().serializeLocation(loc) + ".remoteAccessList", list);

                    e.getPlayer().sendMessage(instance.references.getPrefix() + instance.getLocale().getMessage("event.remote.nicknamesuccess"));

                }
            }

        } catch (Exception ee) {
            Debugger.runReport(ee);
        }
    }
}