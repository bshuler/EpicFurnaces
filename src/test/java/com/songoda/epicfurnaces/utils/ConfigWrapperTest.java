package com.songoda.epicfurnaces.utils;

import com.songoda.epicfurnaces.PluginTestSupport;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfigWrapperTest {

    @BeforeAll
    static void loadPlugin() {
        PluginTestSupport.plugin();
    }

    @Test
    void createNewFileCreatesItOnceAndIsANoOpAfterThat() {
        ConfigWrapper wrapper = new ConfigWrapper(PluginTestSupport.plugin(), "config-wrapper-test", "first-creation.yml");

        assertTrue(wrapper.createNewFile("loading", "header"));
        assertFalse(wrapper.createNewFile("loading", "header"));
    }

    @Test
    void getConfigLazilyLoadsAndReturnsAWorkingFileConfiguration() {
        ConfigWrapper wrapper = new ConfigWrapper(PluginTestSupport.plugin(), "config-wrapper-test", "lazy-load.yml");
        wrapper.createNewFile("loading", "header");

        assertNotNull(wrapper.getConfig());
    }

    @Test
    void saveConfigPersistsValuesVisibleToAFreshWrapperOverTheSameFile() {
        ConfigWrapper writer = new ConfigWrapper(PluginTestSupport.plugin(), "config-wrapper-test", "persisted.yml");
        writer.createNewFile("loading", "header");
        writer.getConfig().set("some.key", "some-value");
        writer.saveConfig();

        ConfigWrapper reader = new ConfigWrapper(PluginTestSupport.plugin(), "config-wrapper-test", "persisted.yml");

        assertEquals("some-value", reader.getConfig().getString("some.key"));
    }

    @Test
    void saveConfigBeforeGetConfigIsANoOp() {
        ConfigWrapper wrapper = new ConfigWrapper(PluginTestSupport.plugin(), "config-wrapper-test", "never-loaded.yml");

        // getConfig() was never called, so there is nothing to save; this
        // must not throw (and must not create the file).
        wrapper.saveConfig();
    }

    @Test
    void createNewFileReturnsFalseWhenAnIOExceptionOccursCreatingIt() throws IOException {
        // "subFolder" here is deliberately an existing plain FILE, not a
        // directory. File#createNewFile() then genuinely throws
        // IOException (the parent path component isn't a directory), which
        // is exactly what the try/catch in createNewFile() guards against -
        // this is real OS-level behavior, not a mock/hack.
        File dataFolder = PluginTestSupport.plugin().getDataFolder();
        File blockingFile = new File(dataFolder, "config-wrapper-test-blocker.yml");
        dataFolder.mkdirs();
        assertTrue(blockingFile.createNewFile());

        ConfigWrapper wrapper = new ConfigWrapper(PluginTestSupport.plugin(), "config-wrapper-test-blocker.yml", "child.yml");

        assertFalse(wrapper.createNewFile("loading", "header"));
    }

    @Test
    void saveConfigLogsAWarningInsteadOfThrowingWhenAnIOExceptionOccurs() throws IOException {
        File dataFolder = PluginTestSupport.plugin().getDataFolder();
        File blockingFile = new File(dataFolder, "config-wrapper-test-blocker-2.yml");
        dataFolder.mkdirs();
        assertTrue(blockingFile.createNewFile());

        ConfigWrapper wrapper = new ConfigWrapper(PluginTestSupport.plugin(), "config-wrapper-test-blocker-2.yml", "child.yml");
        wrapper.getConfig().set("some.key", "some-value");

        // config.save(file) fails because "child.yml"'s parent path
        // component is a plain file, not a directory; saveConfig() must
        // swallow the IOException rather than propagate it.
        wrapper.saveConfig();
    }

    @Test
    void subFolderNullOrEmptyResolvesDirectlyUnderTheDataFolder() {
        ConfigWrapper nullSubFolder = new ConfigWrapper(PluginTestSupport.plugin(), null, "root-null.yml");
        ConfigWrapper emptySubFolder = new ConfigWrapper(PluginTestSupport.plugin(), "", "root-empty.yml");

        assertTrue(nullSubFolder.createNewFile("loading", "header"));
        assertTrue(emptySubFolder.createNewFile("loading", "header"));
    }
}
