package com.remcoil.data.model.comment

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class Comment(
    @SerialName("action_id")
    val actionId: Long,
    val comment: String
)
