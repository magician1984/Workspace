package com.auo.dvr_core

enum class CamLocation(val code: Int) {
    Unknown(0),
    Front(1),
    Rear(2);

    companion object{
        fun fromCode(code: Int) = entries.find { it.code == code } ?: Unknown
    }
}