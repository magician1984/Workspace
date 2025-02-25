#include <stdlib.h>
#include "qcarcam_client.h"

#include "qcarcam.h"

#define TAG "QCarCamClient"
// Helper macro for logging
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)


QCarCamClient::~QCarCamClient() {
    qcarcam_release();
    screen_release();
}

int QCarCamClient::Rotate(float angleX) {
    return qcarcam_rotate(angleX);
}

QCarCamClient::QCarCamClient():currentMode(Mode::CAM_0), camId(-1), screenHndl(nullptr){
    qcarcam_init();
}

int QCarCamClient::StartPreview(Mode mode) {
    this->currentMode = mode;
    qcarcam_stop();
    qcarcam_query();
    qcarcam_start();
    return 0;
}

int QCarCamClient::SetSurface(ANativeWindow *window) {
    this->screenHndl = window;
    screen_init();
    return 0;
}

int QCarCamClient::qcarcam_init() {
    QCarCamRet_e ret = QCARCAM_RET_OK;
    QCarCamIspUsecaseConfig_t isp_config = { 0 };
    isp_config.usecaseId = (QCarCamIspUsecase_e)16;

    QCarCamInit_t qcarcam_init = { 0 };
    qcarcam_init.apiVersion = QCARCAM_VERSION;

    ret = QCarCamInitialize(&qcarcam_init);
    if (ret != QCARCAM_RET_OK) {
        LOGE("QCarCamInitialize failed %d", ret);
        exit(-1);
    }
    LOGI("QCarCamInitialize done");

    uint32_t input_cnt, input_cnt_filled;
    ret = QCarCamQueryInputs(nullptr, 0, &input_cnt);
    if (ret != QCARCAM_RET_OK || input_cnt == 0) {
        LOGE("QCarCamQueryInputs failed: %d, %d", ret, input_cnt);
        return -1;
    }
    QCarCamInput_t *pInputs = (QCarCamInput_t *)calloc(input_cnt, sizeof(*pInputs));
    ret = QCarCamQueryInputs(pInputs, input_cnt, &input_cnt_filled);
    if (ret != QCARCAM_RET_OK || input_cnt != input_cnt_filled) {
        LOGE("Second QCarCamQueryInputs failed: %d", ret);
        return -1;
    }
    LOGI("QCarCamQueryInputs done");
    return 0;
}

int QCarCamClient::qcarcam_query() {
    return 0;
}

int QCarCamClient::qcarcam_start() {
    return 0;
}

int QCarCamClient::qcarcam_stop() {
    return 0;
}

int QCarCamClient::qcarcam_release() {
    return 0;
}

int QCarCamClient::screen_init() {
    return 0;
}

int QCarCamClient::screen_release() {
    return 0;
}

int QCarCamClient::screen_renderer() {
    return 0;
}

int QCarCamClient::GetCurrentMode() {
    return currentMode;
}

int QCarCamClient::qcarcam_rotate(float angle) {
    return 0;
}

