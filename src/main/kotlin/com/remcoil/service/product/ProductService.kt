package com.remcoil.service.product

import com.remcoil.dao.product.ProductDao
import com.remcoil.data.model.batch.Batch
import com.remcoil.data.model.product.Product
import com.remcoil.utils.exceptions.EntryDoesNotExistException
import com.remcoil.utils.logger

class ProductService(private val productDao: ProductDao) {

    suspend fun getAllProducts(): List<Product> {
        val products = productDao.getAll()
        logger.info("Отдали все изделия")
        return products
    }

    suspend fun getProductById(id: Long): Product {
        val product = productDao.getById(id)
        logger.info("Отдали изделие - $id")
        return product ?: throw EntryDoesNotExistException("Изделия с id = $id не существует")
    }

    suspend fun getProductsByBatchId(id: Long): List<Product> {
        val products = productDao.getByBatchId(id)
        logger.info("Отдали изделия партии - $id")
        return products
    }

    suspend fun getProductsByBatches(batches: List<Batch>): List<Product> {
        val products = productDao.getByBatchesId(batches.map { batch -> batch.id })
        logger.info("Отдали изделия для партий $batches")
        return products
    }

    suspend fun createProduct(product: Product): Product {
        val createdProduct = productDao.create(product)
        logger.info("Добавили изделие с id = ${createdProduct.id}")
        return createdProduct
    }

    suspend fun deleteProductById(id: Long) {
        productDao.deleteById(id)
        logger.info("Удалили изделие с id = $id")
    }

    suspend fun deactivateProductById(id: Long) {
        val product = getProductById(id)
        productDao.deactivateById(id, product.productNumber)
        logger.info("Забраковали изделие с id = $id")
        product.active = true
        createProduct(product)
    }

    suspend fun productIsActive(productId: Long): Boolean {
        return try {
            val product = getProductById(productId)
            product.active
        } catch (e: EntryDoesNotExistException) {
            false
        }
    }

    suspend fun updateProduct(product: Product) {
        productDao.update(product)
        logger.info("Обновили изделие с id = ${product.id}")
    }
}