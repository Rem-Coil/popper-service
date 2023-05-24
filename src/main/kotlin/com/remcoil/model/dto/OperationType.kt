package com.remcoil.model.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OperationType(
    val id: Long,
    val type: String,
    @SerialName("sequence_number")
    val sequenceNumber: Int,
    @SerialName("specification_id")
    val specificationId: Long
)
