package com.auo.dvr.manager.filemanager.standard.exception

import com.auo.dvr.manager.filemanager.FileManagerException
import java.io.File

internal class EventHandleException(val eventTime : Long, val outputFile : File) : FileManagerException("Event handle error: $eventTime, $outputFile")