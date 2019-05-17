#include <jni.h>
#include <string>

#define LOG_TAG "OnePlayerNative"
#define ALOGV(...) ((void)__android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, __VA_ARGS__))
#define ALOGD(...) ((void)__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__))
#define ALOGI(...) ((void)__android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__))
#define ALOGW(...) ((void)__android_log_print(ANDROID_LOG_WARN, LOG_TAG, __VA_ARGS__))
#define ALOGE(...) ((void)__android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__))

extern "C" {
#include <libavcodec/avcodec.h>

JNIEXPORT jstring JNICALL
Java_com_coopsrc_oneplayer_kernel_ffmedia_OneFFMedia_ffmpegVersion(JNIEnv *env, jclass type) {

    std::string version = LIBAVCODEC_IDENT;

    return env->NewStringUTF(version.c_str());
}
}