package com.remcoil.data.model.action

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class ActionIdentity(
    @SerialName("operator_id")
    val operatorId: Int,
    @SerialName("bobbin_id")
    val bobbinId: Int,
    @SerialName("action_type")
    val actionType: String
)