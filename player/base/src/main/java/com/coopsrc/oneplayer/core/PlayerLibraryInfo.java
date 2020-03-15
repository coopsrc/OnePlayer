package com.coopsrc.oneplayer.core;

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

    public static final String VERSION = "1.0.0";

    public static final String VERSION_SLASHY = "OnePlayerLib/"+VERSION;

    public static final int VERSION_INT = 1000000;

    public static final boolean ASSERTIONS_ENABLED = true;

    public static final boolean TRACE_ENABLED = true;

    private static final HashSet<String> registeredModules = new HashSet<>();

    private static String registeredModulesString = "one.player.core";

    /**
     * Returns a string consisting of registered module names separated by ", ".
     */
    public static synchronized String registeredModules() {
        return registeredModulesString;
    }

    public static synchronized void registerModule(String name) {
        if (registeredModules.add(name)) {
            registeredModulesString = registeredModulesString + ", " + name;
        }
    }
}
