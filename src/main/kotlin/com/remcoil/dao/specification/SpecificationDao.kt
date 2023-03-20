package com.remcoil.dao.specification

import com.remcoil.data.database.Specifications
import com.remcoil.data.model.specification.Specification
import com.remcoil.utils.safetySuspendTransactionAsync
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class SpecificationDao(private val database: Database) {

    suspend fun getAll(): List<Specification> = safetySuspendTransactionAsync(database) {
        Specifications
            .selectAll()
            .map(::extractSpecification)
    }

    suspend fun getById(id: Long): Specification? = safetySuspendTransactionAsync(database) {
        Specifications
            .select { Specifications.id eq id }
            .map(::extractSpecification)
            .firstOrNull()
    }

    suspend fun update(specification: Specification) = safetySuspendTransactionAsync(database) {
        Specifications.update({ Specifications.id eq specification.id }) {
            it[specificationTitle] = specification.specificationTitle
            it[productType] = specification.productType
            it[testedPercentage] = specification.testedPercentage
        }
    }

    suspend fun create(specification: Specification): Specification = safetySuspendTransactionAsync(database) {
        val id = Specifications.insertAndGetId {
            it[specificationTitle] = specification.specificationTitle
            it[productType] = specification.productType
            it[testedPercentage] = specification.testedPercentage
        }
        specification.copy(id = id.value)
    }

    suspend fun deleteById(id: Long) = safetySuspendTransactionAsync(database) {
        Specifications.deleteWhere { Specifications.id eq id }
    }

    private fun extractSpecification(row: ResultRow): Specification = Specification(
        row[Specifications.id].value,
        row[Specifications.specificationTitle],
        row[Specifications.productType],
        row[Specifications.testedPercentage]
    )
}