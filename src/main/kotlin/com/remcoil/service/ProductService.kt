package com.remcoil.service

import com.remcoil.dao.ProductDao
import com.remcoil.model.dto.Batch
import com.remcoil.model.dto.Kit
import com.remcoil.model.dto.Product
import com.remcoil.utils.exceptions.EntryDoesNotExistException

class ProductService(
    private val productDao: ProductDao
) {

    suspend fun getAllProducts(): List<Product> {
        return productDao.getAll()
    }

    suspend fun getProductsBySpecificationId(id: Long): List<com.remcoil.model.dto.ExtendedProduct> {
        return productDao.getBySpecificationId(id)
    }

    suspend fun getProductsByBatchId(id: Long): List<Product> {
        return productDao.getByBatchId(id)
    }

    suspend fun getProductsByBatchesId(idList: List<Long>): List<Product> {
        return productDao.getByBatchesId(idList)
    }

    suspend fun getProductById(id:Long): Product {
        return productDao.getById(id) ?: throw EntryDoesNotExistException("Product with id = $id not found")
    }

    suspend fun getProductsByIdList(idList: List<Long>): List<Product> {
        return productDao.getByIdList(idList)
    }

    private suspend fun createProduct(product: Product): Product {
        return productDao.create(product)
    }

    suspend fun createByKitAndBatches(kit: Kit, batches: List<Batch>) {
        val products = mutableListOf<Product>()
        for (batch in batches) {
            for (i in 1..kit.batchSize) {
                products.add(Product(batchId = batch.id, productNumber = i))
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
        val products = mutableListOf<Product>()
        val startNumber = getLastNumberByBatchId(batchId)
        for (i in 1..requiredNumber) {
            products.add(
                Product(
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
        val product = productDao.getById(id) ?: throw EntryDoesNotExistException("Product with id = $id not found")
        productDao.update(product.deactivated())
        createProduct(product.unlocked())
    }

    suspend fun deleteInactiveProductBuBatchId(id: Long) {
        productDao.deleteInactiveByBatchId(id)
    }

    suspend fun setLockValueByProductId(id: Long, lock: Boolean) {
        productDao.setLockValueById(id, lock)
    }
}