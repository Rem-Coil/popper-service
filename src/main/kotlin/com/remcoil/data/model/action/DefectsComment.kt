package com.remcoil.data.model.action

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class DefectsComment(
    @SerialName("action_id")
    val actionId: Long,
    val comment: String
)
