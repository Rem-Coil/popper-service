package com.remcoil.model.dto

import kotlinx.datetime.LocalDateTime

data class ExtendedControlAction(
    val id: Long,
    val doneTime: LocalDateTime,
    val successful: Boolean,
    val controlType: ControlType,
    val comment: String?,
    val operationType: Long,
    val employeeId: Long,
    val productId: Long,
    val active: Boolean,
    val batchId: Long,
    val kitId: Long,
    val specificationId: Long
)