package com.remcoil.data.model.action.full

import com.remcoil.utils.LocalDateTimeSerializer
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FullAction(
    @SerialName("id")
    val id: Long,
    @SerialName("action_type")
    val actionType: String,
    @SerialName("done_time")
    @Serializable(with = LocalDateTimeSerializer::class)
    val doneTime: LocalDateTime,
    val successful: Boolean,
    @SerialName("operator_id")
    val operatorId: Int,
    @SerialName("first_name")
    val firstname: String,
    @SerialName("second_name")
    val secondName: String,
    val surname: String
) {
    companion object {
        fun toFullAction(actions: List<FlatFullAction>): List<FullAction> {
            return actions.map { flatFullAction -> FullAction(
                id = flatFullAction.actionId,
                actionType = flatFullAction.actionType,
                doneTime = flatFullAction.doneTime,
                successful = flatFullAction.successful,
                operatorId = flatFullAction.operatorId,
                firstname = flatFullAction.firstname,
                secondName = flatFullAction.secondName,
                surname = flatFullAction.surname
            ) }.toList()
        }
    }
}