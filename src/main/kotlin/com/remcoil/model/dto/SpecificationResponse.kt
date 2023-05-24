package com.remcoil.model.dto

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
    val kitQuantity: Int,
    @SerialName("operation_types")
    var operationTypes: List<OperationType>
) {
    constructor(specification: Specification, kitQuantity: Int, operationTypes: List<OperationType>) : this(
        specification.id,
        specification.specificationTitle,
        specification.productType,
        specification.testedPercentage,
        kitQuantity,
        operationTypes
    )
}
