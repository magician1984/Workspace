#include "dms_service.h"
#include "main.h"
#include <stdio.h>
#include <signal.h>
#include <thread>
#include <atomic>
#include <unistd.h>
#include <cstdarg>
#include <sys/stat.h>
#include <android/log.h>

#include <opencv2/opencv.hpp>
#include <iostream>

#define DMS_CAM_ID 0
#define DMS_USECASE_ID 16
#define FRAME_PROCESS_COUNT 2
#define FRAME_WIDTH 1440
#define FRAME_HEIGHT 1080

#define LOG_TAG "DMS_NATIVE"
// Print dms_service logs
// #define SHOW_DEV_LOG
// Print executable logs
#define SHOW_LOG

#define DUMP_PATH "/data/local/tmp/dms_dmp"
#define DUMP_SKIP_COUNT 5
#define DUMP_MAX_COUNT 100


std::atomic<bool> gRunning{true};

class FrameProcessor : public FrameProcessorBase
{
public:
    FrameProcessor() : mCount(0), mIndex(0) {
        // Remove dump folder
        std::string command = "rm -rf ";
        command += DUMP_PATH;
        int ret = system(command.c_str());
        if (ret != 0) {
            LOG_E("❌ Failed to remove existing folder: %s", DUMP_PATH);
        } else {
            LOG_D("🗑️  Removed existing folder: %s", DUMP_PATH);
        }

        // Create folder
        if (mkdir(DUMP_PATH, 0755) == 0) {
            LOG_D("📁 Created folder: %s", DUMP_PATH);
        } else {
            LOG_E("❌ Failed to create folder: %s", DUMP_PATH);
        }
    }

    void onFrameInput(FrameData_t frameData) override
    {
        if(mIndex >= DUMP_MAX_COUNT){
            return;
        }

        if(mCount % DUMP_SKIP_COUNT == 0){
            processFrame(FRAME_WIDTH, FRAME_HEIGHT, frameData);
        }
        mCount++;
    }
private:
    uint8_t mCount;
    uint64_t mIndex;

    void processFrame(uint32_t width, uint32_t height, FrameData_t frameData){
        // Build filename: /data/local/tmp/dms_dmp/dump_<timestamp>.jpg
        char filename[256];
        snprintf(filename, sizeof(filename), "%s/dump_%llu.jpg", DUMP_PATH, (unsigned long long)mIndex);

        
        LOG_D("Process frame: w=%u, h=%u, fmt=0x%08x", frameData.width, frameData.height, frameData.colorFmt);
        LOG_D("Frame: size=%u, ptr=%p", frameData.length, frameData.ptr);
        if(!frameData.ptr){
            LOG_E("Data pointer is null!");
            return;
        }

        cv::Mat image(height, width, CV_8UC2, frameData.ptr);
    
        cv::Mat bgrImg;
        cv::cvtColor(image, bgrImg, cv::COLOR_YUV2BGR_UYVY);

        // Save to file
        if (cv::imwrite(filename, bgrImg)) {
            LOG_D("✅ Saved image: %s", filename);
        } else {
            LOG_E("❌ Failed to save image: %s", filename);
        }

        mIndex++;
    }
};

void signalHandler(int signum)
{
    LOG_I("Signal received: %d", signum);
    gRunning = false;
}

void workThread()
{
    LOG_D("Start DMS");

    DmsService::Confugure configure{};
    configure.cameraId = DMS_CAM_ID;
    configure.usecaseId = DMS_USECASE_ID;
    configure.frameProcessStep = FRAME_PROCESS_COUNT;

    FrameProcessor *processor = new FrameProcessor();
    DmsService *service = new DmsService(configure, processor);

    service->start();

    // Loop until stop requested
    while (gRunning)
    {
        usleep(100 * 1000); // 100ms sleep
    }

    service->stop();

    delete service;
    delete processor;
    service = nullptr;

    LOG_D("Work thread exited");
}

void printLog(LogLevel_e lv, const char *fmt, va_list args)
{
    int priority = -1;

#if defined(SHOW_DEV_LOG)
    if (lv == DEV_INFO || lv == DEV_DEBUG || lv == DEV_ERROR) {
        switch (lv) {
            case DEV_INFO:  priority = ANDROID_LOG_INFO;  break;
            case DEV_DEBUG: priority = ANDROID_LOG_DEBUG; break;
            case DEV_ERROR: priority = ANDROID_LOG_ERROR; break;
            default:
                return;
        }
    }
#endif

#if defined(SHOW_LOG)
    if (lv == USR_INFO || lv == USR_DEBUG || lv == USR_ERROR) {
        switch (lv) {
            case USR_INFO:  priority = ANDROID_LOG_INFO;  break;
            case USR_DEBUG: priority = ANDROID_LOG_DEBUG; break;
            case USR_ERROR: priority = ANDROID_LOG_ERROR; break;
            default:
                return;
        }
    }
#endif

    if (priority != -1) {
        __android_log_vprint(priority, LOG_TAG, fmt, args);
    }
}

int main(int argc, char **argv)
{
    setLogFunction(printLog);

    signal(SIGINT, signalHandler); // Handle Ctrl+C

    std::thread worker(workThread); // Run in background

    worker.join(); // Wait for thread to finish
    signal(SIGINT, SIG_DFL);
    return 0;
}
