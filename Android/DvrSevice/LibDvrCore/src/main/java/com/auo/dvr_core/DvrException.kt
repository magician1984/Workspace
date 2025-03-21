package com.auo.dvr_core

open class DvrException(tag: String, msg: String) : RuntimeException("[$tag] $msg")