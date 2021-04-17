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
// Created by Tingkuo Zhang on 2021-04-01 22:55.
//

#include <jni.h>

#include "mercury_player.h"
#include "logger.h"

JavaVM *javaVm = nullptr;

jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    javaVm = vm;

    JNIEnv *env;
    if (vm->GetEnv(reinterpret_cast<void **>(&env), JNI_VERSION_1_6) != JNI_OK) {
        return JNI_ERR;
    }

    return JNI_VERSION_1_6;
}

extern "C"
JNIEXPORT jlong JNICALL
Java_com_coopsrc_oneplayer_kernel_mercury_MercuryPlayer_nativeInit(JNIEnv *env, jobject thiz) {
    ALOGW("nativeInit: from %p", thiz);
    auto *player = new MercuryPlayer(new MercuryBridge(javaVm, env, thiz));

    return reinterpret_cast<jlong>(player);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_coopsrc_oneplayer_kernel_mercury_MercuryPlayer_nativeSetSurface(JNIEnv *env, jobject thiz, jlong handle,
                                                                         jobject surface) {

    if (!surface) {
        ALOGE("[%p]nativeSetSurface: surface is null", handle);
        return;
    }
    ALOGW("[%p]nativeSetSurface: %p", handle, surface);


    auto *player = reinterpret_cast<MercuryPlayer *>(handle);

    ANativeWindow *nativeWindow = ANativeWindow_fromSurface(env, surface);
    player->setWindow(nativeWindow);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_coopsrc_oneplayer_kernel_mercury_MercuryPlayer_nativeSetDataSource(JNIEnv *env,
                                                                            jobject thiz,
                                                                            jlong handle,
                                                                            jstring path_) {
    const char *path = env->GetStringUTFChars(path_, nullptr);
    ALOGW("[%p]nativeSetDataSource: %s", handle, path);

    auto *player = reinterpret_cast<MercuryPlayer *>(handle);
    player->setDataSource(path);

    env->ReleaseStringUTFChars(path_, path);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_coopsrc_oneplayer_kernel_mercury_MercuryPlayer_nativePrepare(JNIEnv *env, jobject thiz,
                                                                      jlong handle) {
    ALOGW("[%p]nativePrepare: ", handle);
    auto *player = reinterpret_cast<MercuryPlayer *>(handle);
    player->prepare();
}

extern "C"
JNIEXPORT void JNICALL
Java_com_coopsrc_oneplayer_kernel_mercury_MercuryPlayer_nativeStart(JNIEnv *env, jobject thiz, jlong handle) {
    ALOGW("[%p]nativeStart: ", handle);
    auto *player = reinterpret_cast<MercuryPlayer *>(handle);
    player->start();
}

extern "C"
JNIEXPORT void JNICALL
Java_com_coopsrc_oneplayer_kernel_mercury_MercuryPlayer_nativePause(JNIEnv *env, jobject thiz, jlong handle) {
    ALOGW("[%p]nativePause: ", handle);
    auto *player = reinterpret_cast<MercuryPlayer *>(handle);
    player->pause();
}

extern "C"
JNIEXPORT void JNICALL
Java_com_coopsrc_oneplayer_kernel_mercury_MercuryPlayer_nativeStop(JNIEnv *env, jobject thiz, jlong handle) {
    ALOGW("[%p]nativeStop: ", handle);
    auto *player = reinterpret_cast<MercuryPlayer *>(handle);
    player->stop();
    delete player;
}