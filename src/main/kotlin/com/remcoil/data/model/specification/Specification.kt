package com.remcoil.data.model.specification

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
) {
    constructor(specificationRequestDto: SpecificationRequestDto) : this(
        id = specificationRequestDto.id,
        specificationTitle = specificationRequestDto.specificationTitle,
        productType = specificationRequestDto.productType,
        testedPercentage = specificationRequestDto.testedPercentage
    )
}
