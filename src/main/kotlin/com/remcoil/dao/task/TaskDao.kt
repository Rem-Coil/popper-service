package com.remcoil.dao.task

import com.remcoil.data.database.Actions
import com.remcoil.data.database.Bobbins
import com.remcoil.data.database.Tasks
import com.remcoil.data.model.task.Task
import com.remcoil.data.model.task.TaskIdentity
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

    suspend fun createTask(task: Task): Task = safetySuspendTransactionAsync(database) {
        val id = Tasks.insertAndGetId {
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
        Tasks.deleteWhere { Tasks.id eq id }
    }

    fun extractTask(row: ResultRow): Task = Task(
        row[Tasks.id].value,
        row[Tasks.taskName],
        row[Tasks.taskNumber],
        row[Tasks.quantity]
    )
}