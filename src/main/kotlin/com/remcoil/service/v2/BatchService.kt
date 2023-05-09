package com.remcoil.service.v2

import com.remcoil.dao.v2.BatchDao
import com.remcoil.data.model.v2.Batch
import com.remcoil.data.model.v2.BatchProgress
import com.remcoil.data.model.v2.ExtendedAction
import com.remcoil.data.model.v2.Kit
import com.remcoil.utils.exceptions.EntryDoesNotExistException
import com.remcoil.utils.logger
import kotlin.math.min

class BatchService(
    private val batchDao: BatchDao,
    private val productService: ProductService,
    private val actionService: ActionService,
    private val controlActionService: ControlActionService
) {
    suspend fun getAllBatches(): List<Batch> {
        val batches = batchDao.getAll()
        logger.info("Отдали все партии")
        return batches
    }

    suspend fun getBatchById(id: Long): Batch {
        val batch = batchDao.getById(id)
        logger.info("Отдали партию с id = $id")
        return batch ?: throw EntryDoesNotExistException("Партии с id - $id не существует")
    }

    suspend fun getBatchesByKitId(id: Long): List<Batch> {
        val batches = batchDao.getByKitId(id)
        logger.info("Отдали партии для набора - $id")
        return batches
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

        // Get old batches
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

        logger.info("Обновили размер партий")
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

        logger.info("Обновили число партий")
    }

    suspend fun getBatchesProgressByKitId(id: Long): List<BatchProgress> {
        val batches = getBatchesByKitId(id)
        val actionsByBatch = actionService.getActionsByKitId(id).filter { it.active }.groupBy { it.batchId }
        val controlActionsByBatch = controlActionService.getControlActionsByKitId(id).filter { it.active }.groupBy { it.batchId }
        val defectedProductsQuantityByBatch = productService.getProductsByBatchesId(batches.map { it.id })
            .filter { !it.active }
            .groupingBy { it.batchId }
            .eachCount()

        val batchesProgress = mutableListOf<BatchProgress>()
        for (batch in batches) {
            val operationProgress = mutableMapOf<Long, Int>()
            val controlProgress = mutableMapOf<String, Int>()
            val repairOperations = mutableListOf<ExtendedAction>()
            val lockedQuantity = mutableSetOf<Long>()
            val defectedQuantity = defectedProductsQuantityByBatch[batch.id] ?: 0

            for (action in actionsByBatch[batch.id] ?: listOf()) {
                if (action.repair) {
                    repairOperations.add(action)
                } else {
                    operationProgress.merge(action.operationType, 1) { oldValue, _ -> oldValue + 1 }
                }
            }

            for (controlAction in controlActionsByBatch[batch.id] ?: listOf()) {
                if (controlAction.successful) {
                    controlProgress.merge(controlAction.controlType, 1) { oldValue, _ -> oldValue + 1 }
                } else {
                    if (repairOperations.find {
                            it.productId == controlAction.productId &&
                                    it.operationType == controlAction.operationType &&
                                    it.doneTime > controlAction.doneTime
                        } == null) {
                        lockedQuantity.add(controlAction.productId)
                        operationProgress.computeIfPresent(controlAction.operationType) { _, v -> v - 1 }
                    }
                }
            }

            batchesProgress.add(
                BatchProgress(
                    batch.id,
                    batch.batchNumber,
                    operationProgress,
                    controlProgress,
                    lockedQuantity.size,
                    defectedQuantity
                )
            )
        }

        return batchesProgress
    }


}