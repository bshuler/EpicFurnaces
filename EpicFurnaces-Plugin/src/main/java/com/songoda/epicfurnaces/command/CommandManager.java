package com.songoda.epicfurnaces.command;

import com.songoda.arconix.api.methods.formatting.TextComponent;
import com.songoda.epicfurnaces.EpicFurnacesPlugin;
import com.songoda.epicfurnaces.command.commands.CommandEpicFurnaces;
import com.songoda.epicfurnaces.command.commands.CommandReload;
import com.songoda.epicfurnaces.command.commands.CommandRemote;
import com.songoda.epicfurnaces.command.commands.CommandSettings;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommandManager implements CommandExecutor {

    private EpicFurnacesPlugin instance;

    private List<AbstractCommand> commands = new ArrayList<>();

    public CommandManager(EpicFurnacesPlugin instance) {
        this.instance = instance;

        instance.getCommand("EpicFurnaces").setExecutor(this);

        AbstractCommand commandEpicFurnaces = addCommand(new CommandEpicFurnaces());

        addCommand(new CommandReload(commandEpicFurnaces));
        addCommand(new CommandRemote(commandEpicFurnaces));
        addCommand(new CommandSettings(commandEpicFurnaces));
    }

    private AbstractCommand addCommand(AbstractCommand abstractCommand) {
        commands.add(abstractCommand);
        return abstractCommand;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        for (AbstractCommand abstractCommand : commands) {
            if (abstractCommand.getCommand().equalsIgnoreCase(command.getName())) {
                if (strings.length == 0) {
                    processRequirements(abstractCommand, commandSender, strings);
                    return true;
                }
            } else if (strings.length != 0 && abstractCommand.getParent() != null && abstractCommand.getParent().getCommand().equalsIgnoreCase(command.getName())) {
                String cmd = strings[0];
                if (cmd.equalsIgnoreCase(abstractCommand.getCommand())) {
                    processRequirements(abstractCommand, commandSender, strings);
                    return true;
                }
            }
        }
        commandSender.sendMessage(instance.references.getPrefix() + TextComponent.formatText("&7The command you entered does not exist or is spelt incorrectly."));
        return true;
    }

    private void processRequirements(AbstractCommand command, CommandSender sender, String[] strings) {
        if (!(sender instanceof Player) && command.isNoConsole()) {
            sender.sendMessage("You must be a player to use this command.");
            return;
        }
        if (command.getPermissionNode() == null || sender.hasPermission(command.getPermissionNode())) {
             AbstractCommand.ReturnType returnType = command.runCommand(instance, sender, strings);
             if (returnType == AbstractCommand.ReturnType.SYNTAX_ERROR) {
                 sender.sendMessage(instance.references.getPrefix() + TextComponent.formatText("&cInvalid Syntax!"));
                 sender.sendMessage(instance.references.getPrefix() + TextComponent.formatText("&7The valid syntax is: &6" + command.getSyntax() + "&7."));
             }
            return;
        }
        sender.sendMessage(instance.references.getPrefix() + instance.getLocale().getMessage("event.general.nopermission"));
    }

    public List<AbstractCommand> getCommands() {
        return Collections.unmodifiableList(commands);
    }
}
