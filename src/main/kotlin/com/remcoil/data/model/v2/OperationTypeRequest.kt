package com.remcoil.data.model.v2

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OperationTypeRequest(
    val type: String,
    @SerialName("sequence_number")
    val sequenceNumber: Int
) {
    fun toOperationType(specificationId: Long) = OperationType(
        id = 0,
        type,
        sequenceNumber,
        specificationId
    )
}
