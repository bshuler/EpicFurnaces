package com.songoda.epicfurnaces.api;

import com.songoda.epicfurnaces.PluginTestSupport;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * {@code EpicFurnacesAPI.implementation} is a process-wide static field
 * that can only ever be set once (by design - see the class javadoc), so
 * both the "sets it" and "rejects a second set" behaviors are exercised
 * together in a single test to avoid depending on JUnit test-method
 * execution order for correctness.
 */
class EpicFurnacesAPITest {

    @BeforeAll
    static void loadPlugin() {
        PluginTestSupport.plugin();
    }

    @Test
    void epicFurnacesAPICanBeInstantiated() {
        // No explicit constructor is declared, so javac generates a public
        // no-arg one; exercise it directly so it isn't permanently dead code.
        assertNotNull(new EpicFurnacesAPI());
    }

    @Test
    void setImplementationStoresItAndRejectsBeingSetASecondTime() {
        EpicFurnaces implementation = PluginTestSupport.plugin();

        EpicFurnacesAPI.setImplementation(implementation);

        assertSame(implementation, EpicFurnacesAPI.getImplementation());
        assertThrows(IllegalArgumentException.class, () -> EpicFurnacesAPI.setImplementation(implementation));
    }
}
