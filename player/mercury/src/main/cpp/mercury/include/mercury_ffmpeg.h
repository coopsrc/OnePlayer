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

#ifndef ONEPLAYER_MC_FFMPEG_H
#define ONEPLAYER_MC_FFMPEG_H

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT void JNICALL enableNativeRedirection(JNIEnv *env, jclass object);
JNIEXPORT void JNICALL disableNativeRedirection(JNIEnv *env, jclass object);
JNIEXPORT void JNICALL setNativeLogLevel(JNIEnv *env, jclass object, jint level);
JNIEXPORT jint JNICALL getNativeLogLevel(JNIEnv *env, jclass object);
JNIEXPORT jstring JNICALL getNativeFFmpegVersion(JNIEnv *env, jclass object);
JNIEXPORT jstring JNICALL getNativeVersion(JNIEnv *env, jclass object);
JNIEXPORT jint JNICALL nativeFFmpegExecute(JNIEnv *env, jclass object, jlong id, jobjectArray);
JNIEXPORT void JNICALL nativeFFmpegCancel(JNIEnv *env, jclass object, jlong id);
JNIEXPORT int JNICALL
registerNewNativeFFmpegPipe(JNIEnv *env, jclass object, jstring ffmpegPipePath);
JNIEXPORT jstring JNICALL getNativeBuildDate(JNIEnv *env, jclass object);
JNIEXPORT int JNICALL setNativeEnvironmentVariable(JNIEnv *env, jclass object, jstring variableName,
                                                   jstring variableValue);
JNIEXPORT jstring JNICALL getNativeLastCommandOutput(JNIEnv *env, jclass object);
JNIEXPORT void JNICALL ignoreNativeSignal(JNIEnv *env, jclass object, jint signum);


#ifdef __cplusplus
}
#endif

#endif //ONEPLAYER_MC_FFMPEG_H
