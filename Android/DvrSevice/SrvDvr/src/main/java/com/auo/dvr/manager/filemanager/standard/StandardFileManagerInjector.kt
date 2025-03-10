package com.auo.dvr.manager.filemanager.standard

internal data class StandardFileManagerInjector(
    val processor: IFileProcessor,
    val pool: IFilePool,
    val parser: IFileParser,
    val eventHandler: IEventFileHandler,
    val fileDetector: IFileDetector
)