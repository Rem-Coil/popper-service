package com.remcoil.data.model.action

import com.remcoil.utils.LocalDateTimeSerializer
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Action(
    val id: Long,
    @SerialName("operator_id")
    val operatorId: Long,
    @SerialName("product_id")
    val productId: Long,
    @SerialName("action_type_id")
    val actionTypeId: Long,
    @SerialName("done_time")
    @Serializable(with = LocalDateTimeSerializer::class)
    val doneTime: LocalDateTime,
    var successful: Boolean = true
) {
    constructor(actionRequest: ActionRequest, operatorId: Long) : this(
        id = actionRequest.id,
        operatorId = operatorId,
        productId = actionRequest.productId,
        actionTypeId = actionRequest.actionTypeId,
        doneTime = actionRequest.doneTime,
        successful = actionRequest.successful
    )
}


