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

#include "base_channel.h"

BaseChannel::BaseChannel(int channelId, MercuryBridge *bridge, AVCodecContext *avCodecContext, AVRational timeBase) :
        mChannelId(channelId),
        mBridge(bridge),
        mAVCodecContext(avCodecContext),
        mTimeBase(timeBase) {

    mAVPacketQueue.setReleaseHandle(releaseAvPacket);
    mAVFrameQueue.setReleaseHandle(releaseAvFrame);
}

BaseChannel::~BaseChannel() {
    if (mAVCodecContext) {
        avcodec_close(mAVCodecContext);
        avcodec_free_context(&mAVCodecContext);
        mAVCodecContext = nullptr;
    }

    mAVPacketQueue.clear();
    mAVFrameQueue.clear();

}

void BaseChannel::setEnable(bool enable) {
    mAVPacketQueue.setEnable(enable);
    mAVFrameQueue.setEnable(enable);
}

void BaseChannel::releaseAvFrame(AVFrame *&avFrame) {
    if (avFrame) {
        av_frame_free(&avFrame);
        avFrame = nullptr;
    }
}

void BaseChannel::releaseAvPacket(AVPacket *&avPacket) {
    if (avPacket) {
        av_packet_free(&avPacket);
        avPacket = nullptr;
    }
}
