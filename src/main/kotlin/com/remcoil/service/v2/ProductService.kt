package com.remcoil.service.v2

import com.remcoil.dao.v2.ProductDao
import com.remcoil.data.model.v2.Batch
import com.remcoil.data.model.v2.ExtendedProduct
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

    suspend fun getProductsBySpecificationId(id: Long): List<ExtendedProduct> {
        return productDao.getBySpecificationId(id)
    }

    suspend fun getProductsByBatchId(id: Long): List<Product> {
        val products = productDao.getByBatchId(id)
        logger.info("Отдали изделия партии - $id")
        return products
    }

    suspend fun getProductsByBatchesId(idList: List<Long>): List<Product> {
        return productDao.getByBatchesId(idList)
    }

    suspend fun getProductById(id:Long): Product {
        return productDao.getById(id) ?: throw EntryDoesNotExistException("Продукта с id=$id не существует")
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
        val product = productDao.getById(id) ?: throw EntryDoesNotExistException("Изделия с id = $id не существует")
        productDao.update(product.deactivated())
        logger.info("Забраковали изделие с id = $id")
        createProduct(product)
    }

    suspend fun productIsActive(productId: Long): Boolean {
        return productDao.getById(productId)?.active ?: false
    }
}