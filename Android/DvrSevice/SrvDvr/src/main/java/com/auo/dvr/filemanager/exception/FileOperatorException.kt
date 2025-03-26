package com.auo.dvr.filemanager.exception

import com.auo.dvr.filemanager.FileManagerException


class FileOperatorException(method : String, message: String) : FileManagerException("[$method]: $message")