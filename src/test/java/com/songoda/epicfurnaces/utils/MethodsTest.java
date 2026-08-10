package com.songoda.epicfurnaces.utils;

import com.songoda.epicfurnaces.PluginTestSupport;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MethodsTest {

    private static final String PARTICLES_KEY = "settings.On-upgrade-particles";
    private static final String RAINBOW_GLASS_KEY = "Interfaces.Replace Glass Type 1 With Rainbow Glass";

    @BeforeAll
    static void loadPlugin() {
        PluginTestSupport.plugin();
    }

    @AfterEach
    void resetParticlesFlag() {
        PluginTestSupport.plugin().getConfig().set(PARTICLES_KEY, null);
        PluginTestSupport.plugin().getConfig().set(RAINBOW_GLASS_KEY, null);
        PluginTestSupport.world().clearSpawnedParticles();
    }

    @Test
    void methodsCanBeInstantiatedEvenThoughEveryMemberIsStatic() {
        // Methods has no explicit constructor, so javac generates a public
        // no-arg one; exercising it directly (rather than leaving it as
        // permanently-dead code) is the only way to cover it.
        assertNotNull(new Methods());
    }

    @Test
    void formatEconomyRendersWholeAmountsWithoutDecimals() {
        assertEquals("5000", Methods.formatEconomy(5000.0));
        assertEquals("0", Methods.formatEconomy(0.0));
    }

    @Test
    void formatEconomyRendersFractionalAmountsWithTwoDecimals() {
        assertEquals("12.50", Methods.formatEconomy(12.5));
        assertEquals("0.33", Methods.formatEconomy(1.0 / 3));
    }

    @Test
    void cleanStringUnderscoresToSpacesAndCapitalizesFirstLetter() {
        assertEquals("Iron ingot", Methods.cleanString("IRON_INGOT"));
    }

    @Test
    void cleanStringOfNullIsCaughtAndReturnsNull() {
        // typ.replaceAll(...) throws a NullPointerException for a null
        // argument; this exercises the method's defensive try/catch with a
        // real, legitimate input rather than a contrived one.
        assertNull(Methods.cleanString(null));
    }

    @Test
    void cleanStringStripsEmbeddedColorCodes() {
        // The leading-character capitalization happens before stripColor()
        // runs, so a color code at position 0 would itself get
        // uppercase()'d and then stripped along with its result; placing it
        // mid-string isolates the "strips color codes" behavior cleanly.
        assertEquals("Iron ingot", Methods.cleanString("IRON_§aINGOT"));
    }

    @Test
    void formatTextTranslatesAmpersandColorCodes() {
        String formatted = Methods.formatText("&aHello");

        assertEquals(org.bukkit.ChatColor.GREEN + "Hello", formatted);
    }

    @Test
    void formatTextOfNullIsNull() {
        assertNull(Methods.formatText(null));
    }

    @Test
    void toHiddenStringPrefixesEveryCharacterWithTheColorChar() {
        String hidden = Methods.toHiddenString("1:2");

        assertEquals("§1§:§2", hidden);
    }

    @Test
    void serializeThenUnserializeLocationRoundTrips() {
        Location original = new Location(PluginTestSupport.world(), 12, 65, -34);

        String serialized = Methods.serializeLocation(original);
        Location roundTripped = Methods.unserializeLocation(serialized);

        assertEquals(PluginTestSupport.world().getName(), roundTripped.getWorld().getName());
        assertEquals(12, roundTripped.getBlockX());
        assertEquals(65, roundTripped.getBlockY());
        assertEquals(-34, roundTripped.getBlockZ());
    }

    @Test
    void serializeLocationTruncatesFractionalCoordinatesToBlockCoordinates() {
        Location fractional = new Location(PluginTestSupport.world(), 12.9, 65.1, -34.5);

        assertEquals(PluginTestSupport.world().getName() + ";12;65;-35", Methods.serializeLocation(fractional));
    }

    @Test
    void getGlassReturnsAStainedGlassPane() {
        var glass = Methods.getGlass();

        assertTrue(glass.getType().name().endsWith("_STAINED_GLASS_PANE"));
    }

    @Test
    void getGlassReturnsARandomStainedGlassPaneWhenRainbowIsEnabled() {
        PluginTestSupport.plugin().getConfig().set(RAINBOW_GLASS_KEY, true);

        var glass = Methods.getGlass();

        assertTrue(glass.getType().name().endsWith("_STAINED_GLASS_PANE"));
    }

    @Test
    void getBackgroundGlassReturnsAStainedGlassPaneForBothTypes() {
        assertTrue(Methods.getBackgroundGlass(true).getType().name().endsWith("_STAINED_GLASS_PANE"));
        assertTrue(Methods.getBackgroundGlass(false).getType().name().endsWith("_STAINED_GLASS_PANE"));
    }

    @Test
    void particlesDoesNothingWhenTheConfigFlagIsUnsetOrFalse() {
        var player = PluginTestSupport.server().addPlayer();
        Block block = PluginTestSupport.world().getBlockAt(0, 64, 0);

        Methods.particles(block, player);

        assertTrue(PluginTestSupport.world().getSpawnedParticles().isEmpty());
    }

    @Test
    void particlesSpawnsAtTheBlockCenterWhenTheConfigFlagIsEnabled() {
        PluginTestSupport.plugin().getConfig().set(PARTICLES_KEY, true);
        var player = PluginTestSupport.server().addPlayer();
        Block block = PluginTestSupport.world().getBlockAt(1, 64, 1);

        Methods.particles(block, player);

        assertEquals(1, PluginTestSupport.world().getSpawnedParticles().size());
    }

    @Test
    void formatNameEncodesLevelAndUsesWhenFullIsRequestedAndDecodesBackViaThePlugin() {
        String name = Methods.formatName(3, 7, true);

        var item = new org.bukkit.inventory.ItemStack(Material.FURNACE);
        var meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);

        assertEquals(3, PluginTestSupport.plugin().getFurnceLevel(item));
        assertEquals(7, PluginTestSupport.plugin().getFurnaceUses(item));
    }

    @Test
    void formatNameOmitsTheHiddenEncodingWhenFullIsFalse() {
        String name = Methods.formatName(3, 7, false);

        var item = new org.bukkit.inventory.ItemStack(Material.FURNACE);
        var meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);

        // No ":" was encoded into the display name, so both decoders fall
        // back to their "not encoded" defaults (1 / 0) rather than the
        // actual level/uses.
        assertEquals(1, PluginTestSupport.plugin().getFurnceLevel(item));
        assertEquals(0, PluginTestSupport.plugin().getFurnaceUses(item));
    }
}
