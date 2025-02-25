/**
 * @file sensorlib.h
 *
 * Copyright (c) 2022-2024 Qualcomm Technologies, Inc.
 * All Rights Reserved.
 * Confidential and Proprietary - Qualcomm Technologies, Inc.
 */

#ifndef SENSORLIB_H
#define SENSORLIB_H

#include "sensorsdk.h"

/** @addtogroup SensorLib_API
@{ */

/** @brief Sensor Lib Interface major version type. */
#define SENSOR_LIB_VERSION_MAJOR    1U

/** @brief Sensor Lib Interface minor version type. */
#define SENSOR_LIB_VERSION_MINOR    2U


/** @brief Sensor Platform API
 * Function pointer table for platform functions such as i2c operations and gpio operations.
 */
typedef struct {
    /** @brief I2C read array operation
     *
     * @param pHndl         Sensor lib handle obtained through Open()
     * @param pI2cDev       Specifies I2C device
     * @param pI2cSetting   Specified I2C operation settings for each read
     * @param pRegs         Pointer to I2C register read sequence.
     * @param size          Number of #CameraI2CReg_t in sequence
     *
     * @return #SensorRet_e
     */
    SensorRet_e (*I2CRead)(SensorLibHndl_t pHndl,
            const CameraI2CDevice_t * const pI2cDev,
            const CameraI2CSettings_t * const pI2cSetting,
            CameraI2CReg_t * const pRegs,
            const uint32_t size);

    /** @brief I2C write array operation
     *
     * @param pHndl         Sensor lib handle obtained through Open()
     * @param pI2cDev       Specifies I2C device
     * @param pI2cSetting   Specified I2C operation settings for each write
     * @param pRegs         Pointer to I2C register write sequence
     * @param size          Number of #CameraI2CReg_t in sequence
     *
     * @return #SensorRet_e
     */
    SensorRet_e (*I2CWrite)(SensorLibHndl_t pHndl,
            const CameraI2CDevice_t * const pI2cDev,
            const CameraI2CSettings_t * const pI2cSetting,
            const CameraI2CReg_t * const pRegs,
            const uint32_t size);

    /** @brief I2C burst read/write operation
     *
     * @param pHndl         Sensor lib handle obtained through Open()
     * @param pI2cDev       Specifies I2C device
     * @param pI2cTransact  Specifies I2C transaction details
     *
     * @return #SensorRet_e
     */
    SensorRet_e (*I2CTransact)(SensorLibHndl_t pHndl,
            const CameraI2CDevice_t * const pI2cDev,
            CameraI2CTransact_t * const pI2cTransact);

    /** @brief I2C write on sync event
     *
     * @param pHndl         Sensor lib handle obtained through Open()
     * @param pSyncCfg      Specifies I2C device and sync event trigger
     * @param pRegSettings  I2C write sequence on sync event
     * @param size          Number of i2c write sequences
     *
     * @return #SensorRet_e
     */
    SensorRet_e  (*I2CWriteSync)(SensorLibHndl_t pHndl,
            const CameraI2CDeviceSyncCfg_t * const pSyncCfg,
            const CameraI2CSyncSequence_t * const pRegSettings,
            const uint32_t size);

    /** @brief Executes sequence of GPIO and/or Power commands
     *
     * @param pHndl             Sensor lib handle obtained through Open()
     * @param pPowerSequence    Sequence of GPIO and/or power commands
     * @param size              Size of sequence
     *
     * @return #SensorRet_e
     */
    SensorRet_e (*ExecuteGpioAndPowerSequence)(SensorLibHndl_t pHndl,
            const CameraPowerSequence_t * const pPowerSequence,
            uint16_t size);

    /** @brief Sets GPIO pin with specified value
     *
     * @param pHndl     Sensor lib handle obtained through Open()
     * @param pGpioCmd  Specifies Gpio pin and value
     *
     * @return #SensorRet_e
     */
    SensorRet_e (*GPIOSetValue)(SensorLibHndl_t pHndl,
            const CameraGpioCommand_t * const pGpioCmd);

    /** @brief Read GPIO pin value
     *
     * @param pHndl     Sensor lib handle obtained through Open()
     * @param pGpioCmd  Specifies Gpio pin. The value is filled by the function.
     *
     * @return #SensorRet_e
     */
    SensorRet_e (*GPIOGetValue)(SensorLibHndl_t pHndl,
            CameraGpioCommand_t * const pGpioCmd);

    /** @brief Setup GPIO interrupt with callback function
     *
     * @param pHndl     Sensor lib handle obtained through Open()
     * @param pGpioCmd  Specifies Gpio interrupt pin and callback
     *
     * @return #SensorRet_e
     */
    SensorRet_e (*SetupGpioInterrupt)(SensorLibHndl_t pHndl,
            const CameraGpioIntrSetup_t * const pIntr);

    /** @brief Deinit GPIO interrupt with callback function
     *
     * @param pHndl     Sensor lib handle obtained through Open()
     * @param pGpioCmd  Specifies Gpio interrupt pin and callback
     *
     * @return #SensorRet_e
     */
    SensorRet_e (*DeinitGpioInterrupt)(SensorLibHndl_t pHndl,
            const CameraGpioIntrSetup_t * const pIntr);


    /** @brief Start CCI Frame Sync at particular frequency
     *
     * @param pHndl     Sensor lib handle obtained through Open()
     * @param pFsync    Specifies device GPIO and frame sync frequency
     *
     * @return #SensorRet_e
     */
    SensorRet_e (*StartCCIFrameSync)(SensorLibHndl_t pHndl,
            const CameraCCIFrameSyncSetup_t * const pFsync);

    /** @brief Start CCI timer commands
     *
     * @param pHndl     Sensor lib handle obtained through Open()
     * @param pCommand  Specifies set of commands needed
     *
     * @return #SensorRet_e
     */
    SensorRet_e (*StartTimerCommands)(SensorLibHndl_t pHndl,
            const SensorTimerCommandSetup_t * const pCommand);

    /** @brief Stops CCI Frame Sync.
     *      The GPIO must have been setup using #StartCCIFrameSync
     *
     * @param pHndl     Sensor lib handle obtained through Open()
     * @param devId     Sensor lib device ID
     * @param gpioId    GPIO ID that that will be started
     *
     * @return #SensorRet_e
     */
    SensorRet_e (*StopCCIFrameSync)(SensorLibHndl_t pHndl,
            uint8_t devId,
            CameraGpioType_e  gpioId);

    /** @brief Stops CCI Timer commands
     *      The CCI Timers must have been started using #StartTimerCommands
     *
     * @param pHndl     Sensor lib handle obtained through Open()
     * @param cciStopTimerinfo  bitmask of CCI timers to stop
     *
     * @return #SensorRet_e
     */
    SensorRet_e (*StopTimerCommands)(SensorLibHndl_t pHndl,
            uint32_t cciStopTimerinfo);

    /** @brief  Perform a CSI input hardware operation
     *
     * @param pHndl     Sensor lib handle obtained through Open()
     * @param params    Structure specifying hardware identifiers and operation parameters
     *
     * @return #SensorRet_e
     */
    SensorRet_e (*CSICommand)(SensorLibHndl_t pHndl, const CameraCSISyncParams_t * params);

}SensorPlatformAPI_t;

