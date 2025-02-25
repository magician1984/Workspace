#ifndef CAMERACONFIG_H
#define CAMERACONFIG_H

/**
 * @file cameraconfig.h
 *
 * @brief This header file defines the interface for the CameraConfig library
 *
 * Copyright (c) 2011-2012, 2014, 2016-2024 Qualcomm Technologies, Inc.
 * All Rights Reserved.
 * Confidential and Proprietary - Qualcomm Technologies, Inc.
 *
 */

/* ===========================================================================
                        INCLUDE FILES FOR MODULE
=========================================================================== */
#include <stdint.h>
#include <stdbool.h>

#include "sensorlib.h"

#ifdef __cplusplus
extern "C"
{
#endif // __cplusplus

/* ===========================================================================
                        DATA DECLARATIONS
=========================================================================== */

/** @addtogroup CameraConfig_Constants
@{ */

/* ---------------------------------------------------------------------------
** Constant / Define Declarations
** ------------------------------------------------------------------------ */
/** CameraConfig library name that will be loaded from the device */
#define CAM_CFG_LIBRARY                                "libcamera_config.so"
/** Symbol name that will be used from the CameraConfig library to load the interface.
    It must correspond to the signature of #GetCameraConfigInterfaceType
  */
#define CAM_CFG_ENTRY_INTERFACE                        "GetCameraConfigInterface"

/** CameraConfig library major version number */
#define CAM_CFG_LIB_VERSION_MAJOR                       5
/** CameraConfig library minor version number */
#define CAM_CFG_LIB_VERSION_MINOR                       1


/** max number of SOC configuration definitions */
#define CAM_CFG_MAX_NUM_SOC                             2U
/** max string name length used */
#define CAM_CFG_MAX_NAME_LENGTH                         64U
/** max number of sensor library definitions supported */
#define CAM_CFG_MAX_NUM_SENSOR_LIBS                     8U
/** max number of i2c devices definitions */
#define CAM_CFG_MAX_NUM_I2C_DEVICES                     8U
/** max number of devices defined in each sensor library */
#define CAM_CFG_MAX_NUM_DEVICES                         8U
/** max number of mappings of qcarcam input ID to sensor module */
#define CAM_CFG_MAX_NUM_INPUT_MAPPING                   32U
/** max number of settings override definitions */
#define CAM_CFG_MAX_NUM_SETTINGS_OVERRIDES              64U
/** max number of cams in multi client sesion */
#define CAM_CFG_MC_SESSION_MAX_NUM_CAM                  16U
/** max number of multi client sesion definitions */
#define CAM_CFG_MAX_NUM_MC_SESSIONS                     CAM_CFG_MC_SESSION_MAX_NUM_CAM
/** max number of streams in a multi client sesion */
#define CAM_CFG_MC_SESSIONS_MAX_NUM_STREAM              (QCARCAM_MAX_OUTPUT_STREAMS + QCARCAM_MAX_INPUT_STREAMS)
/** max number of subscribers to a stream */
#define CAM_CFG_MC_SESSIONS_MAX_NUM_SUBCRIBERS          16U
/** max number of subscribers to a stream */
#define CAM_CFG_MC_SESSIONS_CLIENT_ID_MAX               CAM_CFG_MC_SESSIONS_MAX_NUM_SUBCRIBERS
/** max number of PFM streams */
#define  CAM_CFG_MAX_NUM_PFM_STREAMS                     8U
/** maximum number of usecases defined */
#define CAM_CFG_USECASE_MAX_NUM                         32U
/** maximum number of channel mappings for each usecase */
#define CAM_CFG_USECASE_MAPPING_MAX_NUM                 64U
/** max number of modes per input defined */
#define CAM_CFG_MODES_MAX_NUM                           32U
/** max path name size for dump file */
#define CAM_DUMP_PATH_MAX_SIZE                          256U

#define TRUE 1
#define FALSE 0

/* The following is only to be used as a means to override the default CameraConfig by side loading
   an xml file to the device locations mentioned below. Should the xml fail to be parsed or loaded,
   the default CameraConfig library definition will be used.

   @note this is a debug feature and may be disabled in some releases
   */
#if defined(__QNXNTO__)

    #if defined(LINUX_LRH)
    /** camera config xml override file */
    #define CAMERA_CONFIG_XML_FILE   "/var/camera_config.xml"
    /** camera config usecase xml override file */
    #define CAMERA_CONFIG_USECASE_XML_FILE   "/var/camera_config_usecase.xml"
    /** camera config xsd file */
    #define CAMERA_CONFIG_XSD_FILE   "/var/camera_config.xsd"

    #else
    /** camera config xml override file */
    #define CAMERA_CONFIG_XML_FILE   "/var/camera_config.xml"
    /** camera config usecase xml override file */
    #define CAMERA_CONFIG_USECASE_XML_FILE   "/var/camera_config_usecase.xml"
    /** camera config xsd file */
    #define CAMERA_CONFIG_XSD_FILE   "/mnt/bin/camera/camera_config.xsd"

    #endif
#elif defined(__AGL__)

    #if defined(AIS_DATADIR_ENABLE)
    /** camera config xml override file */
    #define CAMERA_CONFIG_XML_FILE "/usr/share/camera/camera_config.xml"
    /** camera config usecase xml override file */
    #define CAMERA_CONFIG_USECASE_XML_FILE   "/usr/share/camera/camera_config_usecase.xml"
    /** camera config xsd file */
    #define CAMERA_CONFIG_XSD_FILE   "/usr/share/camera/camera_config.xsd"

    #elif !defined(AIS_EARLYSERVICE)
    /** camera config xml override file */
    #define CAMERA_CONFIG_XML_FILE   "/data/misc/camera/camera_config.xml"
    /** camera config usecase xml override file */
    #define CAMERA_CONFIG_USECASE_XML_FILE   "/data/misc/camera/camera_config_usecase.xml"
    /** camera config xsd file */
    #define CAMERA_CONFIG_XSD_FILE   "/data/misc/camera/camera_config.xsd"

    #else
    /** camera config xml override file */
    #define CAMERA_CONFIG_XML_FILE   "/usr/bin/camera_config.xml"
    /** camera config usecase xml override file */
    #define CAMERA_CONFIG_USECASE_XML_FILE   "/usr/bin/camera_config_usecase.xml"
    /** camera config xsd file */
    #define CAMERA_CONFIG_XSD_FILE   "/usr/bin/camera/camera_config.xsd"

    #endif
#else //!QNX && !AGL

/** camera config xml override file */
#define CAMERA_CONFIG_XML_FILE   "/vendor/etc/camera/camera_config.xml"
/** camera config usecase xml override file */
#define CAMERA_CONFIG_USECASE_XML_FILE   "/vendor/etc/camera/camera_config_usecase.xml"
/** camera config xsd file */
#define CAMERA_CONFIG_XSD_FILE   "/vendor/etc/camera/camera_config.xsd"

#endif


/** @} */ /* end_addtogroup CameraConfig_Constants */

/* ---------------------------------------------------------------------------
** Type Declarations
** ------------------------------------------------------------------------ */

/** @addtogroup CameraConfig_Datatypes
@{ */

/**
 * @brief This structure describes about camera card info
 */
typedef struct {
    uint32_t chipID;    /**< ChipID of the Soc */
    uint32_t boardType; /**< Board type to identify camera card*/
    uint32_t reserved;  /**< reserved for future use cases*/
} CameraConfigBoardInfo_t;

/**
 * @brief This structure describes I2C Devices
 */
typedef struct {
    CameraI2CType_t i2ctype;   /**< I2C port type CCI or I2C port */
    uint32_t deviceId;         /**< CCI device number */
    uint32_t portId;           /**< I2C port number within device */
    char i2cDevname[CAM_CFG_MAX_NAME_LENGTH];    /**< i2c device node path if applicable*/
} CameraConfigI2CDevice_t;


/**
 * @brief This structure holds sensor lib driver loading information
 */
typedef struct
{
    char strSensorLibraryName[CAM_CFG_MAX_NAME_LENGTH];    /**< The full name of the sensor library shared object. */

    char strSensorLibGetInterfFcn[CAM_CFG_MAX_NAME_LENGTH];/**< Name of the sensor library #SensorLibGetInterface function. */

#ifdef CAM_CFG_BUILD_STATIC_DEVICES
    SensorLibGetInterface pfnSensorLibGetInterf;           /**< Function pointer to the sensor library #SensorLibGetInterface. */
#endif

} CameraConfigSensorLibDriverInfo_t;

/**
 * @brief This structure defines the configurations for a sensor library
 */
typedef struct
{
    uint32_t sensorLibId;     /**< unique sensor lib id */
    uint32_t detectThrdId;    /**< detection thread id to be used for detection */
    CameraConfigSensorLibDriverInfo_t  driverInfo; /**< Sensor Driver loading info */

    uint32_t                    numDevices; /**< number of devices */
    SensorLibDeviceConfig_t     devices[CAM_CFG_MAX_NUM_DEVICES];     /**< Device config */

} CameraConfigSensorLib_t;

/** @brief Dump setting override */
typedef struct
{
    bool isDumpEnabled;                 /**< Enable Dump Diagnostic*/
    char path[CAM_DUMP_PATH_MAX_SIZE];  /**< Path for Diagnostic Dump*/
}DumpSettingOverride_t;


/** @brief Camera setting override */
typedef struct
{
    uint32_t key;     /**< setting key */
    uint32_t value;   /**< setting value */
} CameraSettingOverride_t;

/**
 * @brief This structure defines the mapping of unique qcarcam id to an input sensor
 */
typedef struct
{
    uint32_t qcarcamId;   /**< QCarCam ID to be used by client */

    char   name[CAM_CFG_MAX_NAME_LENGTH]; /**< input name */

    uint32_t sensorLibId;     /**< sensor library Id */
    uint32_t devId;           /**< device Id */
    uint32_t subdevId;        /**< subdevice Id */
    uint8_t  isEepromAvailable;    /**< Flag to represent eeprom availablity */
    bool     earlyBootEnabled;    /**< Flag to represent if early boot is enabled */
} CameraConfigInputMapping_t;

/**
 * @brief This structure defines the mapping of unique buffer list id to a context id and a channel id
 */
typedef struct
{
    uint32_t            bufferlistId;                         ///< bufferlist id
    uint32_t            contextId;                            ///< context id
    uint32_t            channelId;                            ///< channel id
    QCarCamColorFmt_e   colorFmt;                             ///< color format
    uint32_t            openInputIndx;                        ///< input index from the Open command
    uint32_t            inputModeNum;                         ///< input modes num
    uint32_t            inputMode[CAM_CFG_MODES_MAX_NUM];     ///< input mode list
    bool                isStreamSettingSupported;             ///< flag to indicate per stream metadata
}CameraConfigContextChannelConfig_t;

/**
 * @brief This structure defines ContextChannel mappings to a specific usecase
 */
typedef struct
{
    uint32_t usecaseId; 	/**< usecase id */
    uint32_t mappingsNum; 	/**< number of entries in #mappings array */
    /** channel mapping array for each usecase */
    CameraConfigContextChannelConfig_t mappings[CAM_CFG_USECASE_MAPPING_MAX_NUM];
}CameraConfigUsecaseInfo_t;

/**
 * @brief This structure defines usecases list
 */
typedef struct
{
    uint32_t usecasesNum; /**< number of entries in #usecases array */
    CameraConfigUsecaseInfo_t usecases[CAM_CFG_USECASE_MAX_NUM]; /**< usecase information */
}CameraConfigUsecase_t;

/**
 * @brief
 *   Data structure to hold details of Multiclient sesssion meta config
 */
typedef struct
{
    uint32_t bufferlistId;      /**< Bufferlist Id of the metadatactxt */
    uint32_t numBuffers;        /**< number of buffers in the metadtactxt */
    uint32_t size;              /**< size of each buffer in the metadatactxt */
} CameraConfigMCSessionMeta_t;

/**
 * @brief
 *   Data structure to hold details of Multiclient sesssion stream config
 */
typedef struct
{
    uint32_t                            primaryClientId;   /**< Client Id of the primary client of the Stream, range [0:16] */
    uint32_t                            allocatorClientId; /**< Client Id of the client that has privilage to allocate stream buffer, range [0:16] */
    uint32_t                            bufferlistId;      /**< Bufferlist Id of the Streamctxt */
    uint32_t                            numBuffers;        /**< number of buffers in the StreamCtxt */
    uint32_t                            width;             /**< Width of the frame */
    uint32_t                            height;            /**< Height of the frame */
    QCarCamColorFmt_e                   frameFormat;       /**< Color format of the frame */
    uint32_t                            numSubscribers;    /**< Number of Subscribers of the Stream */
    uint32_t                            subscriber[CAM_CFG_MC_SESSIONS_MAX_NUM_SUBCRIBERS];    /**< List of ClientIds of all the Subscriber clients */
    CameraConfigMCSessionMeta_t         metaCfg;          /**< input metadatactxt of the Stream */
} CameraConfigMCSessionStream_t;

/**
 * @brief
 *   Data structure to hold details of Multiclient sesssion camera config
 */
typedef struct
{
    uint32_t                            inputId;            /**< Input identifier of the camera */
    uint32_t                            srcId;              /**< Input source identifier of camera. See #QCarCamInputSrc_t */
    uint32_t                            inputMode;          /**< The input mode id is the index into #QCarCamInputModes_t pModex */
    uint32_t                            primaryClientId;    /**< Client Id of the primary client of the Camera, range [0:16] */
    uint32_t                            numStreams;         /**< Number of streams from the camera */
    CameraConfigMCSessionStream_t       streamCfg[CAM_CFG_MC_SESSIONS_MAX_NUM_STREAM];  /**< List of all streams from camera */
    CameraConfigMCSessionMeta_t         outMetaCfg;         /**< output metadatactxt of camera */
    CameraConfigMCSessionMeta_t         inMetaCfg;          /**< input metadatactxt of camera */
} CameraConfigMCSessionCam_t;

/**
 * @brief
 *   Data structure to hold details of a Multiclient sesssion
 */
typedef struct
{
    uint32_t                            primaryClientId;    /**< Client Id of the primary client of the Session, range [0:16] */
    uint32_t                            usecaseId;          /**< Usecase Id of the session */
    QCarCamOpmode_e                     opMode;             /**< opmode of the session */
    uint32_t                            numCameras;         /**< Number of sensors in the session */
    CameraConfigMCSessionCam_t          camCfg[CAM_CFG_MC_SESSION_MAX_NUM_CAM]; /**< List of Camera ctxt in the session*/
    CameraConfigMCSessionMeta_t         commonMetaCfg;      /**< common metadata ctxt of session*/
} CameraConfigMCSession_t;

/**
 * @brief
 *   Data structure to hold details of a Multiclient list
 */
typedef struct
{
    uint32_t                     numSessions;       /**< Number of multi client sessions */
    CameraConfigMCSession_t      sessionCfg[CAM_CFG_MAX_NUM_MC_SESSIONS]; /**< List of multi client sessions*/
} CameraConfigMultiClient_t;

/**
 * @brief
 *   Data structure to store pfm camera Id and buffer list Id combination
 */
typedef struct
{
    uint32_t cameraId;    /**< Camera Id*/
    uint32_t buffListId;  /**< Buffer list Id*/
} CameraConfigPFMStream_t;

/**
 * @brief
 *   Data structure to store pfm related data
 */
typedef struct
{
    bool                    enablePFM;  /**<Enable PFM or not */
    uint32_t                numStreams;  /**< Number of streams for which PCR sequence needs ro be monitored */
    CameraConfigPFMStream_t streamInfo[CAM_CFG_MAX_NUM_PFM_STREAMS];  /**< List of camera and bufflist ids for PCR sequence monitoring*/
} CameraConfigPFM_t;

/** @brief This structure defines the board specific configurable items related to the
 * camera subsystem.
 */
typedef struct
{
    uint32_t  socId; /**< SOC ID */

    char boardName[CAM_CFG_MAX_NAME_LENGTH];     /**< Board type name */

    uint32_t numI2cDevices;     /**< number of I2C device to be used */
    CameraConfigI2CDevice_t i2cDev[CAM_CFG_MAX_NUM_I2C_DEVICES]; /**< I2C Device configurations */

    uint32_t numCameraLibs; /**< number of sensor libraries to be loaded */
    CameraConfigSensorLib_t cameraLib[CAM_CFG_MAX_NUM_SENSOR_LIBS]; /**< sensor library configurations */

    CameraSettingOverride_t settingsOverrides[CAM_CFG_MAX_NUM_SETTINGS_OVERRIDES];   /**< Settings overrides */

    uint32_t             numInputs;  /**< Number of input mappings */
    CameraConfigInputMapping_t inputs[CAM_CFG_MAX_NUM_INPUT_MAPPING];  /**< Input mappings information */

    CameraConfigMultiClient_t multiclientConfig; /**<  multi client config information */

    CameraConfigUsecase_t   usecase; /**<  usecase related data */

    DumpSettingOverride_t   dumpOverrides;  /**< Dump Diagnostic overrides */

    CameraConfigPFM_t configPFM;  /**< Program Flow Monitor configuration */
} CameraConfigInfo_t;

/** @} */ /* end_addtogroup CameraConfig_Datatypes */

/** @addtogroup CameraConfig_API
@{ */

/**
 * @brief Defines the interface for camera config board specific configurations
 * and methods.
 */
typedef struct
{
    uint32_t  size;         /**< Size of this Library API structure #CameraConfigAPI_t */
    uint32_t  majorVersion; /**< Major version of the Library API #CAM_CFG_LIB_VERSION_MAJOR */
    uint32_t  minorVersion; /**< Minor version of the Library API #CAM_CFG_LIB_VERSION_MINOR */

    /**
     * @brief This method initializes the camera config library
     *
     * @param boardInfo boardInfo for which target we want to retrieve the information
     *
     * @return 0 SUCCESS and -1 otherwise
     */
    int (*CameraConfigInit)(void);

    /**
     * @brief This method deinits the camera config library
     * @return 0 SUCCESS and -1 otherwise
     */
    int (*CameraConfigDeInit)(void);

    /**
     * @brief This method returns the #CameraConfigInfo_t for the specified SOC id.
     *
     * @param  socID   SOC ID for which we wish to retrieve the camera configuration
     *
     * @return #CameraConfigInfo_t Camera configuration information.
     */
    CameraConfigInfo_t const* (*GetCameraConfigInfo)(uint32_t socId);

} CameraConfigAPI_t;

/**
 * @brief Defined prototype for method GetCameraConfigInterface to retrieve CameraConfig interface
 */
typedef CameraConfigAPI_t const* (*GetCameraConfigInterfaceType)(void);

/**
 * @brief This method is used to retrieve the camera board config interface pointer.
 * @return pointer to the interface structure #CameraConfigAPI_t.
 */
CameraConfigAPI_t const* GetCameraConfigInterface(void);

/** @} */ /* end_addtogroup CameraConfig_API */

#ifdef __cplusplus
} // extern "C"
#endif  // __cplusplus

#endif // CAMERACONFIG_H
