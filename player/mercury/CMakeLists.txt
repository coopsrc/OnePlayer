cmake_minimum_required(VERSION 3.10.2)

project("OnePlayer")

find_library(log-lib log)
find_library(z-lib z)
find_library(android-lib android)
find_library(OpenSLES-lib OpenSLES)

set(PREBUILT_DIR ${CMAKE_SOURCE_DIR}/prebuilt)

set(FFMPEG_PREBUILT ${PREBUILT_DIR}/ffmpeg)

add_library(avutil
        SHARED
        IMPORTED)
set_target_properties(avutil
        PROPERTIES IMPORTED_LOCATION
        ${FFMPEG_PREBUILT}/${ANDROID_ABI}/libavutil.so)

add_library(swresample
        SHARED
        IMPORTED)
set_target_properties(swresample
        PROPERTIES IMPORTED_LOCATION
        ${FFMPEG_PREBUILT}/${ANDROID_ABI}/libswresample.so)

add_library(swscale
        SHARED
        IMPORTED)
set_target_properties(swscale
        PROPERTIES IMPORTED_LOCATION
        ${FFMPEG_PREBUILT}/${ANDROID_ABI}/libswscale.so)

add_library(avcodec
        SHARED
        IMPORTED)
set_target_properties(avcodec
        PROPERTIES IMPORTED_LOCATION
        ${FFMPEG_PREBUILT}/${ANDROID_ABI}/libavcodec.so)

add_library(avformat
        SHARED
        IMPORTED)
set_target_properties(avformat
        PROPERTIES IMPORTED_LOCATION
        ${FFMPEG_PREBUILT}/${ANDROID_ABI}/libavformat.so)

add_library(avfilter
        SHARED
        IMPORTED)
set_target_properties(avfilter
        PROPERTIES IMPORTED_LOCATION
        ${FFMPEG_PREBUILT}/${ANDROID_ABI}/libavfilter.so)

add_library(avdevice
        SHARED
        IMPORTED)
set_target_properties(avdevice
        PROPERTIES IMPORTED_LOCATION
        ${FFMPEG_PREBUILT}/${ANDROID_ABI}/libavdevice.so)

set(SRC_DIR ${CMAKE_SOURCE_DIR}/src/main/cpp)

set(MERCURY_SRC_DIR ${SRC_DIR}/mercury/src)
set(MERCURY_INC_DIR ${SRC_DIR}/mercury/includes)
set(FFMPEG_INC_DIR ${SRC_DIR}/ffmpeg)

include_directories(mercury PUBLIC ${FFMPEG_INC_DIR})
include_directories(mercury PRIVATE ${MERCURY_INC_DIR})

aux_source_directory(${MERCURY_SRC_DIR} MERCURY_SRC)

add_library(mercury SHARED ${MERCURY_SRC})

target_link_libraries(mercury
        avutil swresample swscale avcodec avformat avfilter avdevice
        ${log-lib} ${android-lib} ${OpenSLES-lib} ${z-lib})