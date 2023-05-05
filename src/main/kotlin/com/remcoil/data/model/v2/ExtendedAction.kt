package com.remcoil.data.model.v2

import kotlinx.datetime.LocalDateTime


data class ExtendedAction(
    val id: Long,
    val doneTime: LocalDateTime,
    val repair: Boolean,
    val operationType: Long,
    val employeeId: Long,
    val productId: Long,
    val batchId: Long,
    val kitId: Long
)
