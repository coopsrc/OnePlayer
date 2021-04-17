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
 * Datetime: 2021-04-01 22:50
 */
public interface IMercuryPlayer {
    void setSurface(Surface surface);

    void setDataSource(String path);

    void prepare();

    void start();

    void pause();

    void stop();

    interface OnPrepareListener {
        void onPrepared(IMercuryPlayer player);
    }

    interface OnInfoListener {
        void onInfo(IMercuryPlayer player, int what, int extra);
    }

    interface OnErrorListener {
        void onError(IMercuryPlayer player, int what, int extra);
    }
}
