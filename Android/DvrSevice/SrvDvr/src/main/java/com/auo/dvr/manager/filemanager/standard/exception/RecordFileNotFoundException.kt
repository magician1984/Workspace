package com.auo.dvr.manager.filemanager.standard.exception

import com.auo.dvr.manager.filemanager.FileManagerException
import com.auo.dvr_core.RecordFile

internal class RecordFileNotFoundException(recordFile: RecordFile) : FileManagerException("Record file not found: ${recordFile.name}")