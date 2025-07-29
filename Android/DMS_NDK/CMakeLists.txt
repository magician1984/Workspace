cmake_minimum_required(VERSION 3.10)

project(qcarcam_dms)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

set(
    OPEN_CV_ROOT
    ${CMAKE_SOURCE_DIR}/third_party/opencv
)

set(SRC_FILES
    src/main.cpp
)

# Add executable
add_executable(qcarcam_dms ${SRC_FILES})

# Include directories
include_directories(
    inc
    api
    ${OPEN_CV_ROOT}/include
)

# Add definitions
add_definitions(-D_ANDROID_ -DC2D_DISABLED -DANDROID_STL=c++_shared -Wno-unused-parameter -Wno-extern-c-compat -DTESTUTIL_VENDOR_LIB -DPRINT_ALL_LOG)

file(GLOB LIBRARIES "${CMAKE_SOURCE_DIR}/libs/*.so")
file(GLOB OPEN_CV_LIBS "${OPEN_CV_ROOT}/libs/*.so")

target_link_libraries(qcarcam_dms
    ${LIBRARIES}
    ${OPEN_CV_LIBS}
    log
    android
)

