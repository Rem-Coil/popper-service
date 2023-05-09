package com.remcoil.data.model.v2

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SpecificationProgress(
    val id: Long,
    @SerialName("specification_title")
    val specificationTitle: String,
    @SerialName("tested_percentage")
    val testedPercentage: Int,
    @SerialName("kits_progress")
    val kitsProgress: List<KitShortProgress>
) {
    constructor(specification: SpecificationResponse, kitsProgress: List<KitShortProgress>) : this(
        specification.id,
        specification.specificationTitle,
        specification.testedPercentage,
        kitsProgress
    )
}
