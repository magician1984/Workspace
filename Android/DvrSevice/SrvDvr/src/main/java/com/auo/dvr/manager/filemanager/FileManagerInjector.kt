package com.auo.dvr.manager.filemanager

import com.auo.dvr.manager.filemanager.repo.CachedFileRepo
import com.auo.dvr.manager.filemanager.repo.ExternalFileRepo

internal data class FileManagerInjector(
    val parser: IFileParser,
    val fileDetector: IFileDetector,
    val cacheRepo: CachedFileRepo,
    val fileRepo: ExternalFileRepo
)