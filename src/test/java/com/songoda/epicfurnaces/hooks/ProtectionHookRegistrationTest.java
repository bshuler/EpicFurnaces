package com.songoda.epicfurnaces.hooks;

import com.songoda.epicfurnaces.EpicFurnacesPlugin;
import com.songoda.epicfurnaces.PluginTestSupport;
import org.bukkit.plugin.PluginManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Guard-logic coverage for the 6 revived protection-plugin hooks
 * ({@code HookWorldGuard}, {@code HookGriefPrevention}, {@code
 * HookRedProtect}, {@code HookASkyBlock}, {@code HookTowny}, {@code
 * HookPlotSquared}). Each is registered in {@code
 * EpicFurnacesPlugin#onEnable()} only behind a {@code
 * pluginManager.getPlugin("...") != null} guard - see plugin.yml's {@code
 * softdepend} list and PLAN.md ("Per-repo extras" / hooks) for the exact
 * name string each guard checks.
 *
 * <p>What is deliberately NOT tested here: each hook's actual {@code
 * canBuild(...)} decision logic (the real WorldGuard/GriefPrevention/
 * RedProtect/ASkyBlock/Towny/PlotSquared API calls). Every one of those
 * calls a real third-party plugin singleton
 * ({@code WorldGuard.getInstance()}, {@code GriefPrevention.instance},
 * {@code RedProtect.get()}, {@code ASkyBlockAPI.getInstance()}, {@code
 * TownyAPI.getInstance()}, or PlotSquared's platform-bound {@code
 * BukkitUtil.adapt(...)}) that MockBukkit has no way to simulate - there is
 * no fake WorldGuard/Towny/etc. server to register. Forcing coverage there
 * would require reflection or static-mocking hacks, which this project's
 * testing standard already rules out (see the identical, already-documented
 * exception for {@code Methods.java}'s defensive catch blocks in PLAN.md
 * "Test coverage"). What IS genuinely testable, and what actually matters
 * for correctness - a plugin.yml softdepend that is absent must never crash
 * onEnable() or get silently registered anyway - is exercised below.
 *
 * <p>{@code EpicFurnacesPlugin} is loaded exactly once per test JVM by
 * {@link PluginTestSupport} and every other test file in this suite also
 * goes through that same {@code onEnable()} call, so this test is also a
 * real regression guard: if a future edit ever dropped one of the six
 * {@code getPlugin(...) != null} checks in {@code onEnable()}, the very
 * first hook constructor to run (e.g. {@code new HookWorldGuard()}, which
 * immediately calls {@code WorldGuard.getInstance()}) would throw a {@code
 * NoClassDefFoundError} - since none of these six libraries are on the test
 * classpath at all (they are {@code compileOnly}, deliberately not
 * duplicated into {@code testImplementation} the way VaultAPI is, because
 * unlike VaultAPI there is no way to meaningfully stub a WorldGuard/Towny/
 * etc. server) - and every single test in this module would fail at
 * {@code @BeforeAll}, not just this class.
 */
class ProtectionHookRegistrationTest {

    @ParameterizedTest
    @ValueSource(strings = {"WorldGuard", "GriefPrevention", "RedProtect", "ASkyBlock", "Towny", "PlotSquared"})
    void hookedPluginIsAbsentInTheTestServer(String pluginName) {
        // Sanity check on the premise the rest of this test relies on: none
        // of these plugins are registered in the MockBukkit test server, so
        // every onEnable() guard for them evaluated false and no Hook*
        // constructor ran.
        PluginManager pluginManager = PluginTestSupport.server().getPluginManager();
        assertNull(pluginManager.getPlugin(pluginName));
    }

    @Test
    void pluginEnablesAndCanBuildStaysPermissiveWithNoProtectionPluginsPresent() {
        // If onEnable() had registered a hook anyway despite its target
        // plugin being absent, canBuild() would delegate to a hook backed
        // by a null/uninitialized third-party singleton and either throw or
        // (worse) silently deny every build. Neither happens: with no
        // protection plugins present, canBuild() has nothing to consult and
        // is permissive.
        EpicFurnacesPlugin plugin = PluginTestSupport.plugin();

        assertTrue(plugin.canBuild(PluginTestSupport.server().addPlayer(), PluginTestSupport.world().getBlockAt(0, 64, 0).getLocation()));
    }
}
