package com.auo.dvr.manager.filemanager

internal data class FileManagerInjector(
    val parser: IFileParser,
    val fileDetector: IFileDetector,
    val repo : IRepo
)