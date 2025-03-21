package com.auo.dvr.manager.filemanager.exception

import com.auo.dvr.manager.filemanager.FileManagerException
import com.auo.dvr.manager.filemanager.standard.FileManagerState

internal class StateAssertionException(currentState: FileManagerState, expectState : FileManagerState, isNot : Boolean = false) : FileManagerException("Wrong state: required: ${if(isNot) "not" else ""} ${expectState.name}, actual: ${currentState.name}")
