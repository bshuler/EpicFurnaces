package com.songoda.epicfurnaces.Handlers;

import com.songoda.arconix.plugin.Arconix;
import com.songoda.epicfurnaces.EpicFurnaces;
import com.songoda.epicfurnaces.Furnace.Furnace;
import com.songoda.epicfurnaces.Utils.Debugger;
import com.songoda.epicfurnaces.Utils.SettingsManager;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Created by songoda on 2/26/2017.
 */
public class CommandHandler implements CommandExecutor {

    private final EpicFurnaces instance;

    public CommandHandler(final EpicFurnaces instance) {
        this.instance = instance;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        try {
            if (cmd.getName().equalsIgnoreCase("EpicFurnaces")) {
                if (args.length == 0 || args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?")) {
                    sender.sendMessage("");
                    sender.sendMessage(Arconix.pl().getApi().format().formatText(instance.references.getPrefix() + "&7" + instance.getDescription().getVersion() + " Created by &5&l&oBrianna"));
                    sender.sendMessage(Arconix.pl().getApi().format().formatText(" &8- &aEF help &7Displays this page."));
                    sender.sendMessage(Arconix.pl().getApi().format().formatText(" &8- &aEF remote [nickname] &7Remote your furnace."));
                    sender.sendMessage(Arconix.pl().getApi().format().formatText(" &8- &aEF settings &7Edit the EpicFurnaces Settings."));
                    sender.sendMessage(Arconix.pl().getApi().format().formatText(" &8- &aEF reload &7Reloads Configuration and Language files."));
                    sender.sendMessage("");
                } else if (args[0].equalsIgnoreCase("reload")) {
                    if (!sender.isOp()) {
                        sender.sendMessage(instance.references.getPrefix() + instance.getLocale().getMessage("event.general.nopermission"));
                    } else {
                        instance.reload();
                        sender.sendMessage(Arconix.pl().getApi().format().formatText(instance.references.getPrefix() + "&8Configuration and Language files reloaded."));
                    }
                } else if (!(sender instanceof Player)) {
                    return true;
                }
                if (args[0].equalsIgnoreCase("Remote")) {
                    if (!instance.getConfig().getBoolean("settings.Remote-Furnaces") || !sender.hasPermission("EpicFurnaces.Remote")) {
                        sender.sendMessage(instance.references.getPrefix() + instance.getLocale().getMessage("event.general.nopermission"));
                        return true;
                    }
                    if (!instance.dataFile.getConfig().contains("data.charged")) {
                        return true;
                    }
                    if (args.length < 2) {
                        sender.sendMessage(instance.references.getPrefix() + Arconix.pl().getApi().format().formatText("&cInvalid Syntax."));
                    }
                    StringBuilder name = new StringBuilder();
                    for (int i = 1; i < args.length; i++) {
                        name.append(" ").append(args[i]);
                    }
                    name = new StringBuilder(name.toString().trim());
                    for (Furnace furnace : instance.getFurnaceManager().getFurnaces().values()) {
                        if (furnace.getNickname() == null) {
                            continue;
                        }
                        if (!furnace.getNickname().equalsIgnoreCase(name.toString())) {
                            sender.sendMessage(instance.references.getPrefix() + instance.getLocale().getMessage("event.general.nopermission"));
                            continue;
                        }
                        for (String line : furnace.getAccessList()) {
                            String[] halfs = line.split(":");
                            if (!UUID.fromString(halfs[0]).equals(((Player) sender).getUniqueId())) {
                                continue;
                            }
                            Block b = furnace.getLocation().getBlock();
                            org.bukkit.block.Furnace furnaceBlock = (org.bukkit.block.Furnace) b.getState();
                            ((Player) sender).openInventory(furnaceBlock.getInventory());
                            return true;
                        }

                    }
                    sender.sendMessage(instance.references.getPrefix() + instance.getLocale().getMessage("event.remote.notfound"));

                } else if (args[0].equalsIgnoreCase("settings")) {
                    if (!sender.hasPermission("epicfurnaces.admin")) {
                        sender.sendMessage(instance.references.getPrefix() + instance.getLocale().getMessage("event.general.nopermission"));
                    } else {
                        Player p = (Player) sender;
                        SettingsManager.openEditor(p);
                    }
                }
            }

        } catch (Exception e) {
            Debugger.runReport(e);
        }
        return true;
    }
}