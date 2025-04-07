package com.auo.dvr_ui.presentation

import com.auo.dvr_core.CamLocation
import com.auo.dvr_ui.entity.RecordFileData

sealed class IUserIntents{
    data object ViewProtected : IUserIntents()
    data object ViewNormal : IUserIntents()
    class ViewCameraLocation(val camLocation: CamLocation) : IUserIntents(){
        override fun toString(): String {
            return "ViewCameraLocation(camLocation=$camLocation)"
        }
    }
    class SelectFile(val file : RecordFileData) : IUserIntents()
    data object UnselectFile : IUserIntents()
    class LockFile(val file : RecordFileData) : IUserIntents()
    class UnlockFile(val file : RecordFileData) : IUserIntents()
    class DeleteFile(val file : RecordFileData) : IUserIntents()
    class ConfirmDeleteFile(val file : RecordFileData) : IUserIntents()
    data object ReplayFile : IUserIntents()
}