/** @brief Sensor Lib Open parameters */
typedef struct
{
    uint32_t sensorLibId;                       /**< Sensor lib ID */
    const SensorPlatformAPI_t * pPlatformFunc;  /**< Platform function table */
    uint32_t numDevices;                        /**< Number of device configration entries */
    const SensorLibDeviceConfig_t * pDevConfig; /**< Sensor lib device configuration */
} SensorLibOpenParams_t;


/** @brief Event callback function prototype from driver
 *
 * @param pHndl     Sensor lib handle obtained through Open()
 * @param event     Sensor lib event
 * @param pPayload  Event payload
 * @param pPrivateData  Private data pointer registered through RegisterCallback()
 *
 * @return #SensorRet_e
 */
typedef SensorRet_e (*SensorLibEventCallback_t)(SensorLibHndl_t pHndl, SensorLibEvent_e event,
        const SensorLibEventPayload_t *pPayload, void* pPrivateData);

/** @brief Sensor Lib command definition. */
typedef enum
{
    /** @brief Power up/down, power suspend/resume specific subdevice

     Input parameter:
      #SensorLibPowerCtrl_t

     Output parameter:
      None
     */
    SENSORLIB_CMD_POWER_CTRL,

    /** @brief Stream on/off specific subdevice

     Input parameter:
      #SensorLibStreamCtrl_t

     Output parameter:
      None
     */
    SENSORLIB_CMD_STREAM_CTRL,

    /** @brief Detect devices/subdevices that are present

     This command is asynchronous and the library should send event callbacks
       when a device and subdevices are detected.

     @note As part of the detection stage, the implementation may require the initialization
          of the bridge chip and sensors (e.g. power on/off of the deserializer and sensors, remapping of i2c, etc...)

     Input parameter:
      None

     Output parameter:
      None
     */
    SENSORLIB_CMD_DETECT,

    /** @brief Enumerates devices

     Input parameter:
      None

     Output parameter:
      #SensorLibEnumDevices_t
     * */
    SENSORLIB_CMD_ENUM_DEVICES,

    /** @brief Enumerates subdevices of a specific device

     Input parameter:
      #SensorLibEnumSubDevices_t with device id to enumerate

     Output parameter:
      #SensorLibEnumSubDevices_t
     * */
    SENSORLIB_CMD_ENUM_SUBDEVICES,

    /** @brief Set subdevice to specific mode

     Input parameter:
      #SensorLibSubdeviceModeCtrl_t

     Output parameter:
      None
     * */
    SENSORLIB_CMD_SET_SUBDEVICE_MODE,

    /** @brief Get current subdevice mode

     Input parameter:
      #SensorLibSubdeviceModeCtrl_t with subdev id

     Output parameter:
      #SensorLibSubdeviceModeCtrl_t that will be filled
     * */
    SENSORLIB_CMD_GET_SUBDEVICE_MODE,

    /** @brief Get CSI TX parameters of a specific device's CSI

     Input parameter:
      #SensorLibCsiTxParams_t with dev/csi index to query

     Output parameter:
      #SensorLibCsiTxParams_t
     */
    SENSORLIB_CMD_GET_CSI_PARAMS,

    /** @brief Set QCarCam Sensor parameters

     Input parameter:
      #SensorLibQCarCamSetParam_t

     Output parameter:
      None
     */
    SENSORLIB_CMD_SET_PARAM,

    /** @brief Get QCarCam Sensor parameters

     Input parameter:
      #SensorLibQCarCamGetParam_t with dev/subdev and param ID

     Output parameter:
      #SensorLibQCarCamGetParam_t that will be filled
     */
    SENSORLIB_CMD_GET_PARAM,

    /** @brief Process raw frame called on frame ready.
           This function is called synchronously and should run minimally as it will incur
           added latency to frame delivery

     Input parameter:
      #SensorLibProcessFrame_t

     Output parameter:
      #QCarCamFrameInfo_t
     */
    SENSORLIB_CMD_PROCESS_FRAME,

    /** @brief Query interlaced field type

     Input parameter:
      #SensorLibQueryInterlacedField_t with stream information

     Output parameter:
      #SensorLibQueryInterlacedField_t that will be filled
     * */
    SENSORLIB_CMD_QUERY_INTERLACED_FIELD,

    /** @brief Process recover of degraded components

     Input parameter:
      None

     Output parameter:
      None
     */
    SENSORLIB_CMD_RECOVER,

    /** @brief Payload to notify sensor library which stream is going
      through which CSID core.

      This is relevant in case the sensor library wishes to perform an I2C or GPIO operation
      with a trigger from the CSID.

     Input parameter:
      #SensorLibNotifyCsiInfo_t

     Output parameter:
      None
     */
    SENSORLIB_NOTIFY_CSI_INFO,

    /** @brief I2C Payload for sensor library to be applied to sensor

    Input parameter:
     #SensorLibSubmitI2CPacket_t

    Output parameter:
     None
    */
    SENSORLIB_CMD_SUBMIT_I2C_PACKET,

    /** @brief Start of Frame (SOF) notification to to the sensor library

    The Start of Frame (SOF) notification is forwarded to the sensor library for a specific
    vc stream.

    Input parameter:
     #SensorLibNotifySOF_t

    Output parameter:
     None
    */
    SENSORLIB_CMD_NOTIFY_SOF,

    /** @brief I2C Payload for eeprom read

    Input parameter: #SensorLibSubmitI2CPacket_t

    Output parameter: None
    */
    SENSORLIB_CMD_READ_EEPROM,

    /** @brief Update IFE Mapping Info to SensorLib

    @note this is similar to SENSORLIB_NOTIFY_CSI_INFO but uses vc/dt instead of srcId

    This is primarly for the sensor library to be aware of which CSID core is
     used for a specific vc stream. The CSID core parameter is useful
     if the library wishes to setup a GPIO or i2c operation triggered by
     a CSID event.

    Input parameter:
     #SensorLibNotifyCsiInfoWithVcDt_t

    Output parameter:
     None
    * */
    SENSORLIB_NOTIFY_CSI_INFO_WITH_VC_DT,

    /** @brief Dumps Diagnostic Information to logger

    Input parameter:
     None

    Output parameter:
     None
    */
    SENSORLIB_CMD_DIAGNOSTIC_DUMP,

    /** @brief A message to clear the CSI info for the given device handle that was previsouly
               set with #SENSORLIB_NOTIFY_CSI_INFO_WITH_VC_DT. It will be called when CSI information
               is no longer valid.

    Input parameter:
     #SensorLibClearCsiInfo_t

    Output parameter:
     None
    */
    SENSORLIB_CLEAR_CSI_INFO,

    /** @brief Command to Detect and Init Hotplug camera

    Input parameter:
     SensorLibHotPlugDetect_t

    Output parameter:
     None
     * */
    SENSORLIB_CMD_HOTPLUG_DETECT
} SensorLibCmd_e;

