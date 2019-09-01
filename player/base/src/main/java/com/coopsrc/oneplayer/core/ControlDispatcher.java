package com.coopsrc.oneplayer.core;

import com.coopsrc.oneplayer.core.utils.Constants;

/**
 * @author tingkuo
 * <p>
 * Date: 2019-06-27 16:46
 */
public interface ControlDispatcher {
    /**
     * Dispatches a {@link OnePlayer#start()} operation.
     * Dispatches a {@link OnePlayer#pause()} operation.
     *
     * @param player        The {@link OnePlayer} to which the operation should be dispatched.
     * @param playWhenReady Whether playback should proceed when ready.
     * @return True if the operation was dispatched. False if suppressed.
     */
    boolean dispatchSetPlayWhenReady(OnePlayer player, boolean playWhenReady);

    /**
     * Dispatches a {@link OnePlayer#seekTo(long)} operation.
     *
     * @param player     The {@link OnePlayer} to which the operation should be dispatched.
     * @param positionMs The seek position in the specified window, or {@link Constants#TIME_UNSET} to seek to
     *                   the window's default position.
     * @return True if the operation was dispatched. False if suppressed.
     */
    boolean dispatchSeekTo(OnePlayer player, long positionMs);


    /**
     * Dispatches a {@link OnePlayer#stop()} operation.
     *
     * @param player The {@link OnePlayer} to which the operation should be dispatched.
     * @param reset  Whether the player should be reset.
     * @return True if the operation was dispatched. False if suppressed.
     */
    boolean dispatchStop(OnePlayer player, boolean reset);
}
