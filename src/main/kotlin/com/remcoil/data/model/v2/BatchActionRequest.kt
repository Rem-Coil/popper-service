package com.remcoil.data.model.v2

import com.remcoil.utils.LocalDateTimeSerializer
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BatchActionRequest(
    @SerialName("done_time")
    @Serializable(with = LocalDateTimeSerializer::class)
    val doneTime: LocalDateTime,
    val repair: Boolean,
    @SerialName("operation_type")
    val operationType: Long,
    @SerialName("batch_id")
    val batchId: Long
) {
    fun toAction(employeeId: Long, productId: Long) = Action(
        id = 0,
        doneTime,
        repair,
        operationType,
        employeeId,
        productId
    )
}