/** @brief APIs in sensor library */
typedef struct
{
    uint32_t  size;         /**< Size of this Library API structure */
    uint32_t  majorVersion; /**< Major version of the Library API   */
    uint32_t  minorVersion; /**< Minor version of the Library API   */

    /** @brief Open Library instance
     *
     * @param pOpen   Open parameters
     *
     * @return Sensor lib handle #SensorLibHndl_t representing instance of library
     */
    SensorLibHndl_t (*Open)(const SensorLibOpenParams_t* pOpen);

    /** @brief Close Library instance
     *
     * @param pHndl     Sensor library handle
     *
     * @return #SensorRet_e
     */
    SensorRet_e (*Close)(SensorLibHndl_t pHndl);

    /** @brief IO Control API to send commands to the sensor library
     *
     * @param pHndl     Sensor lib handle obtained through Open()
     * @param cmdId     Command of type #SensorLibCmd_e
     * @param pIn       Pointer to command input data
     * @param inLength  Size in bytes of command input data
     * @param pOut      Pointer to command output data
     * @param outLength Size in bytes of command output data
     *
     * @return #SensorRet_e
     */
    SensorRet_e (*Ioctl)(SensorLibHndl_t pHndl,
                const uint32_t   cmdId,
                const void      *pIn,
                const size_t     inLength,
                void            *pOut,
                const size_t     outLength);

    /** @brief Register Callback with sensor library instance
     *
     * @param pHndl         Sensor lib handle obtained through Open()
     * @param pEventCb      Event callback fucntion pointer
     * @param pPrivateData  Private data pointer that will be passed as argument to pEventCb
     *
     * @return #SensorRet_e
     */
    SensorRet_e (*RegisterCallback)(SensorLibHndl_t pHndl, SensorLibEventCallback_t pEventCb,  void* pPrivateData);
} SensorLibraryAPI_t;


/** @brief Defined type for method to get the sensor lib interface.
 *
 * @return  #SensorLibraryAPI_t struct providing the API interface pointers
 */
typedef SensorLibraryAPI_t const* (*SensorLibGetInterface)(void);

/** @} */ /* end_addtogroup SensorLib_API */

#endif /* __SENSORLIB_H__ */
