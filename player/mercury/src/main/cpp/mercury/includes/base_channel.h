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
// Created by Tingkuo Zhang on 2021-04-06 23:35.
//

#ifndef ONEPLAYER_BASE_CHANNEL_H
#define ONEPLAYER_BASE_CHANNEL_H

#include "mercury_bridge.h"
#include "safe_queue.h"

extern "C" {
#include <libavcodec/avcodec.h>
}

class BaseChannel {
public:
    BaseChannel(int channelId, MercuryBridge *bridge, AVCodecContext *avCodecContext, AVRational timeBase);

    virtual ~BaseChannel();

public:
    virtual void play() = 0;

    virtual void stop() = 0;

    virtual void decode() = 0;

    void setEnable(bool enable);

    static void releaseAvFrame(AVFrame *&avFrame);

    static void releaseAvPacket(AVPacket *&avPacket);

public:
    int mChannelId;
    MercuryBridge *mBridge;
    AVCodecContext *mAVCodecContext;
    AVRational mTimeBase;

    SafeQueue<AVPacket *> mAVPacketQueue;
    SafeQueue<AVFrame *> mAVFrameQueue;

    bool isPlaying = false;
    double clock = 0;

};


#endif //ONEPLAYER_BASE_CHANNEL_H
