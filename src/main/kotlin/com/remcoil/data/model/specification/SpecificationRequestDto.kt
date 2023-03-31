package com.remcoil.data.model.specification

import com.remcoil.data.model.specification.action.SpecificationAction
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SpecificationRequestDto(
    val id: Long,
    @SerialName("specification_title")
    val specificationTitle: String,
    @SerialName("product_type")
    val productType: String,
    @SerialName("tested_percentage")
    val testedPercentage: Int,
    @SerialName("action_set")
    val actionSet: Set<SpecificationAction>
)