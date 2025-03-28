package com.auo.dvr.filemanager

internal data class FileManagerInjector(
    val parser: FileManager.IFileParser,
    val repo : FileManager.IRepo,
    val operator : FileManager.IOperatorMethods,
    val eventHandler : FileManager.IEventHandler
)