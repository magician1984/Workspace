#include "qcarcam_jni.h"
#include "qcarcam_client.h"
#include <string>
#include <android/log.h>

#include <android/native_window_jni.h>

#define TAG "QCarCamLib_Native"

QCarCamClient *client = nullptr;

extern "C"
JNIEXPORT jint JNICALL
Java_com_auo_qcarcam_QCarCamLibImpl_nativeAttachSurface(JNIEnv *env, jobject thiz,
                                                        jobject surface) {
    __android_log_print(ANDROID_LOG_INFO, TAG, "AttachSurface called");

    // Convert Surface to ANativeWindow
    ANativeWindow* window = ANativeWindow_fromSurface(env, surface);
    if (!window) {
        __android_log_print(ANDROID_LOG_ERROR, TAG, "Failed to get ANativeWindow");
        return -1;
    }

    delete client;

    client = new QCarCamClient();

    client->SetSurface(window);

    return 0;
}


extern "C"
JNIEXPORT jint JNICALL
Java_com_auo_qcarcam_QCarCamLibImpl_nativeDetachSurface(JNIEnv *env, jobject thiz) {
    __android_log_print(ANDROID_LOG_INFO, TAG, "DetachSurface called");

    delete client;

    return 0;
}
extern "C"
JNIEXPORT jint JNICALL
Java_com_auo_qcarcam_QCarCamLibImpl_nativeSwitchMode(JNIEnv *env, jobject thiz, jint mode) {

    if(client){
        client->StartPreview(Mode(mode));
    }

    return 0;
}
extern "C"
JNIEXPORT jint JNICALL
Java_com_auo_qcarcam_QCarCamLibImpl_nativeRotate(JNIEnv *env, jobject thiz, jfloat angle) {
    if(client){
        client->Rotate(angle);
    }
    return 0;
}
extern "C"
JNIEXPORT jint JNICALL
Java_com_auo_qcarcam_QCarCamLibImpl_nativeGetCurrentMode(JNIEnv *env, jobject thiz) {
    // TODO: implement nativeGetCurrentMode()
    if(client){
        return client->GetCurrentMode();
    }
    return -3;
}