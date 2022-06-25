package com.remcoil.dao.operator

import com.remcoil.data.model.operator.Operator
import com.remcoil.data.database.Operators
import com.remcoil.utils.safetySuspendTransactionAsync
import org.jetbrains.exposed.sql.*

class OperatorDao(private val database: Database) {
    suspend fun getOperator(phone: String): Operator? = safetySuspendTransactionAsync(database) {
        Operators
            .select { (Operators.phone eq phone) and (Operators.active eq true)}
            .map (::extractOperator)
            .firstOrNull()
    }

    suspend fun getAllOperators(onlyActive: Boolean): List<Operator> = safetySuspendTransactionAsync(database) {
        Operators
            .select { (Operators.active eq true) or (Operators.active eq onlyActive)}
            .map(::extractOperator)
    }

    suspend fun getById(id: Int): Operator? = safetySuspendTransactionAsync(database) {
        Operators
            .select { Operators.id eq id }
            .map(::extractOperator)
            .firstOrNull()
    }

    suspend fun isNotExist(operator: Operator): Boolean = safetySuspendTransactionAsync(database) {
        Operators
            .select { (Operators.active eq true) and (Operators.phone eq operator.phone) }
            .map(::extractOperator)
            .isEmpty()
    }

    suspend fun createOperator(operator: Operator): Operator = safetySuspendTransactionAsync(database) {
        val id = Operators.insertAndGetId {
            it[firstName] = operator.firstName
            it[secondName] = operator.secondName
            it[surname] = operator.surname
            it[phone] = operator.phone
            it[password] = operator.password
            it[active] = true
            it[role] = operator.role
        }
        operator.copy(id = id.value)
    }

    suspend fun deleteOperator(id: Int) = safetySuspendTransactionAsync(database) {
        Operators.update({ Operators.id eq id }) {
            it[active] = false
        }
    }

    suspend fun updateOperator(operator: Operator) = safetySuspendTransactionAsync(database) {
        Operators.update({Operators.id eq operator.id}) {
            it[firstName] = operator.firstName
            it[secondName] = operator.secondName
            it[surname] = operator.surname
            it[phone] = operator.phone
            it[password] = operator.password
            it[active] = operator.active
            it[role] = operator.role
        }
    }

    suspend fun trueDeleteOperator(phone: String) = safetySuspendTransactionAsync(database) {
        Operators.deleteWhere { Operators.phone eq phone }
    }

    private fun extractOperator(row: ResultRow): Operator = Operator(
        row[Operators.id].value,
        row[Operators.firstName],
        row[Operators.secondName],
        row[Operators.surname],
        row[Operators.phone],
        row[Operators.password],
        row[Operators.active],
        row[Operators.role]
    )
}