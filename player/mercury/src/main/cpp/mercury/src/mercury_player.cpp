#include <jni.h>
#include <string>
#include <android/log.h>

#include "logger.h"

extern "C" {
#include <libavcodec/avcodec.h>

JNIEXPORT jstring JNICALL
Java_com_coopsrc_oneplayer_kernel_mercury_OneMercuryPlayer_ffmpegVersion(JNIEnv *env, jclass type) {

    std::string version = LIBAVCODEC_IDENT;

    ALOGD("ffmpeg version: %s", version.c_str());

    return env->NewStringUTF(version.c_str());
}
}