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

/**
 * @author tingkuo
 * <p>
 * Datetime: 2020-10-28 20:26
 */
public class FFmpegConfig {

    public static void enableNativeRedirection() {
        _enableNativeRedirection();
    }

    public static void disableNativeRedirection() {
        _disableNativeRedirection();
    }

    public static void setNativeLogLevel(int level) {
        _setNativeLogLevel(level);
    }

    public static int getNativeLogLevel() {
        return _getNativeLogLevel();
    }

    public static String getNativeFFmpegVersion() {
        return _getNativeFFmpegVersion();
    }

    public static String getNativeVersion() {
        return _getNativeVersion();
    }

    public static int nativeFFmpegExecute(final long executionId, final String[] arguments) {
        return _nativeFFmpegExecute(executionId, arguments);
    }

    public static void nativeFFmpegCancel(final long executionId) {
        _nativeFFmpegCancel(executionId);
    }

    public static int registerNewNativeFFmpegPipe(final String ffmpegPipePath) {
        return _registerNewNativeFFmpegPipe(ffmpegPipePath);
    }

    public static String getNativeBuildDate() {
        return _getNativeBuildDate();
    }

    public static int setNativeEnvironmentVariable(final String variableName, final String variableValue) {
        return _setNativeEnvironmentVariable(variableName, variableValue);
    }

    public static String getNativeLastCommandOutput() {
        return _getNativeLastCommandOutput();
    }

    public static void ignoreNativeSignal(final int signum) {
        _ignoreNativeSignal(signum);
    }


    private static native void _enableNativeRedirection();

    private static native void _disableNativeRedirection();

    private static native void _setNativeLogLevel(int level);

    private static native int _getNativeLogLevel();

    private static native String _getNativeFFmpegVersion();

    private static native String _getNativeVersion();

    private native static int _nativeFFmpegExecute(final long executionId, final String[] arguments);

    private static native void _nativeFFmpegCancel(final long executionId);

    private native static int _registerNewNativeFFmpegPipe(final String ffmpegPipePath);

    private native static String _getNativeBuildDate();

    private native static int _setNativeEnvironmentVariable(final String variableName, final String variableValue);

    private native static String _getNativeLastCommandOutput();

    private native static void _ignoreNativeSignal(final int signum);

}
