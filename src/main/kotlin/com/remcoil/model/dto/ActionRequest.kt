package com.remcoil.model.dto

import com.remcoil.utils.LocalDateTimeSerializer
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ActionRequest(
    @SerialName("done_time")
    @Serializable(with = LocalDateTimeSerializer::class)
    val doneTime: LocalDateTime,
    val repair: Boolean,
    @SerialName("operation_type")
    val operationType: Long,
    @SerialName("product_id")
    val productId: Long
) {
    fun toAction(employeeId: Long) = Action(
        id = 0,
        doneTime = this.doneTime,
        repair = this.repair,
        operationType = this.operationType,
        employeeId = employeeId,
        productId = this.productId
    )
}
