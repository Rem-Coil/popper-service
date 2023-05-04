package com.remcoil.data.model.v2

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SpecificationPostRequest(
    val id: Long,
    @SerialName("specification_title")
    val specificationTitle: String,
    @SerialName("product_type")
    val productType: String,
    @SerialName("tested_percentage")
    val testedPercentage: Int,
    @SerialName("operation_types")
    val operationTypes: List<OperationTypeRequest>
) {
    fun getSpecification() = Specification(
        id,
        specificationTitle,
        productType,
        testedPercentage
    )
}
