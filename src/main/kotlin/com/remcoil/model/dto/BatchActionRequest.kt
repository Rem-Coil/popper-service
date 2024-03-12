package com.remcoil.model.dto

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
    @SerialName("products_id")
    val productsId: List<Long>
) {
    fun toActions(employeeId: Long, validProductsId: List<Long>): List<Action> {
        val actions = ArrayList<Action>()
        for (productId in validProductsId) {
            actions.add(Action(0, doneTime, repair, operationType, employeeId, productId))
        }
        return actions
    }
}
