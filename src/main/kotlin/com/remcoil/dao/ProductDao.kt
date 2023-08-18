package com.remcoil.dao

import com.remcoil.model.database.Batches
import com.remcoil.model.database.Kits
import com.remcoil.model.database.Products
import com.remcoil.model.dto.ExtendedProduct
import com.remcoil.model.dto.Product
import com.remcoil.utils.safetySuspendTransactionAsync
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList

class ProductDao(private val database: Database) {

    suspend fun getAll(): List<Product> = safetySuspendTransactionAsync(database) {
        Products
            .selectAll()
            .map(::extractProduct)
    }

    suspend fun getBySpecificationId(id: Long): List<ExtendedProduct> = safetySuspendTransactionAsync(database) {
        (Products leftJoin Batches leftJoin Kits)
            .slice(Products.id, Products.productNumber, Products.active, Products.locked, Products.batchId, Batches.kitId)
            .select { Kits.specificationId eq id }
            .map(::extractExtendedProduct)
    }

    suspend fun getByBatchesId(idList: List<Long>): List<Product> = safetySuspendTransactionAsync(database) {
        Products
            .select { Products.batchId inList (idList) }
            .map(::extractProduct)
    }

    suspend fun getByBatchId(id: Long): List<Product> = safetySuspendTransactionAsync(database) {
        Products
            .select { Products.batchId eq id }
            .map(::extractProduct)
    }

    suspend fun getById(id: Long): Product? = safetySuspendTransactionAsync(database) {
        Products
            .select { Products.id eq id }
            .map(::extractProduct)
            .firstOrNull()
    }

    suspend fun create(product: Product): Product = safetySuspendTransactionAsync(database) {
        val id = Products.insertAndGetId {
            it[batchId] = product.batchId
            it[productNumber] = product.productNumber
            it[active] = product.active
            it[active] = product.locked
        }
        product.copy(id = id.value)
    }

    suspend fun deleteByIdList(idList: List<Long>) = safetySuspendTransactionAsync(database) {
        Products.deleteWhere { Products.id inList idList }
    }

    suspend fun deleteInactiveByBatchId(id: Long) = safetySuspendTransactionAsync(database) {
        Products.deleteWhere { not(active) and (batchId eq id) }
    }

    suspend fun update(product: Product) = safetySuspendTransactionAsync(database) {
        Products.update({ Products.id eq product.id }) {
            it[batchId] = product.batchId
            it[productNumber] = product.productNumber
            it[active] = product.active
            it[active] = product.locked
        }
    }

    suspend fun batchCreate(products: List<Product>) = safetySuspendTransactionAsync(database) {
        Products.batchInsert(products) { product: Product ->
            this[Products.batchId] = product.batchId
            this[Products.productNumber] = product.productNumber
            this[Products.active] = product.active
            this[Products.locked] = product.locked
        }
            .map(::extractProduct)
    }

    suspend fun setLockValueById(id: Long, lock: Boolean) = safetySuspendTransactionAsync(database) {
        Products.update({Products.id eq id}) {
            it[locked] = lock
        }
    }

    private fun extractProduct(row: ResultRow): Product = Product(
        row[Products.id].value,
        row[Products.productNumber],
        row[Products.active],
        row[Products.locked],
        row[Products.batchId].value
    )

    private fun extractExtendedProduct(row: ResultRow): ExtendedProduct =
        ExtendedProduct(
            row[Products.id].value,
            row[Products.productNumber],
            row[Products.active],
            row[Products.locked],
            row[Products.batchId].value,
            row[Batches.kitId].value
        )
}
