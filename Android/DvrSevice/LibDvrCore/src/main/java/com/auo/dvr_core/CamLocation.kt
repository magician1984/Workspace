package com.auo.dvr_core

enum class CamLocation(val code: Int) {
    Front(1),
    Rear(2),
    Left(3),
    Right(4);

    companion object{
        fun fromCode(code: Int) : CamLocation = entries.find { it.code == code }!!
    }
}