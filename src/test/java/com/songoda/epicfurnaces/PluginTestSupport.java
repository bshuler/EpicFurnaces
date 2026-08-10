package com.songoda.epicfurnaces;

import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

/**
 * Shared MockBukkit fixture, loaded exactly once per test JVM rather than
 * per test method or per test class.
 *
 * <p>This is required, not just an optimization: {@link Locale} keeps its
 * {@code plugin}/{@code localeFolder} fields as {@code static}, and {@link
 * Locale#init(org.bukkit.plugin.java.JavaPlugin)} only assigns {@code
 * localeFolder} when it is still {@code null}. If MockBukkit loaded a fresh
 * {@link EpicFurnacesPlugin} (with a fresh temporary data folder) per test,
 * every test after the first would silently keep pointing {@code Locale} at
 * the FIRST test's now-defunct data folder. That is a real, pre-existing bug
 * in {@code Locale}'s static state, not a MockBukkit quirk - see PLAN.md
 * ("Test coverage" / bugs found). Loading the plugin once for the whole test
 * run sidesteps it entirely rather than papering over it with per-test
 * resets that would just be masking the same defect.
 */
public final class PluginTestSupport {

    private static ServerMock server;
    private static EpicFurnacesPlugin plugin;

    private PluginTestSupport() {
    }

    public static synchronized EpicFurnacesPlugin plugin() {
        if (plugin == null) {
            server = MockBukkit.mock();
            plugin = MockBukkit.load(EpicFurnacesPlugin.class);
        }
        return plugin;
    }

    public static synchronized ServerMock server() {
        plugin();
        return server;
    }
}
