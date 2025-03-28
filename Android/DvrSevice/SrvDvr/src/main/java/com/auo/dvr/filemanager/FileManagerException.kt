package com.auo.dvr.filemanager

import com.auo.dvr_core.DvrException

abstract class FileManagerException(message : String) : DvrException("FileManager", message)

internal class FileManagerApiException(functionName:String, message : String) : FileManagerException("$functionName: $message")

internal class FileManagerStateFlowException(currentState: FileManagerState, newState : FileManagerState) : FileManagerException("Wrong state flow: ${currentState.name} -> ${newState.name}")

internal class FileManagerStateException(state : FileManagerState, message: String) : FileManagerException("${state.name}: $message")