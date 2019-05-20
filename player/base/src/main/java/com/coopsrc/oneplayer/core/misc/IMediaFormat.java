package com.coopsrc.oneplayer.core.misc;

/**
 * @author tingkuo
 * <p>
 * Date: 2019-05-10 10:20
 */
public interface IMediaFormat {
    // Common keys
    String KEY_MIME = "mime";

    // Video Keys
    String KEY_WIDTH = "width";
    String KEY_HEIGHT = "height";

    String getString(String name);

    int getInteger(String name);
}
