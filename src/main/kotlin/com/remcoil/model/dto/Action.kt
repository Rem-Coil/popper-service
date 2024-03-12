package com.remcoil.model.dto

import com.remcoil.utils.LocalDateTimeSerializer
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Action(
    val id: Long,
    @SerialName("done_time")
    @Serializable(with = LocalDateTimeSerializer::class)
    val doneTime: LocalDateTime,
    val repair: Boolean,
    @SerialName("operation_type")
    val operationType: Long,
    @SerialName("employee_id")
    val employeeId: Long,
    @SerialName("product_id")
    val productId: Long
)
