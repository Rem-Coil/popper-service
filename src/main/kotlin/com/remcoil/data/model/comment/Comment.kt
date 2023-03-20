package com.remcoil.data.model.comment

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Comment(
    @SerialName("action_id")
    val actionId: Long,
    val comment: String
)
