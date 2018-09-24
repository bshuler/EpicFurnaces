package com.songoda.epicfurnaces.command.commands;

import com.songoda.epicfurnaces.EpicFurnacesPlugin;
import com.songoda.epicfurnaces.command.AbstractCommand;
import com.songoda.epicfurnaces.utils.SettingsManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandSettings extends AbstractCommand {

    public CommandSettings(AbstractCommand parent) {
        super("settings", parent, true);
    }

    @Override
    protected ReturnType runCommand(EpicFurnacesPlugin instance, CommandSender sender, String... args) {
        SettingsManager.openEditor((Player)sender);
        return ReturnType.SUCCESS;
    }

    @Override
    public String getPermissionNode() {
        return "epicspawners.admin";
    }

    @Override
    public String getSyntax() {
        return "/es settings";
    }

    @Override
    public String getDescription() {
        return "Edit the EpicSpawners Settings.";
    }
}
