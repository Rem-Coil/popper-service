package com.remcoil.data.model.v2

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SpecificationResponse(
    val id: Long,
    @SerialName("specification_title")
    val specificationTitle: String,
    @SerialName("product_type")
    val productType: String,
    @SerialName("tested_percentage")
    val testedPercentage: Int,
    @SerialName("kit_quantity")
    val kitQuantity: Int
) {
    constructor(specification: Specification, kitQuantity: Int) : this(
        id = specification.id,
        specificationTitle = specification.specificationTitle,
        productType = specification.productType,
        testedPercentage = specification.testedPercentage,
        kitQuantity = kitQuantity
    )
}
