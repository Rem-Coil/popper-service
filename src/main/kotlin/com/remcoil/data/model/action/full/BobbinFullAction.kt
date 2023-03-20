//package com.remcoil.data.model.action.full
//
//import kotlinx.serialization.SerialName
//import kotlinx.serialization.Serializable
//
//@Serializable
//data class BobbinFullAction(
//    @SerialName("id")
//    val id: Long,
//    @SerialName("bobbin_number")
//    val bobbinNumber: String,
//    @SerialName("is_active_bobbin")
//    val isActiveBobbin: Boolean,
//    @SerialName("actions")
//    val actions: List<FullAction>
//) {
//    companion object {
//        fun toBobbinFullAction(actions: List<FlatFullAction>): BobbinFullAction {
//            return BobbinFullAction(
//                id = actions[0].bobbinId,
//                bobbinNumber = actions[0].bobbinNumber,
//                isActiveBobbin = actions[0].isActiveBobbin,
//                actions = FullAction.toFullAction(actions)
//            )
//        }
//    }
//}