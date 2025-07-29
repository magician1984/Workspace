#!/bin/bash

NDK_PATH=/mnt/Workspace/.Android/Sdk/ndk/28.0.12674087
API=34
ABI=arm64-v8a
BUILD_DIR=build

cmake -B $BUILD_DIR \
    -DCMAKE_TOOLCHAIN_FILE=$NDK_PATH/build/cmake/android.toolchain.cmake \
    -DANDROID_ABI=$ABI \
    -DANDROID_PLATFORM=android-$API \
    -DANDROID_STL=c++_static \
    -DCMAKE_BUILD_TYPE=Release

cmake --build $BUILD_DIR -j$(nproc)
