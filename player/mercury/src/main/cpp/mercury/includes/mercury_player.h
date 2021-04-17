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

//
// Created by Tingkuo Zhang on 2021-04-01 23:01.
//

#ifndef ONEPLAYER_MERCURY_PLAYER_H
#define ONEPLAYER_MERCURY_PLAYER_H

#include <pthread.h>
#include <android/native_window_jni.h>

#include "mercury_config.h"
#include "mercury_bridge.h"
#include "audio_channel.h"
#include "video_channel.h"


extern "C" {
#include <libavformat/avformat.h>
}

class MercuryPlayer {
    friend void *prepare_task(void *args);

    friend void *start_task(void *args);

public:
    MercuryPlayer(MercuryBridge *bridge);

    ~MercuryPlayer();

public:
    void setWindow(ANativeWindow *nativeWindow);

    void setDataSource(const char *path);

    void prepare();

    void start();

    void pause();

    void stop();

private:
    void _prepare();

    void _start();

    void _release();

private:
    char *mPath = nullptr;
    pthread_t mPrepareTask;
    pthread_t mStartTask;
    MercuryBridge *mBridge;

    int64_t mDuration;
    bool isPlaying;

    AVFormatContext *avFormatContext;

    AudioChannel *mAudioChannel;
    VideoChannel *mVideoChannel;

    ANativeWindow *mNativeWindow = nullptr;

    pthread_mutex_t seekMutex;
};


#endif //ONEPLAYER_MERCURY_PLAYER_H
