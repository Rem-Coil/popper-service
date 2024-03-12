package com.remcoil.model.dto

import com.remcoil.utils.LocalDateTimeSerializer
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AcceptanceActionRequest(
    @SerialName("done_time")
    @Serializable(with = LocalDateTimeSerializer::class)
    val doneTime: LocalDateTime,
    @SerialName("products_id")
    val productsId: List<Long>,
) {
    fun toAcceptanceActions(employeeId: Long): List<AcceptanceAction> {
        val acceptanceActions = ArrayList<AcceptanceAction>()
        for (productId in productsId) {
            acceptanceActions.add(
                AcceptanceAction(
                    0,
                    doneTime,
                    productId,
                    employeeId
                )
            )
        }
        return acceptanceActions
    }
}
