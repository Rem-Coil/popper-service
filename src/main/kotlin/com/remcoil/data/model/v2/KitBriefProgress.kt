package com.remcoil.data.model.v2

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class KitBriefProgress(
    @SerialName("specification_id")
    val specificationId: Long,
    @SerialName("specification_title")
    val specificationTitle: String,
    @SerialName("tested_percentage")
    val testedPercentage: Int,
    @SerialName("kit_id")
    val kitId: Long,
    @SerialName("kit_number")
    val kitNumber: String,
    @SerialName("kit_size")
    val kitSize: Int,
    @SerialName("products_in_work")
    val productsInWork: Int,
    @SerialName("products_done")
    val productsDone: Int,
    @SerialName("control_progress")
    val controlProgress: Map<ControlType, Int>,
    @SerialName("locked_quantity")
    val lockedQuantity: Int,
    @SerialName("defected_quantity")
    val defectedQuantity: Int
) {
    constructor(
        kit: ExtendedKit,
        productsInWork: Int,
        productsDone: Int,
        controlProgress: Map<ControlType, Int>,
        lockedQuantity: Int,
        defectedQuantity: Int
    ) : this(
        kit.specificationId,
        kit.specificationTitle,
        kit.testedPercentage,
        kit.id,
        kit.kitNumber,
        kit.batchesQuantity * kit.batchSize,
        productsInWork, productsDone, controlProgress, lockedQuantity, defectedQuantity
    )
}
