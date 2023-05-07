package com.remcoil.dao.v2

import com.remcoil.data.database.v2.Products
import com.remcoil.data.model.v2.Product
import com.remcoil.utils.safetySuspendTransactionAsync
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList

class ProductDao(private val database: Database) {

    suspend fun getAll(): List<Product> = safetySuspendTransactionAsync(database) {
        Products
            .selectAll()
            .map(::extractProduct)
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
        }
        product.copy(id = id.value)
    }

    suspend fun deleteByIdList(idList: List<Long>) = safetySuspendTransactionAsync(database) {
        Products.deleteWhere { Products.id inList idList }
    }

    suspend fun update(product: Product) = safetySuspendTransactionAsync(database) {
        Products.update({ Products.id eq product.id }) {
            it[batchId] = product.batchId
            it[productNumber] = product.productNumber
            it[active] = product.active
        }
    }

    suspend fun batchCreate(products: List<Product>) = safetySuspendTransactionAsync(database) {
        Products.batchInsert(products) { product: Product ->
            this[Products.batchId] = product.batchId
            this[Products.productNumber] = product.productNumber
            this[Products.active] = product.active
        }
            .map(::extractProduct)
    }

    private fun extractProduct(row: ResultRow): Product = Product(
        row[Products.id].value,
        row[Products.productNumber],
        row[Products.active],
        row[Products.batchId].value
    )
}
