package com.remcoil.service.v2

import com.remcoil.dao.v2.ProductDao
import com.remcoil.data.model.v2.Batch
import com.remcoil.data.model.v2.Kit
import com.remcoil.data.model.v2.Product
import com.remcoil.utils.exceptions.EntryDoesNotExistException
import com.remcoil.utils.logger

class ProductService(
    private val productDao: ProductDao
) {

    suspend fun getAllProducts(): List<Product> {
        val products = productDao.getAll()
        logger.info("Отдали все изделия")
        return products
    }

    suspend fun getProductsByBatchId(id: Long): List<Product> {
        val products = productDao.getByBatchId(id)
        logger.info("Отдали изделия партии - $id")
        return products
    }

    private suspend fun createProduct(product: Product): Product {
        val createdProduct = productDao.create(product)
        logger.info("Добавили изделие с id = ${createdProduct.id}")
        return createdProduct
    }

    suspend fun createByKitAndBatches(kit: Kit, batches: List<Batch>) {
        val products = mutableListOf<Product>()
        for (batch in batches) {
            for (i in 1..kit.batchSize) {
                products.add(Product(batchId = batch.id, productNumber = i.toString()))
            }
        }
        productDao.batchCreate(products)
    }

    suspend fun deleteProductById(id: Long) {
        productDao.deleteById(id)
        logger.info("Удалили изделие с id = $id")
    }

    suspend fun reduceProductsQuantity(batchId: Long, excessNumber: Int) {
        val products = productDao.getByBatchId(batchId).filter { it.active }
        val productsToDelete = mutableListOf<Long>()
        for (i in 1..excessNumber) {
            productsToDelete.add(products[products.size - i].id)
        }
        productDao.deleteByIdList(productsToDelete)
    }

    suspend fun increaseProductsQuantity(batchId: Long, requiredNumber: Int) {
        val products = mutableListOf<Product>()
        val startNumber = getLastNumberByBatchId(batchId)
        for (i in 1..requiredNumber) {
            products.add(
                Product(
                    productNumber = "${startNumber + i}",
                    batchId = batchId
                )
            )
        }
        productDao.batchCreate(products)
    }

    private suspend fun getLastNumberByBatchId(batchId: Long): Int {
        return productDao.getByBatchId(batchId)
            .filter { it.active }
            .maxOf { it.productNumber.toInt() }
    }

    suspend fun deactivateProductById(id: Long) {
        val product = productDao.getById(id) ?: throw EntryDoesNotExistException("Изделия с id = $id не существует")
        productDao.update(product.deactivated())
        logger.info("Забраковали изделие с id = $id")
        createProduct(product)
    }

    suspend fun productIsActive(productId: Long): Boolean {
        return productDao.getById(productId)?.active ?: false
    }
}