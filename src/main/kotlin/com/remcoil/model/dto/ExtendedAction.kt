package com.remcoil.model.dto

import kotlinx.datetime.LocalDateTime


data class ExtendedAction(
    val id: Long,
    val doneTime: LocalDateTime,
    val repair: Boolean,
    val operationType: Long,
    val employeeId: Long,
    val productId: Long,
    val active: Boolean,
    val batchId: Long,
    val kitId: Long,
    val specificationId: Long
)
