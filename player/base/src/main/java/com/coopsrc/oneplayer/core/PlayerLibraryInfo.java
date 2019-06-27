package com.coopsrc.oneplayer.core;

import com.coopsrc.oneplayer.core.utils.Assertions;
import com.coopsrc.oneplayer.core.utils.PlayerLogger;
import com.coopsrc.oneplayer.core.utils.TraceUtil;

import java.util.HashSet;

/**
 * @author tingkuo
 * <p>
 * Date: 2019-06-27 16:28
 */
public final class PlayerLibraryInfo {

    private PlayerLibraryInfo() {
    }

    public static final String TAG = "OnePlayer";

    /**
     * The version of the library expressed as a string, for example "1.2.3".
     */
    // Intentionally hardcoded. Do not derive from other constants (e.g. VERSION_INT) or vice versa.
    public static final String VERSION = "1.0.0";

    /**
     * The version of the library expressed as {@code "OnePlayerLib/" + VERSION}.
     */
    // Intentionally hardcoded. Do not derive from other constants (e.g. VERSION) or vice versa.
    public static final String VERSION_SLASHY = "OnePlayerLib/1.0.0";

    /**
     * The version of the library expressed as an integer, for example 1002003.
     *
     * <p>Three digits are used for each component of {@link #VERSION}. For example "1.2.3" has the
     * corresponding integer version 1002003 (001-002-003), and "123.45.6" has the corresponding
     * integer version 123045006 (123-045-006).
     */
    // Intentionally hardcoded. Do not derive from other constants (e.g. VERSION) or vice versa.
    public static final int VERSION_INT = 1000000;

    /**
     * Whether the library was compiled with {@link Assertions}
     * checks enabled.
     */
    public static final boolean ASSERTIONS_ENABLED = true;


    /**
     * Whether the library was compiled with {@link TraceUtil}
     * trace enabled.
     */
    public static final boolean TRACE_ENABLED = true;

    private static final HashSet<String> registeredModules = new HashSet<>();

    private static String registeredModulesString = "one.player.core";

    /**
     * Returns a string consisting of registered module names separated by ", ".
     */
    public static synchronized String registeredModules() {
        return registeredModulesString;
    }

    /**
     * Registers a module to be returned in the {@link #registeredModules()} string.
     *
     * @param name The name of the module being registered.
     */
    public static synchronized void registerModule(String name) {
        if (registeredModules.add(name)) {
            registeredModulesString = registeredModulesString + ", " + name;
        }
    }
}
