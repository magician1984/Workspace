package com.auo.dvr.filemanager

internal data class FileManagerInjector(
    val parser: IFileParser,
    val repo : IRepo<*>
)