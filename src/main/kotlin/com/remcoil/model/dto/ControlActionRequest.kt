package com.remcoil.model.dto

import com.remcoil.utils.LocalDateTimeSerializer
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ControlActionRequest(
    @SerialName("done_time")
    @Serializable(with = LocalDateTimeSerializer::class)
    val doneTime: LocalDateTime,
    val successful: Boolean,
    @SerialName("control_type")
    val controlType: ControlType,
    val comment: String?,
    @SerialName("operation_type")
    val operationType: Long,
    @SerialName("product_id")
    val productId: Long
) {
    fun toControlAction(employeeId: Long) = ControlAction(
        id = 0,
        doneTime,
        successful,
        controlType,
        comment,
        operationType,
        employeeId,
        productId
    )

}
