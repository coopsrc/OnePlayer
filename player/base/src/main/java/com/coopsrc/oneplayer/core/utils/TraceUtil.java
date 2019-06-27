package com.coopsrc.oneplayer.core.utils;

import com.coopsrc.oneplayer.core.PlayerLibraryInfo;

/**
 * @author tingkuo
 * <p>
 * Date: 2019-06-27 16:55
 */
public class TraceUtil {
    private TraceUtil() {
    }


    /**
     * Writes a trace message to indicate that a given section of code has begun.
     *
     * @param sectionName The name of the code section to appear in the trace. This may be at most 127
     *                    Unicode code units long.
     * @see android.os.Trace#beginSection(String)
     */
    public static void beginSection(String sectionName) {
        if (PlayerLibraryInfo.TRACE_ENABLED) {
            beginSectionV18(sectionName);
        }
    }

    /**
     * Writes a trace message to indicate that a given section of code has ended.
     *
     * @see android.os.Trace#endSection()
     */
    public static void endSection() {
        if (PlayerLibraryInfo.TRACE_ENABLED) {
            endSectionV18();
        }
    }

    private static void beginSectionV18(String sectionName) {
        android.os.Trace.beginSection(sectionName);
    }

    private static void endSectionV18() {
        android.os.Trace.endSection();
    }

}
