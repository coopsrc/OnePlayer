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

#ifndef ONEPLAYER_MERCURY_BRIDGE_H
#define ONEPLAYER_MERCURY_BRIDGE_H


#include <jni.h>

#define THREAD_MAIN 1
#define THREAD_CHILD 2

class MercuryBridge {
public:
    MercuryBridge(JavaVM *_javaVM, JNIEnv *_env, jobject &_jobj);

    ~MercuryBridge();

    void onPrepared(int thread = THREAD_MAIN);

    void onError(int what, int extra, int thread = THREAD_MAIN);

    void onInfo(int what, int extra, int thread = THREAD_MAIN);

public:
    JavaVM *javaVm;
    JNIEnv *env;
    jobject jobj;

    jmethodID jmid_error;
    jmethodID jmid_info;
    jmethodID jmid_prepare;

};


#endif //ONEPLAYER_MERCURY_BRIDGE_H
