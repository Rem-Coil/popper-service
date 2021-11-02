package com.remcoil.dao.task

import com.remcoil.data.database.Bobbins
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

    fun getById(id: Int): Task? = transaction(database) {
        Tasks.select { Tasks.id eq id }
            .map(::extractTask)
            .firstOrNull()
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

    fun deleteTask(id: Int) = transaction(database){
        Bobbins.deleteWhere { Bobbins.taskId eq id }
        Tasks.deleteWhere { Tasks.id eq id }
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