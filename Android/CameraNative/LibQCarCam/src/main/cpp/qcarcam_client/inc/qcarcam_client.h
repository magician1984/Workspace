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


enum Mode : int {
    AVM = -2,
    GRID,
    CAM_0,
    CAM_1,
    CAM_2,
    CAM_3
};

class QCarCamClient {
private:
    int camId;
    ANativeWindow *screenHndl;
    Mode currentMode;

    void* qcarcamCtx;

    int qcarcam_init();

    int qcarcam_query();

    int qcarcam_start();

    int qcarcam_stop();

    int qcarcam_release();

    int qcarcam_rotate(float angle);

    int screen_init();

    int screen_release();

    int screen_renderer();

public:
    QCarCamClient();

    int SetSurface(ANativeWindow *window);

    int StartPreview(Mode mode);

    //Release GL
    ~QCarCamClient();

    int Rotate(float angleX);

    int GetCurrentMode();
};

#endif //AVM_QCARCAM_CLIENT_H
