package com.songoda.epicfurnaces.api.utils;

import com.songoda.epicfurnaces.PluginTestSupport;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Exercises the only executable code in this interface: the default
 * {@code canBuild(Player, Block)} method, which delegates to the abstract
 * {@code canBuild(Player, Location)} overload.
 */
class ProtectionPluginHookTest {

    @BeforeAll
    static void loadPlugin() {
        PluginTestSupport.plugin();
    }

    @Test
    void canBuildByBlockReturnsFalseForANullBlockWithoutCallingTheLocationOverload() {
        ProtectionPluginHook hook = new ProtectionPluginHook() {
            @Override
            public JavaPlugin getPlugin() {
                return PluginTestSupport.plugin();
            }

            @Override
            public boolean canBuild(Player player, Location location) {
                throw new AssertionError("Should not be reached for a null block");
            }
        };

        assertFalse(hook.canBuild(PluginTestSupport.server().addPlayer(), (Block) null));
    }

    @Test
    void canBuildByBlockDelegatesToTheLocationOverloadForANonNullBlock() {
        ProtectionPluginHook hook = new ProtectionPluginHook() {
            @Override
            public JavaPlugin getPlugin() {
                return PluginTestSupport.plugin();
            }

            @Override
            public boolean canBuild(Player player, Location location) {
                return true;
            }
        };

        Block block = PluginTestSupport.world().getBlockAt(0, 64, 0);

        assertTrue(hook.canBuild(PluginTestSupport.server().addPlayer(), block));
    }
}
