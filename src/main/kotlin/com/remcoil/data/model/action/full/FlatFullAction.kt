package com.remcoil.data.model.action.full

import com.remcoil.utils.LocalDateTimeSerializer
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FlatFullAction(
    @SerialName("task_id")
    val taskId: Int,
    @SerialName("task_name")
    val taskName: String,
    @SerialName("task_number")
    val taskNumber: String,
    @SerialName("batch_id")
    val batchId: Long,
    @SerialName("batch_number")
    val batchNumber: String,
    @SerialName("bobbin_id")
    val bobbinId: Long,
    @SerialName("bobbin_number")
    val bobbinNumber: String,
    @SerialName("is_active_bobbin")
    val isActiveBobbin: Boolean,
    @SerialName("action_id")
    val actionId: Long,
    @SerialName("action_type")
    val actionType: String,
    @SerialName("done_time")
    @Serializable(with = LocalDateTimeSerializer::class)
    val doneTime: LocalDateTime,
    val successful: Boolean,
    val comment: String?,
    @SerialName("operator_id")
    val operatorId: Int,
    @SerialName("first_name")
    val firstname: String,
    @SerialName("second_name")
    val secondName: String,
    val surname: String
)