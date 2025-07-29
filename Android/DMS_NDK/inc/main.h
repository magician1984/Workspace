#ifndef MAIN
#define MAIN
#include "logging.h"

#define LOG_E(fmt, ...) __printLog(USR_ERROR, fmt, ##__VA_ARGS__)
#define LOG_D(fmt, ...) __printLog(USR_DEBUG, fmt, ##__VA_ARGS__)
#define LOG_I(fmt, ...) __printLog(USR_INFO,  fmt, ##__VA_ARGS__)

#endif /* MAIN */
