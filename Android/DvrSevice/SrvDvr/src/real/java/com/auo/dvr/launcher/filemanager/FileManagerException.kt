package com.auo.dvr.launcher.filemanager

import com.auo.dvr_core.DvrException

abstract class FileManagerException(message : String) : DvrException("FileManager", message)

internal class FileManagerApiException(functionName:String, message : String) : FileManagerException("$functionName: $message")