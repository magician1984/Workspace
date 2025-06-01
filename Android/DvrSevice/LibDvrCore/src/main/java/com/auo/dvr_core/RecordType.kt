package com.auo.dvr_core

enum class RecordType(val code: Int) {
    Unknown(0),
    Normal(1),
    Locked(2),
    Protected(4);

    companion object {
        fun fromCode(code: Int) : RecordType  = entries.find { it.code == code } ?: Unknown
    }
}