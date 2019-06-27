package com.coopsrc.oneplayer.core;

/**
 * @author tingkuo
 * <p>
 * Date: 2019-06-27 16:50
 * <p>
 * Default {@link ControlDispatcher} that dispatches all operations to the player without
 * modification.
 */
public class DefaultControlDispatcher implements ControlDispatcher {
    @Override
    public boolean dispatchSetPlayWhenReady(OnePlayer player, boolean playWhenReady) {
        if (playWhenReady) {
            player.start();
        } else {
            player.pause();
        }
        return true;
    }

    @Override
    public boolean dispatchSeekTo(OnePlayer player, long positionMs) {
        player.seekTo(positionMs);
        return true;
    }

    @Override
    public boolean dispatchStop(OnePlayer player, boolean reset) {
        player.stop();
        if (reset) {
            player.reset();
        }
        return true;
    }
}
