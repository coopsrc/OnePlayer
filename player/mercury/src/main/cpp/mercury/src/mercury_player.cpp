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

#include <cstring>
#include "mercury_player.h"

#include "logger.h"

void *prepare_task(void *args) {
    auto *player = static_cast<MercuryPlayer *>(args);

    player->_prepare();

    return nullptr;
}

void *start_task(void *args) {
    auto *player = static_cast<MercuryPlayer *>(args);

    player->_start();

    return nullptr;
}


MercuryPlayer::MercuryPlayer(MercuryBridge *bridge) :
        mBridge(bridge) {
    avformat_network_init();

    mAudioChannel = nullptr;
    mVideoChannel = nullptr;
}

MercuryPlayer::~MercuryPlayer() {
    avformat_network_deinit();

    if (mBridge) {
        delete mBridge;
        mBridge = nullptr;
    }
    if (mPath) {
        delete[] mPath;
        mPath = nullptr;
    }
}

void MercuryPlayer::setWindow(ANativeWindow *nativeWindow) {
    this->mNativeWindow = nativeWindow;
    if (mVideoChannel) {
        mVideoChannel->setWindow(mNativeWindow);
    }
}

void MercuryPlayer::setDataSource(const char *path) {
    ALOGI("setDataSource: %s.", path);
    if (mPath) {
        delete[] mPath;
        mPath = nullptr;
    }
    mPath = new char[strlen(path) + 1];
    strcpy(mPath, path);
}

void MercuryPlayer::prepare() {
    pthread_create(&mPrepareTask, nullptr, prepare_task, this);
}

void MercuryPlayer::start() {
    isPlaying = true;
    if (mVideoChannel) {
        mVideoChannel->mAudioChannel = mAudioChannel;
        mVideoChannel->play();
    }
    if (mAudioChannel) {
        mAudioChannel->play();
    }
    pthread_create(&mStartTask, nullptr, start_task, this);
}

void MercuryPlayer::pause() {

}

void MercuryPlayer::stop() {
    isPlaying = false;

    pthread_join(mPrepareTask, nullptr);
    pthread_join(mStartTask, nullptr);

    _release();
}

void MercuryPlayer::_prepare() {
    avFormatContext = avformat_alloc_context();

    AVDictionary *options;
//    av_dict_set(&options, "timeout", "5000000", 0);

    int result = avformat_open_input(&avFormatContext, mPath, nullptr, &options);

    if (result != 0) {
        ALOGE("_prepare: open file %s failed, code: %d, message: %s", mPath, result, av_err2str(result));
        mBridge->onError(ERROR_OPEN_FILE_FAILED, result, THREAD_CHILD);
        goto ERROR;
    }

    result = avformat_find_stream_info(avFormatContext, nullptr);
    if (result < 0) {
        ALOGE("_prepare: find %s stream info failed, code: %d, message: %s", mPath, result, av_err2str(result));
        mBridge->onError(ERROR_FIND_STREAMS_FAILED, result, THREAD_CHILD);
        goto ERROR;
    }

    mDuration = avFormatContext->duration / AV_TIME_BASE;

    mBridge->onInfo(INFO_GET_DURATION, mDuration, THREAD_CHILD);

    for (int i = 0; i < avFormatContext->nb_streams; i++) {
        AVStream *avStream = avFormatContext->streams[i];

        AVCodecParameters *avCodecParameters = avStream->codecpar;
        AVCodecID avCodecId = avCodecParameters->codec_id;
        AVCodec *avCodec = avcodec_find_decoder(avCodecId);

        if (!avCodec) {
            ALOGE("_prepare: find decoder codec %d failed.", avCodecId);
            mBridge->onError(ERROR_FIND_DECODER_FAILED, avCodecParameters->codec_id, THREAD_CHILD);
            goto ERROR;
        }

        AVCodecContext *avCodecContext = avcodec_alloc_context3(avCodec);
        if (!avCodecContext) {
            ALOGE("_prepare: open decoder codec %d failed.", avCodecId);
            mBridge->onError(ERROR_ALLOC_CODEC_CONTEXT_FAILED, avCodecParameters->codec_id, THREAD_CHILD);
            goto ERROR;
        }

        result = avcodec_parameters_to_context(avCodecContext, avCodecParameters);
        if (result != 0) {
            mBridge->onError(ERROR_CODEC_CONTEXT_PARAMETERS_FAILED, avCodecParameters->codec_id, THREAD_CHILD);
            goto ERROR;
        }

        result = avcodec_open2(avCodecContext, avCodec, nullptr);
        if (result != 0) {
            mBridge->onError(ERROR_OPEN_DECODER_FAILED, avCodecParameters->codec_id, THREAD_CHILD);
            goto ERROR;
        }

        if (avCodecParameters->codec_type == AVMEDIA_TYPE_VIDEO) {
            double fps = av_q2d(avStream->avg_frame_rate);

            if (isnan(fps) || fps == 0) {
                fps = av_q2d(avStream->r_frame_rate);
            }

            if (isnan(fps) || fps == 0) {
                fps = av_q2d(av_guess_frame_rate(avFormatContext, avStream, nullptr));
            }

            mVideoChannel = new VideoChannel(i, mBridge, avCodecContext, avStream->time_base, fps);
            if (mNativeWindow) {
                mVideoChannel->setWindow(mNativeWindow);
            }
        } else if (avCodecParameters->codec_type == AVMEDIA_TYPE_AUDIO) {
            mAudioChannel = new AudioChannel(i, mBridge, avCodecContext, avStream->time_base);
        }
    }

    if (!mVideoChannel && !mAudioChannel) {
        mBridge->onError(ERROR_NO_MEDIA_DATA, -1, THREAD_CHILD);
        goto ERROR;
    }

    mBridge->onPrepared(THREAD_CHILD);
    return;

    ERROR:
    ALOGE("解析媒体文件失败。。。");
    _release();

}

void MercuryPlayer::_start() {
    int result;
    while (isPlaying) {
        AVPacket *avPacket = av_packet_alloc();
        result = av_read_frame(avFormatContext, avPacket);

        if (result == 0) {

            if (mVideoChannel && avPacket->stream_index == mVideoChannel->mChannelId) {
                mVideoChannel->mAVPacketQueue.enQueue(avPacket);
            } else if (mAudioChannel && avPacket->stream_index == mAudioChannel->mChannelId) {
                mAudioChannel->mAVPacketQueue.enQueue(avPacket);
            } else {
                av_packet_free(&avPacket);
            }

        } else if (result == AVERROR_EOF) {
            if (mVideoChannel && mVideoChannel->mAVPacketQueue.empty() && mVideoChannel->mAVFrameQueue.empty()) {
                break;
            }
        } else {
            break;
        }
    }
    isPlaying = false;
    if (mVideoChannel) {
        mVideoChannel->stop();
    }
    if (mAudioChannel) {
        mAudioChannel->stop();
    }
}

void MercuryPlayer::_release() {
    if (mAudioChannel) {
        delete mAudioChannel;
        mAudioChannel = nullptr;
    }

    if (mVideoChannel) {
        delete mVideoChannel;
        mVideoChannel = nullptr;
    }

    if (avFormatContext) {
        avformat_close_input(&avFormatContext);
        avformat_free_context(avFormatContext);
        avFormatContext = nullptr;
    }
}
