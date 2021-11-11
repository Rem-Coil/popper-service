package com.remcoil.dao.operator

import com.remcoil.data.model.operator.Operator
import com.remcoil.data.database.Operators
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class OperatorDao(private val database: Database) {
    fun getOperator(phone: String): Operator? = transaction(database) {
        Operators
            .select { (Operators.phone eq phone) and (Operators.active eq true)}
            .map (::extractOperator)
            .firstOrNull()
    }

    fun getAllOperators(): List<Operator> =  transaction(database) {
        Operators
            .select { Operators.active eq true }
            .map(::extractOperator)
    }

    fun getById(id: Int): Operator? = transaction(database) {
        Operators
            .select { Operators.id eq id }
            .map(::extractOperator)
            .firstOrNull()
    }

    fun checkOperator(operator: Operator): Operator? = transaction(database) {
        Operators
            .select { (Operators.active eq true) and (Operators.phone eq operator.phone) }
            .map(::extractOperator)
            .firstOrNull()
    }

    fun createOperator(operator: Operator): Operator = transaction(database) {
        val id = Operators.insertAndGetId {
            it[firstName] = operator.firstName
            it[secondName] = operator.secondName
            it[surname] = operator.surname
            it[phone] = operator.phone
            it[password] = operator.password
            it[active] = true
        }
        operator.copy(id = id.value)
    }

    fun deleteOperator(id: Int) = transaction(database) {
        Operators.update({ Operators.id eq id }) {
            it[active] = false
        }
    }

    private fun extractOperator(row: ResultRow): Operator = Operator(
        row[Operators.id].value,
        row[Operators.firstName],
        row[Operators.secondName],
        row[Operators.surname],
        row[Operators.phone],
        row[Operators.password],
        row[Operators.active]
    )
}