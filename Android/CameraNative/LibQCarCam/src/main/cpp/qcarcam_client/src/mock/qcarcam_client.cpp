#include "qcarcam_client.h"

#define NUM_OF_TEXTURE 2

GLfloat screenVertices[] = {
        // Positions          // Texture Coords
        -1.0f, 1.0f, 0.0f, 0.0f, 1.0f,  // Top-left
        -1.0f, -1.0f, 0.0f, 0.0f, 0.0f,  // Bottom-left
        1.0f, 1.0f, 0.0f, 1.0f, 1.0f,  // Top-right
        1.0f, -1.0f, 0.0f, 1.0f, 0.0f   // Bottom-right
};

void updateTexture(GLuint *textureID, float r, float g, float b) {
    if (*textureID == 0) {
        glGenTextures(1, textureID);
    }

    unsigned char color[4] = {
            (unsigned char) (r * 255),
            (unsigned char) (g * 255),
            (unsigned char) (b * 255),
            (unsigned char) (255)
    };

    for (int i = 0; i < NUM_OF_TEXTURE; i++) {
        glBindTexture(GL_TEXTURE_2D, *textureID);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, 1920, 1080, 0, GL_RGBA, GL_UNSIGNED_BYTE, color);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

        glBindTexture(GL_TEXTURE_2D, 0);
    }
}

QCarCamClient::QCarCamClient() : camId(-1), screenHndl(nullptr), eglDisplay(EGL_NO_DISPLAY),
                                 eglSurface(EGL_NO_SURFACE), eglContext(EGL_NO_CONTEXT),
                                 gTexture(0), gVertexBuffer(0), program(0) {}

QCarCamClient::~QCarCamClient() {
    stop();
}

int QCarCamClient::open(int camId, void *memHndl) {
    this->camId = camId;
    this->screenHndl = memHndl;

    ANativeWindow *window = static_cast<ANativeWindow *>(memHndl);

    eglDisplay = eglGetDisplay(EGL_DEFAULT_DISPLAY);
    if (eglDisplay == EGL_NO_DISPLAY) {
        __android_log_print(ANDROID_LOG_ERROR, TAG, "Failed to get EGL display");
        ANativeWindow_release(window);
        return -1;
    }

    if (eglInitialize(eglDisplay, nullptr, nullptr) == EGL_FALSE) {
        __android_log_print(ANDROID_LOG_ERROR, TAG, "Failed to initialize EGL");
        ANativeWindow_release(window);
        return -1;
    }

    // Choose EGL config
    EGLConfig eglConfig;
    EGLint numConfigs;
    const EGLint configAttribs[] = {
            EGL_RENDERABLE_TYPE, EGL_OPENGL_ES2_BIT,
            EGL_SURFACE_TYPE, EGL_WINDOW_BIT,
            EGL_BLUE_SIZE, 8,
            EGL_GREEN_SIZE, 8,
            EGL_RED_SIZE, 8,
            EGL_DEPTH_SIZE, 16,
            EGL_NONE
    };

    if (eglChooseConfig(eglDisplay, configAttribs, &eglConfig, 1, &numConfigs) == EGL_FALSE ||
        numConfigs == 0) {
        __android_log_print(ANDROID_LOG_ERROR, TAG, "Failed to choose EGL config");
        ANativeWindow_release(window);
        return -1;
    }

    // Create EGL context
    const EGLint contextAttribs[] = {
            EGL_CONTEXT_CLIENT_VERSION, 2,
            EGL_NONE
    };
    eglContext = eglCreateContext(eglDisplay, eglConfig, EGL_NO_CONTEXT, contextAttribs);
    if (eglContext == EGL_NO_CONTEXT) {
        __android_log_print(ANDROID_LOG_ERROR, TAG, "Failed to create EGL context");
        ANativeWindow_release(window);
        return -1;
    }

    // Create EGL surface
    eglSurface = eglCreateWindowSurface(eglDisplay, eglConfig, window, nullptr);
    if (eglSurface == EGL_NO_SURFACE) {
        __android_log_print(ANDROID_LOG_ERROR, TAG, "Failed to create EGL surface");
        ANativeWindow_release(window);
        return -1;
    }

    // Make the context current
    if (eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext) == EGL_FALSE) {
        __android_log_print(ANDROID_LOG_ERROR, TAG, "Failed to make EGL context current");
        ANativeWindow_release(window);
        return -1;
    }

    // Set the viewport
    EGLint width, height;
    eglQuerySurface(eglDisplay, eglSurface, EGL_WIDTH, &width);
    eglQuerySurface(eglDisplay, eglSurface, EGL_HEIGHT, &height);
    glViewport(0, 0, width, height);



    // Define a basic vertex shader
    const char *vertexShaderSource = R"(
        attribute vec4 aPosition;
        attribute vec2 aTexCoord;
        varying vec2 vTexCoord;

        void main() {
            gl_Position = aPosition;
            vTexCoord = aTexCoord;
        }
    )";

    // Define a basic fragment shader
    const char *fragmentShaderSource = R"(
        precision mediump float;
        uniform sampler2D uTexture;
        varying vec2 vTexCoord;

        void main() {
            gl_FragColor = texture2D(uTexture, vTexCoord);
        }
    )";

    // Compile shaders
    GLuint vertexShader = glCreateShader(GL_VERTEX_SHADER);
    glShaderSource(vertexShader, 1, &vertexShaderSource, nullptr);
    glCompileShader(vertexShader);

    GLuint fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
    glShaderSource(fragmentShader, 1, &fragmentShaderSource, nullptr);
    glCompileShader(fragmentShader);

    // Link shaders into a program
    program = glCreateProgram();
    glAttachShader(program, vertexShader);
    glAttachShader(program, fragmentShader);
    glLinkProgram(program);
    glUseProgram(program);

