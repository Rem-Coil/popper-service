package com.remcoil.model.dto

import kotlinx.datetime.LocalDateTime

data class ExtendedAcceptanceAction(
    val id: Long,
    val doneTime: LocalDateTime,
    val employeeId: Long,
    val productId: Long,
    val active: Boolean,
    val batchId: Long,
    val kitId: Long,
    val specificationId: Long
)