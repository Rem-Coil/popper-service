package com.remcoil.data.model.action

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class Action(
    val id: Int,
    val operatorId: Int,
    val bobbinId: Int,
    val actionType: String,
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