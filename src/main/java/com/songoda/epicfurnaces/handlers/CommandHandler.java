package com.songoda.epicfurnaces.handlers;

import com.songoda.arconix.plugin.Arconix;
import com.songoda.epicfurnaces.EpicFurnaces;
import com.songoda.epicfurnaces.Lang;
import com.songoda.epicfurnaces.References;
import com.songoda.epicfurnaces.utils.Debugger;
import com.songoda.epicfurnaces.utils.SettingsManager;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

/**
 * Created by songoda on 2/26/2017.
 */
public class CommandHandler implements CommandExecutor {

    private final EpicFurnaces plugin;

    public CommandHandler(final EpicFurnaces plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("unchecked")
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        try {
            if (cmd.getName().equalsIgnoreCase("EpicFurnaces")) {
                if (args.length == 0 || args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?")) {
                    sender.sendMessage("");
                    sender.sendMessage(Arconix.pl().getApi().format().formatText(plugin.references.getPrefix() + "&7" + plugin.getDescription().getVersion() + " Created by &5&l&oBrianna"));
                    sender.sendMessage(Arconix.pl().getApi().format().formatText(" &8- &aEF help &7Displays this page."));
                    sender.sendMessage(Arconix.pl().getApi().format().formatText(" &8- &aEF remote [nickname] &7Remote your furnace."));
                    sender.sendMessage(Arconix.pl().getApi().format().formatText(" &8- &aEF settings &7Edit the EpicFurnaces Settings."));
                    sender.sendMessage(Arconix.pl().getApi().format().formatText(" &8- &aEF reload &7Reloads Configuration and Language files."));
                    sender.sendMessage("");
                } else if (args[0].equalsIgnoreCase("reload")) {
                    if (!sender.isOp()) {
                        sender.sendMessage(plugin.references.getPrefix() + Lang.NO_PERMS.getConfigValue());
                    } else {
                        plugin.langFile.createNewFile("Loading language file", "EpicFurnaces language file");
                        plugin.hooks.hooksFile.createNewFile("Loading Hooks file", "EpicFurnaces hooks file");
                        plugin.loadLanguageFile();
                        plugin.references = new References();
                        plugin.reloadConfig();
                        plugin.saveConfig();
                        sender.sendMessage(Arconix.pl().getApi().format().formatText(plugin.references.getPrefix() + "&8Configuration and Language files reloaded."));
                    }
                } else if (sender instanceof Player) {
                    if (args[0].equalsIgnoreCase("Remote")) {
                        if (plugin.getConfig().getBoolean("settings.Remote-Furnaces") && sender.hasPermission("EpicFurnaces.Remote")) {
                            if (plugin.dataFile.getConfig().contains("data.charged")) {
                                if (args.length >= 2) {
                                    StringBuilder name = new StringBuilder();
                                    for (int i = 1; i < args.length; i++) {
                                        name.append(" ").append(args[i]);
                                    }
                                    name = new StringBuilder(name.toString().trim());
                                    ConfigurationSection cs = plugin.dataFile.getConfig().getConfigurationSection("data.charged");
                                    for (String key : cs.getKeys(false)) {
                                        if (plugin.dataFile.getConfig().contains("data.charged." + key + ".nickname")) {
                                            if (plugin.dataFile.getConfig().getString("data.charged." + key + ".nickname").equalsIgnoreCase(name.toString())) {
                                                if (plugin.dataFile.getConfig().contains("data.charged." + key + ".remoteAccessList")) {
                                                    for (String line : (List<String>) plugin.dataFile.getConfig().getList("data.charged." + key + ".remoteAccessList")) {
                                                        String[] halfs = line.split(":");
                                                        if (UUID.fromString(halfs[0]).equals(((Player) sender).getUniqueId())) {
                                                            Block b = Arconix.pl().getApi().serialize().unserializeLocation(key).getBlock();
                                                            org.bukkit.block.Furnace furnace = (org.bukkit.block.Furnace) b.getState();
                                                            ((Player) sender).openInventory(furnace.getInventory());
                                                            return true;
                                                        }
                                                    }
                                                }
                                                sender.sendMessage(plugin.references.getPrefix() + Lang.NO_PERMS.getConfigValue());
                                                return true;
                                            }
                                        }
                                    }
                                    sender.sendMessage(plugin.references.getPrefix() + Lang.REMOTE_NOT_FOUND.getConfigValue());
                                } else {
                                    sender.sendMessage(plugin.references.getPrefix() + Arconix.pl().getApi().format().formatText("&cInvalid Syntax."));
                                }
                            }
                        } else {
                            sender.sendMessage(plugin.references.getPrefix() + Lang.NO_PERMS.getConfigValue());
                        }
                    } else if (args[0].equalsIgnoreCase("settings")) {
                        if (!sender.hasPermission("epicfurnaces.admin")) {
                            sender.sendMessage(plugin.references.getPrefix() + Lang.NO_PERMS.getConfigValue());
                        } else {
                            Player p = (Player) sender;
                            SettingsManager.openEditor(p);
                        }
                    }
                }
            }
        } catch (Exception e) {
            Debugger.runReport(e);
        }
        return true;
    }
}
