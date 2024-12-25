add_library("qcarcam_client" STATIC src/qcarcam_client.cpp)

add_library(qcx_aidl_client SHARED IMPORTED)

set_target_properties(
        qcx_aidl_client
        PROPERTIES
        IMPORTED_LOCATION
        ${PROJECT_SOURCE_DIR}/prebuilt/${CMAKE_ANDROID_ARCH_ABI}/libqcx_aidl_client.so)

include_directories(inc)

include_directories(${PROJECT_SOURCE_DIR}/qcarcam_api/inc)

find_library(EGL_LIB EGL)
find_library(GLES2_LIB GLESv2)

target_link_libraries("qcarcam_client"
        qcx_aidl_client
        ${EGL_LIB}
        ${GLES2_LIB}
)

# Add runtime library search path based on architecture
if(CMAKE_ANDROID_ARCH_ABI STREQUAL "arm64-v8a")
    target_link_options(qcarcam_client PRIVATE "-Wl,-rpath,/vendor/lib64")
elseif(CMAKE_ANDROID_ARCH_ABI STREQUAL "armeabi-v7a")
    target_link_options(qcarcam_client PRIVATE "-Wl,-rpath,/vendor/lib")
endif()