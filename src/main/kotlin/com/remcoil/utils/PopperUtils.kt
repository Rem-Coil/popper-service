package com.remcoil.utils

import org.slf4j.Logger
import org.slf4j.LoggerFactory

inline fun <T1: Any, T2: Any, R: Any> safeLet(p1: T1?, p2: T2?, block: (T1, T2)->R?): R? {
    return if (p1 != null && p2 != null) block(p1, p2) else null
}

val Any.logger: Logger get() = LoggerFactory.getLogger(this::class.java)