//    updateTexture(&gTexture, 1.0f, 0.0f, 0.0f);
//
//    __android_log_print(ANDROID_LOG_DEBUG, TAG, "1");
//    GLuint VBO;
//    __android_log_print(ANDROID_LOG_DEBUG, TAG, "1_1");
//    glGenBuffers(1, &VBO);
//    __android_log_print(ANDROID_LOG_DEBUG, TAG, "1_2");
//    glBindBuffer(GL_ARRAY_BUFFER, VBO);
//    __android_log_print(ANDROID_LOG_DEBUG, TAG, "1_3");
//    glBufferData(GL_ARRAY_BUFFER, sizeof(screenVertices), screenVertices, GL_STATIC_DRAW);
//
//    GLuint positionLoc = glGetAttribLocation(program, "aPosition");
//    GLuint texCoorLoc = glGetAttribLocation(program, "aTexCoord");
//    GLint textLoc = glGetAttribLocation(program, "uTexture");
//
//    __android_log_print(ANDROID_LOG_DEBUG, TAG, "1_4");
//    glClear(GL_COLOR_BUFFER_BIT);
//
//    __android_log_print(ANDROID_LOG_DEBUG, TAG, "2");
//    glActiveTexture(GL_TEXTURE0);
//    glBindTexture(GL_TEXTURE_2D, gTexture);
//    glUniform1i(textLoc, 0);
//
//    __android_log_print(ANDROID_LOG_DEBUG, TAG, "3");
//    glBindBuffer(GL_ARRAY_BUFFER, VBO);
//
//    glVertexAttribPointer(positionLoc, 3, GL_FLOAT, GL_FALSE, 5 * sizeof(GLfloat), (GLvoid*)0);
//    glEnableVertexAttribArray(positionLoc);
//
//    glVertexAttribPointer(texCoorLoc, 2, GL_FLOAT, GL_FALSE, 5*sizeof(GLfloat), (GLvoid*)0);
//    glEnableVertexAttribArray(texCoorLoc);
//
//    __android_log_print(ANDROID_LOG_DEBUG, TAG, "4");
//    glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
//    glDisableVertexAttribArray(positionLoc);
//    glDisableVertexAttribArray(textLoc);
//
//    __android_log_print(ANDROID_LOG_DEBUG, TAG, "5");
//    eglSwapBuffers(eglDisplay, eglSurface);

    return 0;
}

int QCarCamClient::start() {


    // Define vertices for a simple triangle
    GLfloat vertices[] = {
            0.0f, 0.5f, 0.0f,  // Top vertex
            -0.5f, -0.5f, 0.0f,  // Bottom-left vertex
            0.5f, -0.5f, 0.0f   // Bottom-right vertex
    };

    // Create vertex buffer and bind data
    GLuint vertexBuffer;
    glGenBuffers(1, &vertexBuffer);
    glBindBuffer(GL_ARRAY_BUFFER, vertexBuffer);
    glBufferData(GL_ARRAY_BUFFER, sizeof(vertices), vertices, GL_STATIC_DRAW);

    // Get attribute location
    GLint positionLocation = glGetAttribLocation(program, "vPosition");
    glVertexAttribPointer(positionLocation, 3, GL_FLOAT, GL_FALSE, 0, (GLvoid *) 0);
    glEnableVertexAttribArray(positionLocation);

    // Render a triangle
    glClearColor(0.0f, 0.0f, 0.0f, 1.0f);  // Black background
    glClear(GL_COLOR_BUFFER_BIT);

    // Draw the triangle
    glDrawArrays(GL_TRIANGLES, 0, 3);

    // Swap buffers
    eglSwapBuffers(eglDisplay, eglSurface);
    return 0;
}

int QCarCamClient::pause() {
    LOGI("Pause not implemented");
    return 0;
}

int QCarCamClient::resume() {
    LOGI("Resume not implemented");
    return 0;
}

int QCarCamClient::stop() {


    return 0;
}

int QCarCamClient::rotate(float angleX) {
    LOGI("Rotate not implemented, angleX: %f", angleX);
    return 0;
}

