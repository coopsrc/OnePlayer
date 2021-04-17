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
// Created by Tingkuo Zhang on 2021-04-07 22:51.
//

#ifndef ONEPLAYER_AUDIO_CHANNEL_H
#define ONEPLAYER_AUDIO_CHANNEL_H

#include <SLES/OpenSLES.h>
#include <SLES/OpenSLES_Android.h>

#include "base_channel.h"
#include "mercury_bridge.h"
#include "mercury_config.h"

extern "C" {
#include <libavcodec/avcodec.h>
#include <libswresample/swresample.h>
};

class AudioChannel : public BaseChannel {

public:
    friend void *audio_decode_task(void *args);

    friend void *audio_play_task(void *args);

    friend void bqPlayerCallback(SLAndroidSimpleBufferQueueItf bufferQueue, void *pContext);

public:
    AudioChannel(int channelId, MercuryBridge *bridge, AVCodecContext *avCodecContext, const AVRational &timeBase);

    virtual ~AudioChannel();

public:
    virtual void play();

    virtual void stop();

    virtual void decode();

private:
    void _play();

    int _getData();

    void _releaseOpenSL();

private:
    pthread_t mAudioDecodeTask;
    pthread_t mAudioPlayTask;

    SwrContext *swrContext = nullptr;
    int out_channels;
    int out_sampleSize;
    int bufferCount;
    uint8_t *buffer;

    SLPlayItf bqPlayerInterface = nullptr;
    SLObjectItf bqPlayerObject = nullptr;
    SLAndroidSimpleBufferQueueItf bqPlayerBufferQueue = nullptr;
    SLObjectItf outputMixObject = nullptr;
    SLObjectItf engineObject = nullptr;
    SLEngineItf engineInterface = nullptr;
};


#endif //ONEPLAYER_AUDIO_CHANNEL_H
