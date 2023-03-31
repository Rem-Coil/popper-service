package com.remcoil.dao.specification

import com.remcoil.data.database.SpecificationActions
import com.remcoil.data.model.specification.action.SpecificationAction
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

    suspend fun batchCreate(actions: List<SpecificationAction>) = safetySuspendTransactionAsync(database) {
        SpecificationActions.batchInsert(actions) { action: SpecificationAction ->
            this[SpecificationActions.actionType] = action.actionType
            this[SpecificationActions.sequenceNumber] = action.sequenceNumber
            this[SpecificationActions.specificationId] = action.specificationId
        }
            .map(::extractSpecificationAction)
    }.toSet()

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

    suspend fun getBySpecificationId(id: Long): Set<SpecificationAction> = safetySuspendTransactionAsync(database) {
        SpecificationActions.select { SpecificationActions.specificationId eq id }
            .map(::extractSpecificationAction)
    }.toSet()

    private fun extractSpecificationAction(row: ResultRow): SpecificationAction = SpecificationAction(
        row[SpecificationActions.id].value,
        row[SpecificationActions.actionType],
        row[SpecificationActions.sequenceNumber],
        row[SpecificationActions.specificationId].value
    )
}