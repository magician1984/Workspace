//
// Created by bruce on 12/10/24.
//

#ifndef AVM_QCARCAM_CLIENT_H
#define AVM_QCARCAM_CLIENT_H
#include <EGL/egl.h>
#include <GLES2/gl2.h>
#include <android/native_window.h>
#include <android/native_window_jni.h>
#include <android/log.h>

#define TAG "QCarCamClient"
// Helper macro for logging
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)

class QCarCamClient {
private:
    int camId;
    void *screenHndl;

    EGLDisplay eglDisplay;
    EGLSurface eglSurface;
    EGLContext eglContext;
    GLuint program;

    GLuint gTexture;
    GLuint gVertexBuffer;
public:
    QCarCamClient();

    //Release GL
    ~QCarCamClient();

    //memHndl is a ANativeWindow
    //Initialize GL
    int open(int camId, void *memHndl);

    //Start to renderer
    int start();

    // TODO
    int pause();

    // TODO
    int resume();

    // Stop renderer, clean screen
    int stop();
    int rotate(float angleX);
};

#endif //AVM_QCARCAM_CLIENT_H
