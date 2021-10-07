package com.remcoil.data.model.task

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Task(
    val id: Int,
    val taskName: String,
    val taskNumber: String,
    val quantity: Int,
    val winding: Int,
    val output: Int,
    val isolation: Int,
    val molding: Int,
    val crimping: Int,
    val quality: Int,
    val testing: Int
) {
    constructor(identity: TaskIdentity) : this(
        0,
        identity.taskName,
        identity.taskNumber,
        identity.quantity,
        0,
        0,
        0,
        0,
        0,
        0,
        0
    )
}
