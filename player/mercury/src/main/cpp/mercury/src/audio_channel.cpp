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

#include "logger.h"
#include "audio_channel.h"


void *audio_play_task(void *args) {

    auto *audioChannel = static_cast<AudioChannel *>(args);

    audioChannel->_play();

    return nullptr;
}

void *audio_decode_task(void *args) {

    auto *audioChannel = static_cast<AudioChannel *>(args);

    audioChannel->decode();

    return nullptr;
}


void bqPlayerCallback(SLAndroidSimpleBufferQueueItf bufferQueue, void *pContext) {

    auto *audioChannel = static_cast<AudioChannel *>(pContext);

    int dataSize = audioChannel->_getData();

    if (dataSize > 0) {
        (*bufferQueue)->Enqueue(bufferQueue, audioChannel->buffer, dataSize);
    }

}

AudioChannel::AudioChannel(int channelId, MercuryBridge *bridge, AVCodecContext *avCodecContext, const AVRational &timeBase)
        : BaseChannel(channelId, bridge, avCodecContext, timeBase) {
    swrContext = swr_alloc();


    out_channels = av_get_channel_layout_nb_channels(AV_CH_LAYOUT_STEREO);
    out_sampleSize = av_get_bytes_per_sample(AV_SAMPLE_FMT_S16);
    int out_sampleRate = 44100;
    bufferCount = out_sampleRate * out_sampleSize * out_channels;
    buffer = static_cast<uint8_t *>(malloc(bufferCount));


}

AudioChannel::~AudioChannel() {

}

void AudioChannel::play() {

    swrContext = swr_alloc_set_opts(nullptr, AV_CH_LAYOUT_STEREO, AV_SAMPLE_FMT_S16, 44100,
                                    mAVCodecContext->channel_layout, mAVCodecContext->sample_fmt, mAVCodecContext->sample_rate,
                                    0, nullptr);
    if (swr_init(swrContext) < 0) {
        ALOGE("AudioChannel::AudioChannel: init SwrContext failed");
    } else {
        ALOGW("AudioChannel::AudioChannel: init SwrContext succeed");
    }

    isPlaying = true;
    setEnable(true);

    pthread_create(&mAudioDecodeTask, nullptr, audio_decode_task, this);
    pthread_create(&mAudioPlayTask, nullptr, audio_play_task, this);
}

