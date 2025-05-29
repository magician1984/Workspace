package com.auo.dvr_ui.presentation

import com.auo.dvr_core.DvrConfigure
import com.auo.dvr_core.RecordGroup

sealed class IUserIntents{

    data object ViewProtected : IUserIntents()
    data object ViewNormal : IUserIntents()
    data object ViewLocked : IUserIntents()


    data class SelectGroup(val group : RecordGroup) : IUserIntents()
    data class UnselectGroup(val group : RecordGroup) : IUserIntents()
    data object LockSelectedGroups : IUserIntents()
    data object UnlockSelectedGroups : IUserIntents()
    data object DeleteSelectedGroups : IUserIntents()
    data object ConfirmDeleteFile : IUserIntents()

    data object ConfirmUnmountStorage : IUserIntents()
    data object OpenSettings : IUserIntents()
    data object UnmountStorage : IUserIntents()
    data class UpdateConfigure(val configure: DvrConfigure) : IUserIntents()
    data object RequestPlayFile : IUserIntents()
    data object ReleasePlayFile : IUserIntents()
}