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
// Created by Tingkuo Zhang on 2021-04-06 22:39.
//

#ifndef ONEPLAYER_MERCURY_CONFIG_H
#define ONEPLAYER_MERCURY_CONFIG_H


#define ERROR_OPEN_FILE_FAILED 1
#define ERROR_FIND_STREAMS_FAILED 2
#define ERROR_FIND_DECODER_FAILED 3
#define ERROR_ALLOC_CODEC_CONTEXT_FAILED 4
#define ERROR_CODEC_CONTEXT_PARAMETERS_FAILED 5
#define ERROR_OPEN_DECODER_FAILED 6
#define ERROR_NO_MEDIA_DATA 7

#define INFO_GET_DURATION 1


//#define MAX_AUDIO_FARME_SIZE 48000 * 2
#define MAX_AUDIO_FARME_SIZE 44100 * 2

#define AV_SYNC_THRESHOLD_MIN 0.04
#define AV_SYNC_THRESHOLD_MAX 0.1

#endif //ONEPLAYER_MERCURY_CONFIG_H
