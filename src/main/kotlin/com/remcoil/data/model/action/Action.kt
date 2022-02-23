package com.remcoil.data.model.action

import com.remcoil.utils.LocalDateTimeSerializer
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Action(
    val id: Int,
    @SerialName("operator_id")
    val operatorId: Int,
    @SerialName("bobbin_id")
    val bobbinId: Int,
    @SerialName("action_type")
    val actionType: String,
    @SerialName("done_time")
    @Serializable(with = LocalDateTimeSerializer::class)
    val doneTime: LocalDateTime,
    val successful: Boolean
)


