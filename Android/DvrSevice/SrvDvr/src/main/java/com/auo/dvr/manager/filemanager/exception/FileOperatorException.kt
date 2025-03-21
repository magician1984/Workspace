package com.auo.dvr.manager.filemanager.exception

import com.auo.dvr.manager.filemanager.FileManagerException

class FileOperatorException(method : String, message: String) : FileManagerException("[$method]: $message")