package com.remcoil.dao.v2

import com.remcoil.data.database.v2.Products
import com.remcoil.data.database.v2.view.ExtendedProducts
import com.remcoil.data.model.v2.Product
import com.remcoil.utils.safetySuspendTransactionAsync
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class ProductDao(private val database: Database) {

    suspend fun getAll(): List<Product> = safetySuspendTransactionAsync(database) {
        ExtendedProducts
            .selectAll()
            .map(::extractProduct)
    }

    suspend fun getByBatchId(id: Long): List<Product> = safetySuspendTransactionAsync(database) {
        ExtendedProducts
            .select { ExtendedProducts.batchId eq id }
            .map(::extractProduct)
    }

    suspend fun getByBatchesId(idList: List<Long>): List<Product> = safetySuspendTransactionAsync(database) {
        ExtendedProducts
            .select { ExtendedProducts.batchId inList (idList) }
            .map(::extractProduct)
    }

    suspend fun getById(id: Long): Product? = safetySuspendTransactionAsync(database) {
        ExtendedProducts
            .select { ExtendedProducts.id eq id }
            .map(::extractProduct)
            .firstOrNull()
    }

    suspend fun getPureById(id: Long): Product? = safetySuspendTransactionAsync(database) {
        Products
            .select { Products.id eq id }
            .map(::extractPureProduct)
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

    suspend fun deleteById(id: Long) = safetySuspendTransactionAsync(database) {
        Products.deleteWhere { Products.id eq id }
    }

    suspend fun deactivateById(id: Long, productNumber: String) = safetySuspendTransactionAsync(database) {
        Products.update({ Products.id eq id }) {
            it[active] = false
            it[this.productNumber] = "$productNumber def"
        }
    }

    suspend fun update(product: Product) = safetySuspendTransactionAsync(database) {
        Products.update({ Products.id eq product.id }) {
            it[batchId] = product.batchId
            it[productNumber] = product.productNumber
            it[active] = product.active
        }
    }

    private fun extractPureProduct(row: ResultRow): Product = Product(
        row[Products.id].value,
        row[Products.productNumber],
        row[Products.active],
        row[Products.batchId].value
    )

    private fun extractProduct(row: ResultRow): Product = Product(
        row[ExtendedProducts.id],
        row[ExtendedProducts.productNumber],
        row[ExtendedProducts.active],
        row[ExtendedProducts.batchId]
    )
}
