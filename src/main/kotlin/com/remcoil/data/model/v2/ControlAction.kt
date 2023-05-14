package com.remcoil.data.model.v2

import com.remcoil.utils.LocalDateTimeSerializer
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ControlAction(
    val id: Long,
    @SerialName("done_time")
    @Serializable(with = LocalDateTimeSerializer::class)
    val doneTime: LocalDateTime,
    val successful: Boolean,
    @SerialName("control_type")
    val controlType: ControlType,
    val comment: String?,
    @SerialName("operation_type")
    val operationType: Long,
    @SerialName("employee_id")
    val employeeId: Long,
    @SerialName("product_id")
    val productId: Long
)
