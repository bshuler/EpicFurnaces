package com.songoda.epicfurnaces;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReferencesTest {

    @BeforeAll
    static void loadPlugin() {
        PluginTestSupport.plugin();
    }

    @Test
    void prefixIsTheLocaleMessagePlusATrailingSpace() {
        String expectedMessage = PluginTestSupport.plugin().getLocale().getMessage("general.nametag.prefix");

        References references = new References();

        assertEquals(expectedMessage + " ", references.getPrefix());
    }
}
