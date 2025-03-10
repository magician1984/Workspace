package com.auo.dvr_core

enum class CamLocationDef(val value : Byte){
    Front(0),
    Rear(1),
    Unknown(2)
}

enum class RecordFileTypeDef(val value : Byte){
    Normal(0),
    Protect(1),
    Event(2),
    Unknown(-1)
}