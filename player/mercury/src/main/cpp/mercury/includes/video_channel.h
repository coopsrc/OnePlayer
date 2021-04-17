/*
 * Copyright (C) 2021 Zhang Tingkuo(zhangtingkuo@gmail.com)
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
// Created by Tingkuo Zhang on 2021-04-07 22:52.
//

#ifndef ONEPLAYER_VIDEO_CHANNEL_H
#define ONEPLAYER_VIDEO_CHANNEL_H

#include <android/native_window_jni.h>

#include "base_channel.h"
#include "mercury_bridge.h"
#include "audio_channel.h"
#include "logger.h"


#include <android/native_window_jni.h>

extern "C" {
}
extern "C" {
#include <libavutil/rational.h>
#include <libswscale/swscale.h>
#include <libavutil/imgutils.h>
#include <libavutil/time.h>
}

class VideoChannel : public BaseChannel {

public:
    friend void *video_decode_task(void *args);

    friend void *video_play_task(void *args);

public:
    VideoChannel(int channelId, MercuryBridge *bridge, AVCodecContext *avCodecContext, const AVRational &timeBase, double fps);

    virtual ~VideoChannel();

public:
    virtual void play();

    virtual void stop();

    virtual void decode();

public:
    void setWindow(ANativeWindow *nativeWindow);

private:
    void renderFrame(uint8_t *data[4], int linesize[4], int width, int height);

private:
    void _play();

private:
    double fps;
    pthread_t mVideoDecodeTask;
    pthread_t mVideoPlayTask;

    pthread_mutex_t mWindowMutex;
    ANativeWindow *mNativeWindow = nullptr;

public:
    AudioChannel *mAudioChannel = nullptr;

};


#endif //ONEPLAYER_VIDEO_CHANNEL_H
