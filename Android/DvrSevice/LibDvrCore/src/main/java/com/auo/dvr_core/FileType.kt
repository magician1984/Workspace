package com.auo.dvr_core

enum class FileType(val code: Int) {
    Unknown(0),
    Normal(1),
    Locked(2),
    Protected(3);

    companion object {
        fun fromCode(code: Int) : FileType  = entries.find { it.code == code } ?: Unknown
    }
}