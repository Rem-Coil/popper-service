package com.remcoil.service

import com.remcoil.dao.BatchDao
import com.remcoil.model.dto.*
import com.remcoil.utils.exceptions.EntryDoesNotExistException
import kotlin.math.min

class BatchService(
    private val batchDao: BatchDao,
    private val productService: ProductService,
    private val actionService: ActionService,
    private val controlActionService: ControlActionService
) {
    suspend fun getAllBatches(): List<Batch> {
        return batchDao.getAll()
    }

    suspend fun getBatchById(id: Long): Batch {
        return batchDao.getById(id) ?: throw EntryDoesNotExistException("Batch with id = $id not found")
    }

    suspend fun getBatchesByKitId(id: Long): List<Batch> {
        return batchDao.getByKitId(id)
    }

    suspend fun createByKit(kit: Kit, startNumber: Int) {
        val batches = mutableListOf<Batch>()
        for (i in startNumber..kit.batchesQuantity) {
            batches.add(Batch(batchNumber = i, kitId = kit.id))
        }
        val createdBatches = batchDao.batchCreate(batches)
        productService.createByKitAndBatches(kit, createdBatches)
    }

    suspend fun updateBatchSize(old: Kit, kit: Kit) {
        if (old.batchSize == kit.batchSize) {
            return
        }

        val batches = batchDao
            .getByKitId(kit.id)
            .take(min(old.batchesQuantity, kit.batchesQuantity))

        for (batch in batches) {
            if (old.batchSize > kit.batchSize) {
                productService.reduceProductsQuantity(
                    batch.id,
                    excessNumber = old.batchSize - kit.batchSize
                )
            } else {
                productService.increaseProductsQuantity(
                    batch.id,
                    requiredNumber = kit.batchSize - old.batchSize
                )
            }
        }
    }

    suspend fun updateBatchesQuantity(old: Kit, kit: Kit) {
        if (old.batchesQuantity == kit.batchesQuantity) {
            return
        }

        if (old.batchesQuantity > kit.batchesQuantity) {
            val batches = batchDao.getByKitId(kit.id)
            for (i in 1..old.batchesQuantity - kit.batchesQuantity) {
                batchDao.deleteById(batches[batches.size - i].id)
            }
        } else {
            for (i in 1..kit.batchesQuantity - old.batchesQuantity) {
                createByKit(kit, old.batchesQuantity + i)
            }
        }
    }

    suspend fun getBatchesProgressByKitId(id: Long): List<BatchProgress> {
        val batches = getBatchesByKitId(id)
        val actionsByBatchId = actionService.getActionsByKitId(id).filter { it.active }.groupBy { it.batchId }
        val controlActionsByBatchId =
            controlActionService.getControlActionsByKitId(id).filter { it.active }.groupBy { it.batchId }
        val defectedProductsQuantityByBatchId = productService.getProductsByBatchesId(batches.map { it.id })
            .filter { !it.active }
            .groupingBy { it.batchId }
            .eachCount()

        val batchesProgress = mutableListOf<BatchProgress>()

        for (batch in batches) {
            val operationProgress = mutableMapOf<Long, Int>()
            val controlProgress = mutableMapOf(ControlType.OTK to 0, ControlType.TESTING to 0)
            val repairOperations = mutableListOf<ExtendedAction>()
            val lockedProductsIdSet = mutableSetOf<Long>()
            val defectedProductsQuantity = defectedProductsQuantityByBatchId[batch.id] ?: 0

            for (action in actionsByBatchId[batch.id] ?: listOf()) {
                if (action.repair) {
                    repairOperations.add(action)
                } else {
                    operationProgress.merge(action.operationType, 1) { quantity, _ -> quantity + 1 }
                }
            }

            for (controlAction in controlActionsByBatchId[batch.id] ?: listOf()) {
                if (controlAction.successful) {
                    controlProgress[controlAction.controlType] = controlProgress[controlAction.controlType]!! + 1
                } else {
                    if (controlAction.needRepair && repairOperations.find {
                            it.productId == controlAction.productId &&
                                    it.operationType == controlAction.operationType &&
                                    it.doneTime > controlAction.doneTime
                        } == null) {
                        lockedProductsIdSet.add(controlAction.productId)
                        operationProgress.computeIfPresent(controlAction.operationType) { _, quantity -> quantity - 1 }
                    }
                }
            }

            batchesProgress.add(
                BatchProgress(
                    batch.id,
                    batch.batchNumber,
                    operationProgress,
                    controlProgress,
                    lockedProductsIdSet.size,
                    defectedProductsQuantity
                )
            )
        }

        return batchesProgress
    }

    suspend fun clearActionHistoryById(id: Long) {
        val productsIdList = productService.getProductsByBatchId(id).map { it.id }
        actionService.deleteActionsByProducts(productsIdList)
        controlActionService.deleteControlActionsByProducts(productsIdList)
        productService.deleteInactiveProductBuBatchId(id)
    }
}