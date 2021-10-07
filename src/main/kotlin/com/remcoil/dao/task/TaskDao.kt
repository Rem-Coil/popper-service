package com.remcoil.dao.task

import com.remcoil.data.database.Tasks
import com.remcoil.data.model.task.Task
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class TaskDao(private val database: Database) {

    fun getAllTasks(): List<Task> = transaction(database) {
        Tasks
            .selectAll()
            .map(::extractTask)
    }

    fun createTask(task: Task): Task = transaction(database) {
        val id = Tasks.insertAndGetId {
            it[taskName] = task.taskName
            it[taskNumber] = task.taskNumber
            it[quantity] = task.quantity
            it[winding] = task.winding
            it[output] = task.output
            it[isolation] = task.isolation
            it[molding] = task.molding
            it[crimping] = task.crimping
            it[quality] = task.quality
            it[testing] = task.testing
        }
        task.copy(id = id.value)
    }

    fun deleteTask(task_name: String) = transaction(database){
        Tasks.deleteWhere { Tasks.taskName eq task_name }
    }

    fun extractTask(row: ResultRow): Task = Task(
        row[Tasks.id].value,
        row[Tasks.taskName],
        row[Tasks.taskNumber],
        row[Tasks.quantity],
        row[Tasks.winding],
        row[Tasks.output],
        row[Tasks.isolation],
        row[Tasks.molding],
        row[Tasks.crimping],
        row[Tasks.quality],
        row[Tasks.testing]
    )
}