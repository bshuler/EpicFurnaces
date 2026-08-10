package com.songoda.epicfurnaces.utils;

import com.songoda.epicfurnaces.PluginTestSupport;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DebuggerTest {

    private static final String DEBUG_KEY = "System.Debugger Enabled";

    @BeforeAll
    static void loadPlugin() {
        PluginTestSupport.plugin();
    }

    @AfterEach
    void resetDebugFlag() {
        PluginTestSupport.plugin().getConfig().set(DEBUG_KEY, false);
    }

    @Test
    void isDebugReflectsTheConfiguredFlagAndDefaultsToFalse() {
        assertFalse(Debugger.isDebug());

        PluginTestSupport.plugin().getConfig().set(DEBUG_KEY, true);

        assertTrue(Debugger.isDebug());
    }

    @Test
    void runReportPrintsNothingWhenDebugIsDisabled() {
        PluginTestSupport.plugin().getConfig().set(DEBUG_KEY, false);
        String output = captureStdOut(() -> Debugger.runReport(new Exception("should not be printed")));

        assertTrue(output.isEmpty());
    }

    @Test
    void runReportPrintsAHeaderAndTheStackTraceWhenDebugIsEnabled() {
        PluginTestSupport.plugin().getConfig().set(DEBUG_KEY, true);

        PrintStream originalOut = System.out;
        PrintStream originalErr = System.err;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayOutputStream err = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        System.setErr(new PrintStream(err));
        try {
            Debugger.runReport(new Exception("boom"));
        } finally {
            System.setOut(originalOut);
            System.setErr(originalErr);
        }

        // The header lines go to System.out; Throwable#printStackTrace()
        // (called directly on the exception, not redirected) writes to
        // System.err by default.
        assertTrue(out.toString().contains("error encountered in EpicFurnaces"));
        assertTrue(err.toString().contains("boom"));
    }

    @Test
    void sendReportIsANoOpAndDoesNotThrow() {
        Debugger.sendReport(new Exception("ignored"));
    }

    @Test
    void debuggerCanBeInstantiatedEvenThoughEveryMemberIsStatic() {
        // No explicit constructor is declared, so javac generates a public
        // no-arg one; exercise it directly so it isn't permanently dead code.
        assertNotNull(new Debugger());
    }

    private static String captureStdOut(Runnable action) {
        PrintStream original = System.out;
        ByteArrayOutputStream capture = new ByteArrayOutputStream();
        System.setOut(new PrintStream(capture));
        try {
            action.run();
        } finally {
            System.setOut(original);
        }
        return capture.toString();
    }
}
