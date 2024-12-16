#include "qcarcam_jni.h"
#include "qcarcam_client.h"
#include <string>
#include <android/log.h>

#include <android/native_window_jni.h>
#include <EGL/egl.h>
#include <GLES2/gl2.h>

#define TAG "QCarCamLib_Native"

EGLDisplay eglDisplay = EGL_NO_DISPLAY;
EGLSurface eglSurface = EGL_NO_SURFACE;
EGLContext eglContext = EGL_NO_CONTEXT;

QCarCamClient *client = nullptr;

extern "C"
JNIEXPORT jint JNICALL
Java_com_auo_qcarcam_QCarCamLibImpl_nativeInit(JNIEnv *env, jobject thiz) {
    client = new QCarCamClient();
    return 0;
}
extern "C"
JNIEXPORT jint JNICALL
Java_com_auo_qcarcam_QCarCamLibImpl_nativeRelease(JNIEnv *env, jobject thiz) {
    delete client;
    return 0;
}
extern "C"
JNIEXPORT jint JNICALL
Java_com_auo_qcarcam_QCarCamLibImpl_nativeAttachSurface(JNIEnv *env, jobject thiz,
                                                        jobject surface) {
    __android_log_print(ANDROID_LOG_INFO, TAG, "AttachSurface called");

    // Convert Surface to ANativeWindow
    ANativeWindow* window = ANativeWindow_fromSurface(env, surface);
    if (!window) {
        __android_log_print(ANDROID_LOG_ERROR, TAG, "Failed to get ANativeWindow");
        return -1;
    }

    if(client){
        client->open(0, window);
    }

//    // Initialize EGL
//    eglDisplay = eglGetDisplay(EGL_DEFAULT_DISPLAY);
//    if (eglDisplay == EGL_NO_DISPLAY) {
//        __android_log_print(ANDROID_LOG_ERROR, TAG, "Failed to get EGL display");
//        ANativeWindow_release(window);
//        return -1;
//    }
//
//    if (eglInitialize(eglDisplay, nullptr, nullptr) == EGL_FALSE) {
//        __android_log_print(ANDROID_LOG_ERROR, TAG, "Failed to initialize EGL");
//        ANativeWindow_release(window);
//        return -1;
//    }

//    // Choose EGL config
//    EGLConfig eglConfig;
//    EGLint numConfigs;
//    const EGLint configAttribs[] = {
//            EGL_RENDERABLE_TYPE, EGL_OPENGL_ES2_BIT,
//            EGL_SURFACE_TYPE, EGL_WINDOW_BIT,
//            EGL_BLUE_SIZE, 8,
//            EGL_GREEN_SIZE, 8,
//            EGL_RED_SIZE, 8,
//            EGL_DEPTH_SIZE, 16,
//            EGL_NONE
//    };
//
//    if (eglChooseConfig(eglDisplay, configAttribs, &eglConfig, 1, &numConfigs) == EGL_FALSE || numConfigs == 0) {
//        __android_log_print(ANDROID_LOG_ERROR, TAG, "Failed to choose EGL config");
//        ANativeWindow_release(window);
//        return -1;
//    }
//
//    // Create EGL context
//    const EGLint contextAttribs[] = {
//            EGL_CONTEXT_CLIENT_VERSION, 2,
//            EGL_NONE
//    };
//    eglContext = eglCreateContext(eglDisplay, eglConfig, EGL_NO_CONTEXT, contextAttribs);
//    if (eglContext == EGL_NO_CONTEXT) {
//        __android_log_print(ANDROID_LOG_ERROR, TAG, "Failed to create EGL context");
//        ANativeWindow_release(window);
//        return -1;
//    }
//
//    // Create EGL surface
//    eglSurface = eglCreateWindowSurface(eglDisplay, eglConfig, window, nullptr);
//    if (eglSurface == EGL_NO_SURFACE) {
//        __android_log_print(ANDROID_LOG_ERROR, TAG, "Failed to create EGL surface");
//        ANativeWindow_release(window);
//        return -1;
//    }
//
//    // Make the context current
//    if (eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext) == EGL_FALSE) {
//        __android_log_print(ANDROID_LOG_ERROR, TAG, "Failed to make EGL context current");
//        ANativeWindow_release(window);
//        return -1;
//    }
//
//    // Set the viewport
//    EGLint width, height;
//    eglQuerySurface(eglDisplay, eglSurface, EGL_WIDTH, &width);
//    eglQuerySurface(eglDisplay, eglSurface, EGL_HEIGHT, &height);
//    glViewport(0, 0, width, height);
//
//    // Define vertices for a simple triangle
//    GLfloat vertices[] = {
//            0.0f,  0.5f, 0.0f,  // Top vertex
//            -0.5f, -0.5f, 0.0f,  // Bottom-left vertex
//            0.5f, -0.5f, 0.0f   // Bottom-right vertex
//    };
//
//    // Define a basic vertex shader
//    const char* vertexShaderSource = R"(
//        attribute vec4 vPosition;
//        void main() {
//            gl_Position = vPosition;
//        }
//    )";
//
//    // Define a basic fragment shader
//    const char* fragmentShaderSource = R"(
//        precision mediump float;
//        void main() {
//            gl_FragColor = vec4(0.0, 1.0, 0.0, 1.0);  // Green color
//        }
//    )";
//
//    // Compile shaders
//    GLuint vertexShader = glCreateShader(GL_VERTEX_SHADER);
//    glShaderSource(vertexShader, 1, &vertexShaderSource, nullptr);
//    glCompileShader(vertexShader);
//
//    GLuint fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
//    glShaderSource(fragmentShader, 1, &fragmentShaderSource, nullptr);
//    glCompileShader(fragmentShader);
//
//    // Link shaders into a program
//    GLuint program = glCreateProgram();
//    glAttachShader(program, vertexShader);
//    glAttachShader(program, fragmentShader);
//    glLinkProgram(program);
//    glUseProgram(program);
//
//    // Create vertex buffer and bind data
//    GLuint vertexBuffer;
//    glGenBuffers(1, &vertexBuffer);
//    glBindBuffer(GL_ARRAY_BUFFER, vertexBuffer);
//    glBufferData(GL_ARRAY_BUFFER, sizeof(vertices), vertices, GL_STATIC_DRAW);
//
//    // Get attribute location
//    GLint positionLocation = glGetAttribLocation(program, "vPosition");
//    glVertexAttribPointer(positionLocation, 3, GL_FLOAT, GL_FALSE, 0, (GLvoid*)0);
//    glEnableVertexAttribArray(positionLocation);
//
//    // Render a triangle
//    glClearColor(0.0f, 0.0f, 0.0f, 1.0f);  // Black background
//    glClear(GL_COLOR_BUFFER_BIT);
//
//    // Draw the triangle
//    glDrawArrays(GL_TRIANGLES, 0, 3);
//
//    // Swap buffers
//    eglSwapBuffers(eglDisplay, eglSurface);

    // Clean up
//    ANativeWindow_release(window);

    return 0;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_auo_qcarcam_QCarCamLibImpl_nativeStart(JNIEnv *env, jobject thiz) {
    __android_log_print(ANDROID_LOG_INFO, TAG, "Start called");
    if(client){
        client->start();
    }
    return 0;
}
extern "C"
JNIEXPORT jint JNICALL
Java_com_auo_qcarcam_QCarCamLibImpl_nativeResume(JNIEnv *env, jobject thiz) {
    __android_log_print(ANDROID_LOG_INFO, TAG, "Resume called");
    return 0;
}
extern "C"
JNIEXPORT jint JNICALL
Java_com_auo_qcarcam_QCarCamLibImpl_nativePause(JNIEnv *env, jobject thiz) {
    __android_log_print(ANDROID_LOG_INFO, TAG, "Pause called");
    return 0;
}
extern "C"
JNIEXPORT jint JNICALL
Java_com_auo_qcarcam_QCarCamLibImpl_nativeStop(JNIEnv *env, jobject thiz) {
    __android_log_print(ANDROID_LOG_INFO, TAG, "Stop called");
    return 0;
}
extern "C"
JNIEXPORT jint JNICALL
Java_com_auo_qcarcam_QCarCamLibImpl_nativeDetachSurface(JNIEnv *env, jobject thiz) {
    __android_log_print(ANDROID_LOG_INFO, TAG, "DetachSurface called");
    return 0;
}