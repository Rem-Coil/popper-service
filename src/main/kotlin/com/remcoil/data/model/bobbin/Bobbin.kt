package com.remcoil.data.model.bobbin

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Bobbin(
    val id: Long,
    @SerialName("batch_id")
    val batchId: Long,
    @SerialName("bobbin_number")
    var bobbinNumber: String
)