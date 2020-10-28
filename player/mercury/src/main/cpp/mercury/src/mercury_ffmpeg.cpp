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
// Created by tingkuo on 2020-10-28 19:51.
//

#include <jni.h>

#include <string>

#include "logger.h"

#include "mercury_player.h"
#include "mercury_ffmpeg.h"

#include "libavutil/ffversion.h"

JNINativeMethod methods[] = {
        {"_enableNativeRedirection",      "()V",                                     (void *) enableNativeRedirection},
        {"_disableNativeRedirection",     "()V",                                     (void *) disableNativeRedirection},
        {"_setNativeLogLevel",            "(I)V",                                    (void *) setNativeLogLevel},
        {"_getNativeLogLevel",            "()I",                                     (void *) getNativeLogLevel},
        {"_getNativeFFmpegVersion",       "()Ljava/lang/String;",                    (void *) getNativeFFmpegVersion},
        {"_getNativeVersion",             "()Ljava/lang/String;",                    (void *) getNativeVersion},
        {"_nativeFFmpegExecute",          "(J[Ljava/lang/String;)I",                 (void *) nativeFFmpegExecute},
        {"_nativeFFmpegCancel",           "(J)V",                                    (void *) nativeFFmpegCancel},
        {"_registerNewNativeFFmpegPipe",  "(Ljava/lang/String;)I",                   (void *) registerNewNativeFFmpegPipe},
        {"_getNativeBuildDate",           "()Ljava/lang/String;",                    (void *) getNativeBuildDate},
        {"_setNativeEnvironmentVariable", "(Ljava/lang/String;Ljava/lang/String;)I", (void *) setNativeEnvironmentVariable},
        {"_getNativeLastCommandOutput",   "()Ljava/lang/String;",                    (void *) getNativeLastCommandOutput},
        {"_ignoreNativeSignal",           "(I)V",                                    (void *) ignoreNativeSignal}
};


jint registerNativeMethod(JNIEnv *env) {
    jclass clazz = env->FindClass("com/coopsrc/oneplayer/kernel/mercury/FFmpegConfig");
    if (env->RegisterNatives(clazz, methods, sizeof(methods) / sizeof(methods[0])) < 0) {
        return JNI_ERR;
    }

    return JNI_OK;
}

jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env = nullptr;
    if (vm->GetEnv(reinterpret_cast<void **>(&env), JNI_VERSION_1_6) != JNI_OK) {
        return JNI_ERR;
    }

    if (registerNativeMethod(env) != JNI_OK) {
        return JNI_ERR;
    }

    return JNI_VERSION_1_6;
}

void enableNativeRedirection(JNIEnv *env, jclass object) {

}

void disableNativeRedirection(JNIEnv *env, jclass object) {

}

void setNativeLogLevel(JNIEnv *env, jclass object, jint level) {

}

jint getNativeLogLevel(JNIEnv *env, jclass object) {
    return 0;
}

jstring getNativeFFmpegVersion(JNIEnv *env, jclass object) {
    std::string version = FFMPEG_VERSION;
    ALOGD("ffmpeg version: %s", version.c_str());
    return env->NewStringUTF(version.c_str());
}

jstring getNativeVersion(JNIEnv *env, jclass object) {
    std::string version = MERCURY_PLAYER_VERSION;
    ALOGD("player version: %s", version.c_str());
    return env->NewStringUTF(version.c_str());
}

jint nativeFFmpegExecute(JNIEnv *env, jclass object, jlong id, jobjectArray) {
    return 0;
}

void nativeFFmpegCancel(JNIEnv *env, jclass object, jlong id) {

}

int registerNewNativeFFmpegPipe(JNIEnv *env, jclass object, jstring ffmpegPipePath) {
    return 0;
}

jstring getNativeBuildDate(JNIEnv *env, jclass object) {
    std::string version = "";
    return env->NewStringUTF(version.c_str());
}

int setNativeEnvironmentVariable(JNIEnv *env, jclass object, jstring variableName,
                                 jstring variableValue) {
    return 0;
}

jstring getNativeLastCommandOutput(JNIEnv *env, jclass object) {
    std::string version = "";
    return env->NewStringUTF(version.c_str());
}

void ignoreNativeSignal(JNIEnv *env, jclass object, jint signum) {

}
