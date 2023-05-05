package com.remcoil.data.model.v2

import kotlinx.datetime.LocalDateTime

data class ExtendedControlAction(
    val id: Long,
    val doneTime: LocalDateTime,
    val successful: Boolean,
    val controlType: String,
    val comment: String,
    val operationType: Long,
    val employeeId: Long,
    val productId: Long,
    val batchId: Long,
    val kitId: Long
)