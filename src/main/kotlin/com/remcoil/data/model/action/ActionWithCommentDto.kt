package com.remcoil.data.model.action

import com.remcoil.utils.LocalDateTimeSerializer
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ActionWithCommentDto(
    val id: Long,
    @SerialName("bobbin_id")
    val bobbinId: Long,
    @SerialName("action_type")
    val actionType: String,
    @SerialName("done_time")
    @Serializable(with = LocalDateTimeSerializer::class)
    val doneTime: LocalDateTime,
    var successful: Boolean = false,
    val comment: String
)
