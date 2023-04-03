package com.remcoil.data.model.action.full

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BatchFullAction(
    @SerialName("id")
    val id: Long,
    @SerialName("batch_number")
    val batchNumber: String,
    @SerialName("bobbins")
    val bobbins: List<BobbinFullAction>
) {
    companion object {
        fun toBatchFullAction(actions: List<FlatFullAction>): BatchFullAction {
            return BatchFullAction(
                id = actions[0].batchId,
                batchNumber = actions[0].batchNumber,
                bobbins = actions
                    .groupBy { action -> action.bobbinId }
                    .values.map { list -> BobbinFullAction.toBobbinFullAction(list) }
            )
        }
    }
}
