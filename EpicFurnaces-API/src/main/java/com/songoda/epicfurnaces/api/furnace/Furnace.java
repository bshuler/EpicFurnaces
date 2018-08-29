package com.songoda.epicfurnaces.api.furnace;

import org.bukkit.Location;

import java.util.List;

public interface Furnace {
    Level getLevel();

    List<String> getAccessList();

    boolean addToAccessList(String string);

    boolean removeFromAccessList(String string);

    void clearAccessList();

    Location getLocation();

    void setNickname(String nickname);

    String getNickname();

    int getUses();

    int getTolevel();
}
