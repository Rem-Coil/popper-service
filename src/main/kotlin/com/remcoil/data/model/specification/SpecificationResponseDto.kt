package com.remcoil.data.model.specification

import com.remcoil.data.model.specification.action.SpecificationAction
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SpecificationResponseDto(
    val id: Long,
    @SerialName("specification_title")
    val specificationTitle: String,
    @SerialName("product_type")
    val productType: String,
    @SerialName("tested_percentage")
    val testedPercentage: Int,
    @SerialName("kit_quantity)")
    val kitQuantity: Int,
    @SerialName("action_list")
    var actionSet: Set<SpecificationAction>
) {
    constructor(specification: Specification, kitQuantity: Int, actionSet: Set<SpecificationAction>) : this(
        id = specification.id,
        specificationTitle = specification.specificationTitle,
        productType = specification.productType,
        testedPercentage = specification.testedPercentage,
        kitQuantity = kitQuantity,
        actionSet = actionSet
    )
}
