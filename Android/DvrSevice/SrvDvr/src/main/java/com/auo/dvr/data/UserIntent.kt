package com.auo.dvr.data

import com.auo.dvr_core.DvrConfigure
import com.auo.dvr_core.DvrState
import com.auo.dvr_core.RecordFile

sealed interface UserIntent<R> {
    data object GetRecordFiles : UserIntent<List<RecordFile>>
    data object GetState : UserIntent<DvrState>
    data object GetConfig : UserIntent<DvrConfigure>
    data class UpdateConfig(val config: DvrConfigure) : UserIntent<Unit>
    data class LockRecord(val recordFile: RecordFile) : UserIntent<Unit>
    data class UnlockRecord(val recordFile: RecordFile) : UserIntent<Unit>
    data class DeleteRecord(val recordFile: RecordFile) : UserIntent<Unit>
    data class CopyRecord(val recordFile: RecordFile, val destPath: String) : UserIntent<Unit>
    data object UnmountStorage : UserIntent<Unit>
}