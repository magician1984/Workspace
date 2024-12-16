add_library(${CMAKE_PROJECT_NAME} SHARED
        # List C/C++ source files with relative paths to this CMakeLists.txt.
        src/qcarcam_jni.cpp)

include_directories(inc)

include_directories(${PROJECT_SOURCE_DIR}/qcarcam_client/inc)

# Add these libraries in your CMakeLists.txt or Makefile

target_link_libraries(${CMAKE_PROJECT_NAME} qcarcam_client)

# Specifies libraries CMake should link to your target library. You
# can link libraries from various origins, such as libraries defined in this
# build script, prebuilt third-party libraries, or Android system libraries.
target_link_libraries(${CMAKE_PROJECT_NAME}
        # List libraries link to the target library
        android
        log)