package com.auo.dvr.manager.filemanager.exception

import com.auo.dvr.manager.filemanager.FileManagerException
import com.auo.dvr.manager.filemanager.standard.FileManagerState

internal class StateFlowException(currentState: FileManagerState, newState : FileManagerState) : FileManagerException("Wrong state flow: ${currentState.name} -> ${newState.name}")
