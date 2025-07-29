#ifndef LOGGING
#define LOGGING

#include <stdint.h>
#include <stdarg.h>

#define TAG "DMS_SERVICE"

#ifdef __cplusplus
extern "C"{
#endif

typedef enum log_lv{
    DEV_INFO = 0,
    DEV_DEBUG,
    DEV_ERROR,
    USR_INFO,
    USR_DEBUG,
    USR_ERROR
}LogLevel_e;

typedef void (*LogFunc)(LogLevel_e lv, const char* fmt, va_list args);

void setLogFunction(LogFunc funcPtr);

void __printLog(LogLevel_e lv, const char* fmt, ...);

#ifdef __cplusplus
}
#endif

#endif /* LOGGING */
