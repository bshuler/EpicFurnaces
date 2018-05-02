package com.songoda.epicfurnaces.Events;

import com.songoda.arconix.method.formatting.TextComponent;
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

    private final EpicFurnaces instance;

    public LoginListeners(EpicFurnaces instance) {
        this.instance = instance;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        try {
            Player p = e.getPlayer();
            if (!p.isOp() || !instance.getConfig().getBoolean("settings.Helpful-Tips")
                    || instance.getServer().getPluginManager().getPlugin("Factions") == null
                    || instance.hooks.FactionsHook != null) {
                return;
            }
            p.sendMessage("");
            p.sendMessage(TextComponent.formatText(instance.references.getPrefix() + "&7Heres the deal,"));
            p.sendMessage(TextComponent.formatText("&7Because you're not using the offical versions of &6Factions"));
            p.sendMessage(TextComponent.formatText("&7I cannot give you full support out of the box."));
            p.sendMessage(TextComponent.formatText("&7Things will work without it but if you wan't a flawless"));
            p.sendMessage(TextComponent.formatText("&7experience you need to download"));
            p.sendMessage(TextComponent.formatText("&7&6https://www.spigotmc.org/resources/54337/&7."));
            p.sendMessage(TextComponent.formatText("&7turn &6Helpful-Tips &7off in the config."));
            p.sendMessage("");
        } catch (Exception ee) {
            Debugger.runReport(ee);
        }
    }
}
