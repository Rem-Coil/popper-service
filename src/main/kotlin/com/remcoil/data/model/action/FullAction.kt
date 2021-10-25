package com.remcoil.data.model.action

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FullAction(
    val bobbinId: Int,
    val bobbinNumber: String,
    val firstname: String,
    @SerialName("second_name")
    val secondName: String,
    val surname: String,
    val actionType: String,
    val doneTime: LocalDateTime
)