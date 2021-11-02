package com.remcoil.data.model.action

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
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
    val doneTime: LocalDateTime
) {
    constructor(identity: ActionIdentity): this(
        0,
        identity.operatorId,
        identity.bobbinId,
        identity.actionType,
        Clock.System.now().toLocalDateTime(TimeZone.UTC)
    )
}