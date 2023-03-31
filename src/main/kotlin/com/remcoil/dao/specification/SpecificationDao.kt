package com.remcoil.dao.specification

import com.remcoil.data.database.ExtendedSpecifications
import com.remcoil.data.database.Specifications
import com.remcoil.data.model.specification.Specification
import com.remcoil.data.model.specification.SpecificationResponseDto
import com.remcoil.utils.safetySuspendTransactionAsync
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class SpecificationDao(private val database: Database) {

    suspend fun getAll(): List<SpecificationResponseDto> = safetySuspendTransactionAsync(database) {
        ExtendedSpecifications
            .selectAll()
            .map(::extractExtendedSpecification)
    }

    suspend fun getById(id: Long): SpecificationResponseDto? = safetySuspendTransactionAsync(database) {
        ExtendedSpecifications
            .select { ExtendedSpecifications.id eq id }
            .map(::extractExtendedSpecification)
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

    private fun extractExtendedSpecification(row: ResultRow): SpecificationResponseDto = SpecificationResponseDto(
        row[ExtendedSpecifications.id],
        row[ExtendedSpecifications.specificationTitle],
        row[ExtendedSpecifications.productType],
        row[ExtendedSpecifications.testedPercentage],
        row[ExtendedSpecifications.kitQuantity],
        setOf()
    )
}