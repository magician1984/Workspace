add_library("qcarcam_client" STATIC src/mock/qcarcam_client.cpp)

include_directories(inc)

find_library(EGL_LIB EGL)
find_library(GLES2_LIB GLESv2)

target_link_libraries("qcarcam_client"
        glm
        ${EGL_LIB}
        ${GLES2_LIB}
)
