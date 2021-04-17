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
// Created by Tingkuo Zhang on 2021-04-07 23:38.
//

#ifndef ONEPLAYER_SAFE_QUEUE_H
#define ONEPLAYER_SAFE_QUEUE_H

#include <queue>
#include <pthread.h>

using namespace std;

template<typename T>

class SafeQueue {
    typedef void (*ReleaseHandle)(T &);

    typedef void (*SyncHandle)(queue<T> &);

public:
    SafeQueue() {
        pthread_mutex_init(&mutex, nullptr);
        pthread_cond_init(&cond, nullptr);
    }

    ~SafeQueue() {
        pthread_cond_destroy(&cond);
        pthread_mutex_destroy(&mutex);
    }

    void enQueue(T element) {
        pthread_mutex_lock(&mutex);

        if (mEnable) {
            queue.push(element);
            pthread_cond_signal(&cond);
        } else {
            releaseHandle(element);
        }

        pthread_mutex_unlock(&mutex);
    }

    int deQueue(T &element) {
        int result = 0;

        pthread_mutex_lock(&mutex);

        while (mEnable && queue.empty()) {
            pthread_cond_wait(&cond, &mutex);
        }

        if (!queue.empty()){
            element = queue.front();
            queue.pop();
            result = 1;
        }

        pthread_mutex_unlock(&mutex);

        return result;
    }

    void clear() {
        pthread_mutex_lock(&mutex);

        int size = queue.size();
        for (int i = 0; i < size; ++i) {
            T element = queue.front();
            releaseHandle(element);
            queue.pop();
        }

        pthread_mutex_unlock(&mutex);
    }

    void setEnable(bool enable) {
        pthread_mutex_lock(&mutex);

        mEnable = enable;

        pthread_cond_signal(&cond);

        pthread_mutex_unlock(&mutex);
    }

    int empty() {
        return queue.empty();
    }

    int size() {
        return queue.size();
    }

    void sync() {
        pthread_mutex_lock(&mutex);

        syncHandle(queue);

        pthread_mutex_unlock(&mutex);
    }

    void setReleaseHandle(ReleaseHandle handle) {
        releaseHandle = handle;
    }

    void setSyncHandle(SyncHandle handle) {
        syncHandle = handle;
    }

private:
    pthread_cond_t cond;
    pthread_mutex_t mutex;
    bool mEnable;
    queue<T> queue;
    ReleaseHandle releaseHandle;
    SyncHandle syncHandle;

};


#endif //ONEPLAYER_SAFE_QUEUE_H
