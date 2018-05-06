package com.songoda.epicfurnaces;

public class References {

    private String prefix;

    public References() {
        prefix = Lang.PREFIX.getConfigValue(null, null) + " ";
    }

    public String getPrefix() {
        return this.prefix;
    }
}
