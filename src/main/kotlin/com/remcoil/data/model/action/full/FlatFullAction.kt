package com.remcoil.data.model.action.full

import com.remcoil.utils.LocalDateTimeSerializer
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FlatFullAction(
    @SerialName("specification_id")
    val specificationId: Long,
    @SerialName("specification_title")
    val specificationTitle: String,
    @SerialName("product_type")
    val productType: String,

    @SerialName("kit_id")
    val kitId: Long,
    @SerialName("kit_number")
    val kitNumber: String,

    @SerialName("batch_id")
    val batchId: Long,
    @SerialName("batch_number")
    val batchNumber: String,

    @SerialName("product_id")
    val productId: Long,
    @SerialName("product_number")
    val productNumber: String,
    @SerialName("active")
    val active: Boolean,

    @SerialName("action_id")
    val actionId: Long,
    @SerialName("done_time")
    @Serializable(with = LocalDateTimeSerializer::class)
    val doneTime: LocalDateTime,
    val successful: Boolean,

    @SerialName("specification_action_id")
    val specificationActionId: Long,
    @SerialName("action_type")
    val actionType: String,
    @SerialName("sequence_number")
    val sequenceNumber: Int,

    val comment: String?,

    @SerialName("operator_id")
    val operatorId: Int,
    @SerialName("first_name")
    val firstname: String,
    @SerialName("second_name")
    val secondName: String,
    val surname: String,
    val role: String
)