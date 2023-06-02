package com.remcoil.dao

import com.remcoil.model.database.OperationTypes
import com.remcoil.model.dto.OperationType
import com.remcoil.utils.safetySuspendTransactionAsync
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList

class OperationTypeDao(private val database: Database) {
    suspend fun getBySpecificationId(id: Long): List<OperationType> = safetySuspendTransactionAsync(database) {
        OperationTypes
            .select { OperationTypes.specificationId eq id }
            .map(::extractOperationType)
    }

    suspend fun create(operationType: OperationType): OperationType = safetySuspendTransactionAsync(database) {
        val id = OperationTypes.insertAndGetId {
            it[type] = operationType.type
            it[sequenceNumber] = operationType.sequenceNumber
            it[specificationId] = operationType.specificationId
        }
        operationType.copy(id = id.value)
    }

    suspend fun batchCreate(operationTypes: List<OperationType>): List<OperationType> = safetySuspendTransactionAsync(database) {
            OperationTypes.batchInsert(operationTypes) { operationType: OperationType ->
                this[OperationTypes.type] = operationType.type
                this[OperationTypes.sequenceNumber] = operationType.sequenceNumber
                this[OperationTypes.specificationId] = operationType.specificationId
            }
                .map(::extractOperationType)
        }

    suspend fun update(operationType: OperationType) = safetySuspendTransactionAsync(database) {
        OperationTypes.update({ OperationTypes.id eq operationType.id }) {
            it[type] = operationType.type
            it[sequenceNumber] = operationType.sequenceNumber
            it[specificationId] = operationType.specificationId
        }
    }

    suspend fun deleteById(id: Long) = safetySuspendTransactionAsync(database) {
        OperationTypes.deleteWhere { OperationTypes.id eq id }
    }

    suspend fun deleteByIdList(idList: List<Long>) = safetySuspendTransactionAsync(database) {
        OperationTypes.deleteWhere { OperationTypes.id inList(idList)}
    }

    private fun extractOperationType(row: ResultRow): OperationType =
        OperationType(
            id = row[OperationTypes.id].value,
            type = row[OperationTypes.type],
            sequenceNumber = row[OperationTypes.sequenceNumber],
            specificationId = row[OperationTypes.specificationId].value
        )
}