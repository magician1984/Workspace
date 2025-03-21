package com.auo.dvr.manager.filemanager

internal data class FileManagerInjector(
    val processor: IFileProcessor,
    val pool: IFilePool,
    val parser: IFileParser,
    val eventHandler: IEventFileHandler,
    val fileDetector: IFileDetector
)