/*
 * Copyright (C) 2020 Zhang Tingkuo(zhangtingkuo@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.coopsrc.oneplayer.kernel.mercury;


import android.view.Surface;

/**
 * @author coopsrc
 * <p>
 * Datetime: 2021-04-01 22:49
 */
public class MercuryPlayer implements IMercuryPlayer {

    private final long nativeHandle;

    private OnPrepareListener mOnPrepareListener;
    private OnInfoListener mOnInfoListener;
    private OnErrorListener mOnErrorListener;

    static {
        System.loadLibrary("mercury");
    }

    public MercuryPlayer() {
        nativeHandle = nativeInit();
    }

    @Override
    public void setSurface(Surface surface) {
        nativeSetSurface(nativeHandle, surface);
    }

    @Override
    public void setDataSource(String path) {
        nativeSetDataSource(nativeHandle, path);
    }

    @Override
    public void prepare() {
        nativePrepare(nativeHandle);
    }

    @Override
    public void start() {
        nativeStart(nativeHandle);
    }

    @Override
    public void pause() {
        nativePause(nativeHandle);
    }

    @Override
    public void stop() {
        nativeStop(nativeHandle);
    }

    //-- call to native
    private native long nativeInit();

    private native void nativeSetSurface(long handle, Surface surface);

    private native void nativeSetDataSource(long handle, String path);

    private native void nativePrepare(long handle);

    private native void nativeStart(long handle);

    private native void nativePause(long handle);

    private native void nativeStop(long handle);

    //-- native callback
    private void _onPrepared() {
        if (mOnPrepareListener != null) {
            mOnPrepareListener.onPrepared(this);
        }
    }

    private void _onError(int what, int extra) {
        if (mOnErrorListener != null) {
            mOnErrorListener.onError(this, what, extra);
        }
    }

    private void _onInfo(int what, int extra) {
        if (mOnInfoListener != null) {
            mOnInfoListener.onInfo(this, what, extra);
        }
    }

    // -- player listeners
    public void setOnPrepareListener(OnPrepareListener onPrepareListener) {
        mOnPrepareListener = onPrepareListener;
    }

    public void setOnInfoListener(OnInfoListener onInfoListener) {
        mOnInfoListener = onInfoListener;
    }

    public void setOnErrorListener(OnErrorListener onErrorListener) {
        mOnErrorListener = onErrorListener;
    }
}
