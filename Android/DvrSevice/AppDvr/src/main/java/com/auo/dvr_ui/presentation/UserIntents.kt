package com.auo.dvr_ui.presentation

import com.auo.dvr_core.CamLocation
import com.auo.dvr_core.DvrConfigure
import com.auo.dvr_core.RecordDuration
import com.auo.dvr_core.RecordResolution
import com.auo.dvr_ui.entity.RecordFileData

sealed class IUserIntents{
    data object ViewProtected : IUserIntents()
    data object ViewNormal : IUserIntents()
    class ViewCameraLocation(val camLocation: CamLocation) : IUserIntents(){
        override fun toString(): String {
            return "ViewCameraLocation(camLocation=$camLocation)"
        }
    }
    data class SelectFile(val file : RecordFileData) : IUserIntents()
    data object UnselectFile : IUserIntents()
    data class LockFile(val file : RecordFileData) : IUserIntents()
    data class UnlockFile(val file : RecordFileData) : IUserIntents()
    data class DeleteFile(val file : RecordFileData) : IUserIntents()
    data class ConfirmDeleteFile(val file : RecordFileData) : IUserIntents()
    data object ConfirmUnmountStorage : IUserIntents()
    data object OpenSettings : IUserIntents()
    data object UnmountStorage : IUserIntents()
    data class UpdateConfigure(val configure: DvrConfigure) : IUserIntents()
    data object ReplayFile : IUserIntents()
}