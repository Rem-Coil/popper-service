package com.remcoil.data.model.task

import com.remcoil.data.database.Tasks
import kotlinx.serialization.Serializable

@Serializable
data class Task(
    val id: Int,
    val task_name: String,
    val task_number: String,
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
        identity.task_name,
        identity.task_number,
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
