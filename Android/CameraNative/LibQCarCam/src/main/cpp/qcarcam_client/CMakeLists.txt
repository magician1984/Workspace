add_library("qcarcam_client" STATIC src/qcarcam_client.cpp)

add_library(utils SHARED IMPORTED)

set_target_properties(
        utils
        PROPERTIES
        IMPORTED_LOCATION
        ${PROJECT_SOURCE_DIR}/qcarcam_api/prebuild/${CMAKE_ANDROID_ARCH_ABI}/libutils.so
)


add_library(cpp SHARED IMPORTED)

set_target_properties(
        cpp
        PROPERTIES
        IMPORTED_LOCATION
        ${PROJECT_SOURCE_DIR}/qcarcam_api/prebuild/${CMAKE_ANDROID_ARCH_ABI}/libc++.so
)

add_library(base SHARED IMPORTED)

set_target_properties(
        base
        PROPERTIES
        IMPORTED_LOCATION
        ${PROJECT_SOURCE_DIR}/qcarcam_api/prebuild/${CMAKE_ANDROID_ARCH_ABI}/libbase.so
)

add_library(cutils SHARED IMPORTED)

set_target_properties(
        cutils
        PROPERTIES
        IMPORTED_LOCATION
        ${PROJECT_SOURCE_DIR}/qcarcam_api/prebuild/${CMAKE_ANDROID_ARCH_ABI}/libcutils.so
)

add_library(aosal SHARED IMPORTED)

set_target_properties(
        aosal
        PROPERTIES
        IMPORTED_LOCATION
        ${PROJECT_SOURCE_DIR}/qcarcam_api/prebuild/${CMAKE_ANDROID_ARCH_ABI}/libaosal.so
)

add_library(qcxosal SHARED IMPORTED)

set_target_properties(
        qcxosal
        PROPERTIES
        IMPORTED_LOCATION
        ${PROJECT_SOURCE_DIR}/qcarcam_api/prebuild/${CMAKE_ANDROID_ARCH_ABI}/libqcxosal.so)

add_library(qcxclient SHARED IMPORTED)

set_target_properties(
        qcxclient
        PROPERTIES
        IMPORTED_LOCATION
        ${PROJECT_SOURCE_DIR}/qcarcam_api/prebuild/${CMAKE_ANDROID_ARCH_ABI}/libqcxclient.so)



include_directories(inc)

include_directories(${PROJECT_SOURCE_DIR}/qcarcam_api/inc)

find_library(EGL_LIB EGL)
find_library(GLES2_LIB GLESv2)

target_link_libraries("qcarcam_client"
        utils
        cpp
        base
        cutils
        aosal
        qcxosal
        qcxclient
        ${EGL_LIB}
        ${GLES2_LIB}
)
