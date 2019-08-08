package com.coopsrc.oneplayer.core.text;

import java.util.List;

/**
 * @author tingkuo
 * <p>
 * Date: 2019-08-08 11:19
 */
public interface TextOutput {
    /**
     * Called when there is a change in the {@link Cue}s.
     *
     * @param cues The {@link Cue}s. May be empty.
     */
    void onCues(List<Cue> cues);
}
