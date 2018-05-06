package com.songoda.epicfurnaces.listeners;

import com.songoda.arconix.plugin.Arconix;
import com.songoda.epicfurnaces.EpicFurnaces;
import com.songoda.epicfurnaces.utils.Debugger;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Created by songo on 6/26/2017.
 */
public class LoginListeners implements Listener {

    private EpicFurnaces plugin = EpicFurnaces.pl();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        try {
            Player p = e.getPlayer();
            if (p.isOp() && plugin.getConfig().getBoolean("settings.Helpful-Tips")) {
                if (plugin.getServer().getPluginManager().getPlugin("Factions") != null && plugin.hooks.FactionsHook == null) {
                    p.sendMessage("");
                    p.sendMessage(Arconix.pl().getApi().format().formatText(plugin.references.getPrefix() + "&7Heres the deal,"));
                    p.sendMessage(Arconix.pl().getApi().format().formatText("&7Because you're not using the offical versions of &6Factions"));
                    p.sendMessage(Arconix.pl().getApi().format().formatText("&7I cannot give you full support out of the box."));
                    p.sendMessage(Arconix.pl().getApi().format().formatText("&7Things will work without it but if you wan't a flawless"));
                    p.sendMessage(Arconix.pl().getApi().format().formatText("&7experience you need to download"));
                    p.sendMessage(Arconix.pl().getApi().format().formatText("&7&6https://www.spigotmc.org/resources/54337/&7."));
                    p.sendMessage(Arconix.pl().getApi().format().formatText("&7turn &6Helpful-Tips &7off in the config."));
                    p.sendMessage("");
                }
            }
        } catch (Exception ee) {
            Debugger.runReport(ee);
        }
    }
}