package com.auo.dvr.manager.filemanager.standard.exception

import com.auo.dvr.manager.filemanager.FileManagerException
import com.auo.dvr.manager.filemanager.RecordFileInstance

internal class FilePoolException(recordFile: RecordFileInstance, message: String) : FileManagerException("File pool control error: $recordFile, $message")