package com.remcoil.service.v2

import com.remcoil.dao.v2.ProductDao
import com.remcoil.data.model.v2.Batch
import com.remcoil.data.model.v2.Product
import com.remcoil.utils.exceptions.EntryDoesNotExistException
import com.remcoil.utils.logger

class ProductService(private val productDao: ProductDao) {

    suspend fun getAllProducts(): List<Product> {
        val products = productDao.getAll()
        logger.info("Отдали все изделия")
        return products
    }

    private suspend fun getProductById(id: Long): Product {
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
        val product = productDao.getPureById(id) ?: throw EntryDoesNotExistException("Изделия с id = $id не существует")
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
}