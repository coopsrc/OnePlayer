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

#include "video_channel.h"
#include "mercury_config.h"



void *video_decode_task(void *args) {
    auto *videoChannel = static_cast<VideoChannel *>(args);
    videoChannel->decode();
    return nullptr;
}

void *video_play_task(void *args) {
    auto *videoChannel = static_cast<VideoChannel *>(args);

    videoChannel->_play();

    return nullptr;
}

VideoChannel::VideoChannel(int channelId, MercuryBridge *bridge, AVCodecContext *avCodecContext,
                           const AVRational &timeBase, double fps) :
        BaseChannel(channelId, bridge, avCodecContext, timeBase), fps(fps) {

    pthread_mutex_init(&mWindowMutex, nullptr);
}

VideoChannel::~VideoChannel() {
    pthread_mutex_destroy(&mWindowMutex);
}

void VideoChannel::setWindow(ANativeWindow *nativeWindow) {

    pthread_mutex_lock(&mWindowMutex);

    if (mNativeWindow) {
        ANativeWindow_release(mNativeWindow);
    }

    this->mNativeWindow = nativeWindow;
    pthread_mutex_unlock(&mWindowMutex);
}


void VideoChannel::play() {
    isPlaying = true;
    setEnable(true);
    pthread_create(&mVideoDecodeTask, nullptr, video_decode_task, this);
    pthread_create(&mVideoPlayTask, nullptr, video_play_task, this);
}

void VideoChannel::decode() {
    AVPacket *avPacket = nullptr;
    int result;
    while (isPlaying) {
        result = mAVPacketQueue.deQueue(avPacket);
        if (!isPlaying) {
            break;
        }
        if (result != 1) {
            continue;
        }

        result = avcodec_send_packet(mAVCodecContext, avPacket);
        releaseAvPacket(avPacket);
        if (result < 0) {
            break;
        }

        AVFrame *avFrame = av_frame_alloc();
        result = avcodec_receive_frame(mAVCodecContext, avFrame);
        if (result == AVERROR(EAGAIN)) {
            continue;
        } else if (result < 0) {
            break;
        }
        mAVFrameQueue.enQueue(avFrame);
    }
    releaseAvPacket(avPacket);
}

void VideoChannel::stop() {
    isPlaying = false;
    mBridge = nullptr;
    setEnable(false);

    pthread_join(mVideoDecodeTask, nullptr);
    pthread_join(mVideoPlayTask, nullptr);

    if (mNativeWindow) {
        ANativeWindow_release(mNativeWindow);
        mNativeWindow = nullptr;
    }

}

void VideoChannel::_play() {

    SwsContext *swsContext = sws_getContext(
            mAVCodecContext->width,
            mAVCodecContext->height,
            mAVCodecContext->pix_fmt,
            mAVCodecContext->width,
            mAVCodecContext->height,
            AV_PIX_FMT_RGBA,
            SWS_FAST_BILINEAR,
            nullptr, nullptr, nullptr);

    uint8_t *data[4];
    int linesize[4];

    av_image_alloc(data, linesize, mAVCodecContext->width, mAVCodecContext->height, AV_PIX_FMT_RGBA, 1);

    AVFrame *avFrame;
    int result;

    double frame_delay = 1.0 / fps;
    double extra_delay;
    double delay;

    double clockDiff;

    while (isPlaying) {
        result = mAVFrameQueue.deQueue(avFrame);

        if (!isPlaying) {
            break;
        }

        if (result != 1) {
            continue;
        }

        clock = avFrame->best_effort_timestamp * av_q2d(mTimeBase);

        extra_delay = avFrame->repeat_pict / (2 * fps);
        delay = frame_delay + extra_delay;
        if (mAudioChannel) {
            clockDiff = clock - mAudioChannel->clock;

            double sync = FFMAX(AV_SYNC_THRESHOLD_MAX, FFMIN(AV_SYNC_THRESHOLD_MIN, delay));

            if (clockDiff <= -sync) {
                delay = FFMAX(0, delay + clockDiff);
            } else if (clockDiff > sync) {
                delay = delay + clockDiff;
            }

            ALOGD("VideoChannel::_play: Video: %1f, Audio: %1f, Delay: %1f, A-V=%1f", clock, mAudioChannel->clock, delay,
                  -clockDiff);
        }


        av_usleep(delay * 1000000);

        sws_scale(swsContext, avFrame->data, avFrame->linesize, 0, avFrame->height, data, linesize);

        renderFrame(data, linesize, mAVCodecContext->width, mAVCodecContext->height);
        releaseAvFrame(avFrame);
    }

    av_free(&data);
    isPlaying = false;
    releaseAvFrame(avFrame);
    sws_freeContext(swsContext);
}

void VideoChannel::renderFrame(uint8_t **data, int *linesize, int width, int height) {

    pthread_mutex_lock(&mWindowMutex);

    if (!mNativeWindow) {
        pthread_mutex_unlock(&mWindowMutex);
        return;
    }

    ANativeWindow_setBuffersGeometry(mNativeWindow, width, height, WINDOW_FORMAT_RGBA_8888);

    ANativeWindow_Buffer buffer;
    int result = ANativeWindow_lock(mNativeWindow, &buffer, nullptr);
    if (result != 0) {
        ANativeWindow_release(mNativeWindow);
        mNativeWindow = nullptr;
        pthread_mutex_unlock(&mWindowMutex);
        return;
    }

    auto *dstData = static_cast<uint8_t *>(buffer.bits);
    int dstSize = buffer.stride * 4;

    uint8_t *srcData = data[0];
    int srcSize = linesize[0];

    for (int i = 0; i < buffer.height; ++i) {
        memcpy(dstData + i * dstSize, srcData + i * srcSize, srcSize);
    }

    ANativeWindow_unlockAndPost(mNativeWindow);
    pthread_mutex_unlock(&mWindowMutex);
}
