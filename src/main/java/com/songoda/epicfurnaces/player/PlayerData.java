package com.songoda.epicfurnaces.player;

import com.songoda.epicfurnaces.furnace.Furnace;

import java.util.*;

public class PlayerData {

    private final UUID playerUUID;

    private Furnace lastFurace = null;

    private boolean inOverview = false;

    private boolean isSettingNickname = false;

    public PlayerData(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public Furnace getLastFurace() {
        return lastFurace;
    }

    public void setLastFurace(Furnace lastFurace) {
        this.lastFurace = lastFurace;
    }

    public boolean isInOverview() {
        return inOverview;
    }

    public void setInOverview(boolean inOverview) {
        this.inOverview = inOverview;
    }

    public boolean isSettingNickname() {
        return isSettingNickname;
    }

    public void setSettingNickname(boolean settingNickname) {
        isSettingNickname = settingNickname;
    }
}
