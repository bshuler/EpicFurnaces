package com.songoda.epicfurnaces.events;

import com.songoda.arconix.plugin.Arconix;
import com.songoda.epicfurnaces.EpicFurnaces;
import com.songoda.epicfurnaces.furnace.Furnace;
import com.songoda.epicfurnaces.player.PlayerData;
import com.songoda.epicfurnaces.utils.Debugger;
import org.bukkit.Location;
import org.bukkit.entity.Player;
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
            Player player = e.getPlayer();
            PlayerData playerData = instance.getPlayerDataManager().getPlayerData(player);

            if (!e.isCancelled()) {
                if (playerData.isSettingNickname()) {
                    e.setCancelled(true);

                    playerData.setSettingNickname(false);

                    for (Furnace furnace : instance.getFurnaceManager().getFurnaces().values()) {
                        if (furnace.getNickname() == null) continue;
                        if (furnace.getNickname().equalsIgnoreCase(e.getMessage())) {
                            player.sendMessage(instance.references.getPrefix() + instance.getLocale().getMessage("event.remote.nicknameinuse"));
                            return;
                        }
                    }

                    Furnace furnace = playerData.getLastFurace();

                    furnace.setNickname(e.getMessage());

                    List<String> list = new ArrayList<>();

                    furnace.clearAccessList();
                    furnace.addToAccessList(player.getUniqueId().toString() + ":" + player.getName());
                    instance.dataFile.getConfig().set("data.charged." + Arconix.pl().getApi().serialize().serializeLocation(furnace.getLocation()) + ".remoteAccessList", list);

                    player.sendMessage(instance.references.getPrefix() + instance.getLocale().getMessage("event.remote.nicknamesuccess"));

                }
            }

        } catch (Exception ee) {
            Debugger.runReport(ee);
        }
    }
}