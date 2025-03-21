package com.auo.dvr_core

enum class FileType(val code: Int) {
    Unknown(0),
    Locked(1),
    Protected(2);

    companion object {
        fun fromCode(code: Int) : FileType  = entries.find { it.code == code } ?: Unknown
    }
}