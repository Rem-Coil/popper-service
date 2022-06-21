package com.remcoil.dao.task

import com.remcoil.data.database.Bobbins
import com.remcoil.data.database.TaskTable
import com.remcoil.data.model.task.Task
import com.remcoil.utils.safetySuspendTransactionAsync
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class TaskDao(private val database: Database) {

    fun getAllTasks(): List<Task> = transaction(database) {
        TaskTable
            .selectAll()
            .map(::extractTask)
    }

    fun getById(id: Int): Task? = transaction(database) {
        TaskTable.select { TaskTable.id eq id }
            .map(::extractTask)
            .firstOrNull()
    }

    suspend fun updateTask(task: Task) = safetySuspendTransactionAsync(database) {
        TaskTable.update({TaskTable.id eq task.id}) {
            it[taskName] = task.taskName
            it[taskNumber] = task.taskNumber
            it[quantity] = task.quantity
        }
    }

    suspend fun createTask(task: Task): Task = safetySuspendTransactionAsync(database) {
        val id = TaskTable.insertAndGetId {
            it[taskName] = task.taskName
            it[taskNumber] = task.taskNumber
            it[quantity] = task.quantity
        }
        createBobbins(task.taskNumber, id.value, task.quantity)
        task.copy(id = id.value)
    }

    private fun createBobbins(number: String, id: Int, quantity: Int) {
        for (i in 1..quantity) {
            Bobbins.insert {
                it[taskId] = id
                it[bobbinNumber] = "$number-$i"
            }
        }
    }

    suspend fun deleteTask(id: Int) = safetySuspendTransactionAsync(database) {
        TaskTable.deleteWhere { TaskTable.id eq id }
    }

    fun extractTask(row: ResultRow): Task = Task(
        row[TaskTable.id].value,
        row[TaskTable.taskName],
        row[TaskTable.taskNumber],
        row[TaskTable.quantity]
    )
}