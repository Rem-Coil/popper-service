package com.remcoil.model.dto

import com.remcoil.utils.LocalDateTimeSerializer
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BatchControlActionRequest(
    @SerialName("done_time")
    @Serializable(with = LocalDateTimeSerializer::class)
    val doneTime: LocalDateTime,
    val successful: Boolean,
    @SerialName("need_repair")
    val needRepair: Boolean,
    @SerialName("control_type")
    val controlType: ControlType,
    val comment: String?,
    @SerialName("operation_type")
    val operationType: Long,
    @SerialName("products_id")
    val productsId: List<Long>
) {
    fun toControlActions(employeeId: Long): List<ControlAction> {
        val controlActions = ArrayList<ControlAction>()
        for (productId in productsId) {
            controlActions.add(
                ControlAction(
                    0,
                    doneTime,
                    successful,
                    needRepair,
                    controlType,
                    comment,
                    operationType,
                    employeeId,
                    productId
                )
            )
        }
        return controlActions
    }
}
