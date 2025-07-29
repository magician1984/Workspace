#ifndef DMS_SERVICE
#define DMS_SERVICE

#include <stdint.h>
#include <string>
#include <cstdint>

/**
 * @brief Structure representing a single camera frame.
 */
struct frame_data
{
    uint32_t width;
    uint32_t height;
    uint32_t colorFmt;
    uint64_t length;          ///< Length of the frame data in bytes.
    void *ptr;                ///< Pointer to the raw frame buffer (e.g., NV21).
    uint64_t timestampNano;   ///< Timestamp in nanoseconds.
};

typedef frame_data FrameData_t;

/**
 * @brief Abstract base class for frame processing.
 *
 * Users can inherit from this class and override the `onFrameInput()` method
 * to receive frame data from the DmsService.
 *
 * Example:
 * ```cpp
 * class MyProcessor : public FrameProcessorBase {
 * public:
 *     void onFrameInput(FrameData_t frameData) override {
 *         // Process frame data here
 *     }
 * };
 * ```
 */
class FrameProcessorBase
{
public:
    virtual void onFrameInput(FrameData_t frameData) = 0;
    virtual ~FrameProcessorBase() = default;
};

/**
 * @brief DMS (Driver Monitoring Service) controller class.
 *
 * This service handles camera initialization and frame streaming.
 * Users can provide a custom `FrameProcessorBase` implementation
 * to receive frames for processing.
 *
 * Example usage:
 * ```cpp
 * DmsService::Confugure config;
 * config.cameraId = 0;
 * config.usecaseId = 16;
 *
 * MyProcessor *processor = new MyProcessor();
 * DmsService *service = new DmsService(config, processor);
 * service->start();
 * // ...
 * service->stop();
 * delete service;
 * delete processor;
 * ```
 */
class DmsService
{
public:
    /**
     * @brief Configuration structure for initializing the service.
     */
    struct Confugure
    {
        uint32_t cameraId;    ///< ID of the camera to use.
        uint32_t usecaseId;   ///< Usecase ID as defined by the platform.
        uint8_t frameProcessStep = 2;    /// How offen a frame is processed. (the camera input is 30 fps)
    };

    /**
     * @brief Constructor.
     * @param configure Camera and usecase configuration.
     * @param frameProcessor User-defined frame processor.
     */
    DmsService(Confugure configure, FrameProcessorBase *frameProcessor);

    /**
     * @brief Destructor.
     */
    ~DmsService();

    /**
     * @brief Starts the DMS service and begins frame streaming.
     */
    void start();

    /**
     * @brief Stops the DMS service and releases resources.
     */
    void stop();

private:
    struct Params;
    Params *mParams;
};

#endif /* DMS_SERVICE */
