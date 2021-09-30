package com.remcoil.dao.operator

import com.remcoil.data.model.Operator
import com.remcoil.data.database.Operators
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class OperatorDao(private val database: Database) {
    fun getOperator(phone: String): Operator = transaction(database) {
        Operators
            .select { Operators.phone eq phone}
            .map (::extractOperator)
            .first()
    }

    fun getAllOperators() = transaction(database) {
        Operators
            .selectAll()
            .map { ::extractOperator }
    }

    fun createOperator(operator: Operator): Operator = transaction(database) {
        val id = Operators.insertAndGetId {
            it[firstname] = operator.firstname
            it[second_name] = operator.second_name
            it[surname] = operator.surname
            it[phone] = operator.phone
            it[password] = operator.password
        }
        operator.copy(id = id.value)
    }

    fun deleteOperator(phone: String) = transaction(database) {
        Operators.deleteWhere { Operators.phone eq phone }
    }

    private fun extractOperator(row: ResultRow): Operator = Operator(
        row[Operators.id].value,
        row[Operators.firstname],
        row[Operators.second_name],
        row[Operators.surname],
        row[Operators.phone],
        row[Operators.password]
    )
}