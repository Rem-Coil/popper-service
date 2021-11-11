package com.remcoil.data.model.action

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FullAction(
    val bobbinId: Int,
    @SerialName("bobbin_number")
    val bobbinNumber: String,
    val firstname: String,
    @SerialName("second_name")
    val secondName: String,
    val surname: String,
    @SerialName("action_type")
    val actionType: String,
    @SerialName("done_time")
    val doneTime: LocalDateTime
)