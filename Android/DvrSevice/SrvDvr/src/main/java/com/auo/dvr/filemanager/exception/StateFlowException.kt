package com.auo.dvr.filemanager.exception

import com.auo.dvr.filemanager.FileManagerException
import com.auo.dvr.filemanager.FileManagerState

internal class StateFlowException(currentState: FileManagerState, newState : FileManagerState) : FileManagerException("Wrong state flow: ${currentState.name} -> ${newState.name}")
