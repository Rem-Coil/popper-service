package com.remcoil.data.model.v2

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class KitShortProgress(
    val id: Long,
    @SerialName("kit_number")
    val kitNumber: String,
    @SerialName("kit_size")
    val kitSize: Int,
    @SerialName("products_in_work")
    val productsInWork: Int,
    @SerialName("products_done")
    val productsDone: Int,
    @SerialName("control_progress")
    val controlProgress: Map<String, Int>,
    @SerialName("locked_quantity")
    val lockedQuantity: Int,
    @SerialName("defected_quantity")
    val defectedQuantity: Int
)
