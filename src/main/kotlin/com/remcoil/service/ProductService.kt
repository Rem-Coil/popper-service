package com.remcoil.service

import com.remcoil.dao.ProductDao
import com.remcoil.utils.exceptions.EntryDoesNotExistException
import com.remcoil.utils.logger

class ProductService(
    private val productDao: ProductDao
) {

    suspend fun getAllProducts(): List<com.remcoil.model.dto.Product> {
        val products = productDao.getAll()
        logger.info("Отдали все изделия")
        return products
    }

    suspend fun getProductsBySpecificationId(id: Long): List<com.remcoil.model.dto.ExtendedProduct> {
        return productDao.getBySpecificationId(id)
    }

    suspend fun getProductsByBatchId(id: Long): List<com.remcoil.model.dto.Product> {
        val products = productDao.getByBatchId(id)
        logger.info("Отдали изделия партии - $id")
        return products
    }

    suspend fun getProductsByBatchesId(idList: List<Long>): List<com.remcoil.model.dto.Product> {
        return productDao.getByBatchesId(idList)
    }

    suspend fun getProductById(id:Long): com.remcoil.model.dto.Product {
        return productDao.getById(id) ?: throw EntryDoesNotExistException("Продукта с id=$id не существует")
    }

    private suspend fun createProduct(product: com.remcoil.model.dto.Product): com.remcoil.model.dto.Product {
        val createdProduct = productDao.create(product)
        logger.info("Добавили изделие с id = ${createdProduct.id}")
        return createdProduct
    }

    suspend fun createByKitAndBatches(kit: com.remcoil.model.dto.Kit, batches: List<com.remcoil.model.dto.Batch>) {
        val products = mutableListOf<com.remcoil.model.dto.Product>()
        for (batch in batches) {
            for (i in 1..kit.batchSize) {
                products.add(com.remcoil.model.dto.Product(batchId = batch.id, productNumber = i))
            }
        }
        productDao.batchCreate(products)
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
        val products = mutableListOf<com.remcoil.model.dto.Product>()
        val startNumber = getLastNumberByBatchId(batchId)
        for (i in 1..requiredNumber) {
            products.add(
                com.remcoil.model.dto.Product(
                    productNumber = startNumber + i,
                    batchId = batchId
                )
            )
        }
        productDao.batchCreate(products)
    }

    private suspend fun getLastNumberByBatchId(batchId: Long): Int {
        return productDao.getByBatchId(batchId)
            .filter { it.active }
            .maxOf { it.productNumber }
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