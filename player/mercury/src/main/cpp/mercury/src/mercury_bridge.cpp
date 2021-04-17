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
// Created by Tingkuo Zhang on 2021-04-02 00:56.
//

#include "mercury_bridge.h"

MercuryBridge::MercuryBridge(JavaVM *_javaVM, JNIEnv *_env, jobject &_jobj) : javaVm(_javaVM), env(_env) {
    jobj = env->NewGlobalRef(_jobj);
    jclass jclazz = env->GetObjectClass(jobj);

    jmid_prepare = env->GetMethodID(jclazz, "_onPrepared", "()V");
    jmid_error = env->GetMethodID(jclazz, "_onError", "(II)V");
    jmid_info = env->GetMethodID(jclazz, "_onInfo", "(II)V");
}

MercuryBridge::~MercuryBridge() {
    env->DeleteGlobalRef(jobj);
    jobj = nullptr;
}

void MercuryBridge::onPrepared(int thread) {
    if (thread == THREAD_CHILD) {
        JNIEnv *jniEnv;
        if (javaVm->AttachCurrentThread(&jniEnv, nullptr) != JNI_OK) {
            return;
        }
        jniEnv->CallVoidMethod(jobj, jmid_prepare);
        javaVm->DetachCurrentThread();
    } else {
        env->CallVoidMethod(jobj, jmid_prepare);
    }
}

void MercuryBridge::onError(int what, int extra, int thread) {
    if (thread == THREAD_CHILD) {
        JNIEnv *jniEnv;
        if (javaVm->AttachCurrentThread(&jniEnv, nullptr) != JNI_OK) {
            return;
        }
        jniEnv->CallVoidMethod(jobj, jmid_error, what, extra);
        javaVm->DetachCurrentThread();
    } else {
        env->CallVoidMethod(jobj, jmid_error, what, extra);
    }
}

void MercuryBridge::onInfo(int what, int extra, int thread) {
    if (thread == THREAD_CHILD) {
        JNIEnv *jniEnv;
        if (javaVm->AttachCurrentThread(&jniEnv, nullptr) != JNI_OK) {
            return;
        }
        jniEnv->CallVoidMethod(jobj, jmid_info, what, extra);
        javaVm->DetachCurrentThread();
    } else {
        env->CallVoidMethod(jobj, jmid_info, what, extra);
    }
}
