package com.remcoil.data.model.action

import com.remcoil.utils.LocalDateTimeSerializer
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Action(
    val id: Long,
    @SerialName("operator_id")
    val operatorId: Int,
    @SerialName("bobbin_id")
    val bobbinId: Long,
    @SerialName("action_type")
    val actionType: String,
    @SerialName("done_time")
    @Serializable(with = LocalDateTimeSerializer::class)
    val doneTime: LocalDateTime,
    var successful: Boolean = true
) {
    constructor(dto: ActionDto, operatorId: Int): this(
        dto.id,
        operatorId,
        dto.bobbinId,
        dto.actionType,
        dto.doneTime,
        dto.successful
    )

//    constructor(batchAction: BatchAction, operatorId: Int, bobbinId: Long)
}


