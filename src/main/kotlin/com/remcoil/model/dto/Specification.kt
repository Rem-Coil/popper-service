package com.remcoil.model.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Specification(
    val id: Long,
    @SerialName("specification_title")
    val specificationTitle: String,
    @SerialName("product_type")
    val productType: String,
    @SerialName("tested_percentage")
    val testedPercentage: Int
)
