package com.remcoil.dao.specification

import com.remcoil.data.database.SpecificationActions
import com.remcoil.data.model.specification.SpecificationAction
import com.remcoil.utils.safetySuspendTransactionAsync
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class SpecificationActionDao(private val database: Database) {
    suspend fun getById(id: Long): SpecificationAction? = safetySuspendTransactionAsync(database) {
        SpecificationActions
            .select { SpecificationActions.id eq id }
            .map(::extractSpecificationAction)
            .firstOrNull()
    }

    suspend fun getAll(): List<SpecificationAction> = safetySuspendTransactionAsync(database) {
        SpecificationActions
            .selectAll()
            .map(::extractSpecificationAction)
    }

    suspend fun create(specificationAction: SpecificationAction): SpecificationAction =
        safetySuspendTransactionAsync(database) {
            val id = SpecificationActions.insertAndGetId {
                it[actionType] = specificationAction.actionType
                it[sequenceNumber] = specificationAction.sequenceNumber
                it[specificationId] = specificationAction.specificationId
            }
            specificationAction.copy(id = id.value)
        }

    suspend fun update(specificationAction: SpecificationAction) = safetySuspendTransactionAsync(database) {
        SpecificationActions.update({ SpecificationActions.id eq specificationAction.id }) {
            it[actionType] = specificationAction.actionType
            it[sequenceNumber] = specificationAction.sequenceNumber
            it[specificationId] = specificationAction.specificationId
        }
    }

    suspend fun deleteById(id: Long) = safetySuspendTransactionAsync(database) {
        SpecificationActions.deleteWhere { SpecificationActions.id eq id }
    }

    private fun extractSpecificationAction(row: ResultRow): SpecificationAction = SpecificationAction(
        row[SpecificationActions.id].value,
        row[SpecificationActions.actionType],
        row[SpecificationActions.sequenceNumber],
        row[SpecificationActions.specificationId].value
    )
}