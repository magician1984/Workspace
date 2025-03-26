package com.auo.dvr.filemanager.exception

import com.auo.dvr.manager.filemanager.FileManagerException
import com.auo.dvr.RecordFileInstance

internal class FilePoolException(recordFile: RecordFileInstance, message: String) : FileManagerException("File pool control error: $recordFile, $message")