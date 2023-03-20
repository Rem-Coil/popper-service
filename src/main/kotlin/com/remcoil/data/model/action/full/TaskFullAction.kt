//package com.remcoil.data.model.action.full
//
//import kotlinx.serialization.SerialName
//import kotlinx.serialization.Serializable
//
//@Serializable
//data class TaskFullAction(
//    @SerialName("id")
//    val id: Int,
//    @SerialName("task_name")
//    val taskName: String,
//    @SerialName("task_number")
//    val taskNumber: String,
//    @SerialName("batches")
//    val batches: List<BatchFullAction>
//) {
//    companion object {
//        fun toTaskFullAction(actions: List<FlatFullAction>): TaskFullAction {
//            return TaskFullAction(
//                id = actions[0].taskId,
//                taskName = actions[0].taskName,
//                taskNumber = actions[0].taskNumber,
//                batches = actions
//                    .groupBy { action -> action.batchId }
//                    .values.map { list -> BatchFullAction.toBatchFullAction(list) }
//            )
//        }
//    }
//}
