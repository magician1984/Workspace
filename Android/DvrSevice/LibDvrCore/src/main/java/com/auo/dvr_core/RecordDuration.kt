package com.auo.dvr_core

enum class RecordDuration(val value : Long) {
    OneMin(60 * 1000),
    ThreeMin(3 * 60 * 1000),
    FiveMin(5 * 60 * 1000)
}