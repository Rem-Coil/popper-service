package com.remcoil.data.model.specification

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SpecificationAction(
    val id: Long,
    @SerialName("action_type")
    val actionType: String,
    @SerialName("sequence_number")
    val sequenceNumber: Int,
    @SerialName("specification_id")
    val specificationId: Long
)
