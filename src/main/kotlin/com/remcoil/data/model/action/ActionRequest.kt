package com.remcoil.data.model.action

import com.remcoil.utils.LocalDateTimeSerializer
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ActionRequest(
    val id: Long,
    @SerialName("product_id")
    val productId: Long,
    @SerialName("action_type_id")
    val actionTypeId: Long,
    @SerialName("done_time")
    @Serializable(with = LocalDateTimeSerializer::class)
    val doneTime: LocalDateTime,
    var successful: Boolean = true
)
