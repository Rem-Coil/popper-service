package com.remcoil.dao.task

import com.remcoil.data.database.Tasks
import com.remcoil.data.model.task.Task
import com.remcoil.utils.safetySuspendTransactionAsync
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class TaskDao(private val database: Database) {

    fun getAllTasks(): List<Task> = transaction(database) {
        Tasks
            .selectAll()
            .map(::extractTask)
    }

    fun getById(id: Int): Task? = transaction(database) {
        Tasks.select { Tasks.id eq id }
            .map(::extractTask)
            .firstOrNull()
    }

    suspend fun updateTask(task: Task) = safetySuspendTransactionAsync(database) {
        Tasks.update({Tasks.id eq task.id}) {
            it[taskName] = task.taskName
            it[taskNumber] = task.taskNumber
        }
    }

    suspend fun createTask(task: Task): Task = safetySuspendTransactionAsync(database) {
        val id = Tasks.insertAndGetId {
            it[taskName] = task.taskName
            it[taskNumber] = task.taskNumber
        }
        task.copy(id = id.value)
    }

    suspend fun deleteTask(id: Int) = safetySuspendTransactionAsync(database) {
        Tasks.deleteWhere { Tasks.id eq id }
    }

    private fun extractTask(row: ResultRow): Task = Task(
        row[Tasks.id].value,
        row[Tasks.taskName],
        row[Tasks.taskNumber],
    )
}