void AudioChannel::decode() {
    AVPacket *avPacket = nullptr;
    int result;
    while (isPlaying) {
        result = mAVPacketQueue.deQueue(avPacket);

        if (result != 1) {
            continue;
        }

        if (!isPlaying) {
            break;
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

void AudioChannel::stop() {
    isPlaying = false;
    mBridge = nullptr;
    setEnable(false);

    pthread_join(mAudioDecodeTask, nullptr);
    pthread_join(mAudioPlayTask, nullptr);

    _releaseOpenSL();
    if (!swrContext) {
        swr_free(&swrContext);
        swrContext = nullptr;
    }
}

void AudioChannel::_play() {
    SLresult result;

    result = slCreateEngine(&engineObject, 0, nullptr, 0, nullptr, nullptr);
    if (SL_RESULT_SUCCESS != result) {
        ALOGE("AudioChannel::_play: create OpenSLES object failed.");
        return;
    }

    result = (*engineObject)->Realize(engineObject, SL_BOOLEAN_FALSE);
    if (SL_RESULT_SUCCESS != result) {
        ALOGE("AudioChannel::_play: init OpenSLES object failed.");
        return;
    }

    result = (*engineObject)->GetInterface(engineObject, SL_IID_ENGINE, &engineInterface);
    if (SL_RESULT_SUCCESS != result) {
        ALOGE("AudioChannel::_play: get OpenSLES interface failed.");
        return;
    }

    result = (*engineInterface)->CreateOutputMix(engineInterface, &outputMixObject, 0, nullptr, nullptr);
    if (SL_RESULT_SUCCESS != result) {
        ALOGE("AudioChannel::_play: create OpenSLES OutputMix failed.");
        return;
    }

    result = (*outputMixObject)->Realize(outputMixObject, SL_BOOLEAN_FALSE);
    if (SL_RESULT_SUCCESS != result) {
        ALOGE("AudioChannel::_play: init OpenSLES MixObject failed.");
        return;
    }

    SLDataLocator_AndroidSimpleBufferQueue bufferQueue = {SL_DATALOCATOR_ANDROIDSIMPLEBUFFERQUEUE, 2};
    SLDataFormat_PCM dataFormatPcm = {
            SL_DATAFORMAT_PCM,
            2,
            SL_SAMPLINGRATE_44_1,
            SL_PCMSAMPLEFORMAT_FIXED_16,
            SL_PCMSAMPLEFORMAT_FIXED_16,
            SL_SPEAKER_FRONT_LEFT | SL_SPEAKER_FRONT_RIGHT,
            SL_BYTEORDER_LITTLEENDIAN
    };
    SLDataSource slDataSource = {&bufferQueue, &dataFormatPcm};

    SLDataLocator_OutputMix outputMix = {SL_DATALOCATOR_OUTPUTMIX, outputMixObject};
    SLDataSink audioSink = {&outputMix, nullptr};

    const SLInterfaceID ids[1] = {SL_IID_BUFFERQUEUE};
    const SLboolean req[1] = {SL_BOOLEAN_TRUE};

    result = (*engineInterface)->CreateAudioPlayer(engineInterface, &bqPlayerObject, &slDataSource, &audioSink, 1, ids, req);
    if (SL_RESULT_SUCCESS != result) {
        ALOGE("AudioChannel::_play: create audio player failed.");
        return;
    }

    result = (*bqPlayerObject)->Realize(bqPlayerObject, SL_BOOLEAN_FALSE);
    if (SL_RESULT_SUCCESS != result) {
        ALOGE("AudioChannel::_play: init audio player failed.");
        return;
    }

    result = (*bqPlayerObject)->GetInterface(bqPlayerObject, SL_IID_BUFFERQUEUE, &bqPlayerBufferQueue);
    if (SL_RESULT_SUCCESS != result) {
        ALOGE("AudioChannel::_play: get play data queue interface failed.");
        return;
    }

    result = (*bqPlayerBufferQueue)->RegisterCallback(bqPlayerBufferQueue, bqPlayerCallback, this);
    if (SL_RESULT_SUCCESS != result) {
        ALOGE("AudioChannel::_play: register player buffer queue callback failed.");
        return;
    }

    (*bqPlayerObject)->GetInterface(bqPlayerObject, SL_IID_PLAY, &bqPlayerInterface);
    (*bqPlayerInterface)->SetPlayState(bqPlayerInterface, SL_PLAYSTATE_PLAYING);

    bqPlayerCallback(bqPlayerBufferQueue, this);
}

int AudioChannel::_getData() {

    int dataSize = 0;
    AVFrame *avFrame = nullptr;
    int result;
    while (isPlaying) {
        result = mAVFrameQueue.deQueue(avFrame);

        if (!isPlaying) {
            break;
        }

        if (result != 1) {
            continue;
        }

        int nb = swr_convert(swrContext, &buffer, bufferCount, (const uint8_t **) avFrame->data, avFrame->nb_samples);

        dataSize = nb * out_channels * out_sampleSize;
        clock = avFrame->pts * av_q2d(mTimeBase);
        break;

    }

    releaseAvFrame(avFrame);
    return dataSize;
}

void AudioChannel::_releaseOpenSL() {

    if (bqPlayerInterface) {
        (*bqPlayerInterface)->SetPlayState(bqPlayerInterface, SL_PLAYSTATE_STOPPED);
        bqPlayerInterface = nullptr;
    }

    if (bqPlayerObject) {
        (*bqPlayerObject)->Destroy(bqPlayerObject);
        bqPlayerObject = nullptr;
        bqPlayerObject = nullptr;
    }

    if (outputMixObject) {
        (*outputMixObject)->Destroy(outputMixObject);
        outputMixObject = nullptr;
    }

    if (engineObject) {
        (*engineObject)->Destroy(engineObject);
        engineObject = nullptr;
        engineInterface = nullptr;
    }
}
