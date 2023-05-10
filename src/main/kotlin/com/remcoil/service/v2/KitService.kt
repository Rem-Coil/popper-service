package com.remcoil.service.v2

import com.remcoil.dao.v2.KitDao
import com.remcoil.data.model.v2.ExtendedAction
import com.remcoil.data.model.v2.Kit
import com.remcoil.data.model.v2.KitFullProgress
import com.remcoil.data.model.v2.KitShortProgress
import com.remcoil.utils.exceptions.EntryDoesNotExistException
import com.remcoil.utils.logger


class KitService(
    private val kitDao: KitDao,
    private val batchService: BatchService,
    private val operationTypeService: OperationTypeService,
    private val productService: ProductService,
    private val actionService: ActionService,
    private val controlActionService: ControlActionService
) {

    suspend fun getAllKits(): List<Kit> {
        val kits = kitDao.getAll()
        logger.info("Вернули все наборы")
        return kits
    }

    suspend fun getKitById(id: Long): Kit {
        val kit = kitDao.getById(id) ?: throw EntryDoesNotExistException("Набор с id = $id не существует")
        logger.info("Вернули данные от наборе - $id")
        return kit
    }

    suspend fun deleteKitById(id: Long) {
        kitDao.deleteById(id)
        logger.info("Данные о ТЗ удалены")
    }

    suspend fun createKit(kit: Kit): Kit {
        val created = kitDao.create(kit)
        logger.info("Создан набор - ${created.kitNumber}")
        batchService.createByKit(created, startNumber = 1)
        return created
    }

    suspend fun updateKit(kit: Kit) {
        val oldKit = getKitById(kit.id)
        kitDao.update(kit)

        batchService.updateBatchSize(oldKit, kit)
        batchService.updateBatchesQuantity(oldKit, kit)

        logger.info("Обновили набор с id = ${kit.id}")
    }

    suspend fun getKitProgressById(id: Long): KitFullProgress {
        val kit = getKitById(id)
        val operationTypes = operationTypeService.getOperationTypesBySpecificationId(kit.specificationId)
        val batchesProgress = batchService.getBatchesProgressByKitId(kit.id)

        return KitFullProgress(
            kit,
            operationTypes,
            batchesProgress
        )
    }

    suspend fun getKitProgressBySpecificationId(id: Long): MutableList<KitShortProgress> {
        val kits = kitDao.getBySpecificationId(id)
        val operationTypes = operationTypeService.getOperationTypesBySpecificationId(id).sortedBy { it.sequenceNumber }
        val actionsByKitId = actionService.getActionsBySpecificationId(id).filter { it.active }.groupBy { it.kitId }
        val controlActionsByKitId = controlActionService.getControlActionsBySpecificationId(id).filter { it.active }.groupBy { it.kitId }
        val defectedProductsCountByKitId = productService.getProductsBySpecificationId(id)
            .filter { !it.active }
            .groupingBy { it.kitId }
            .eachCount()

        val kitsProgress = mutableListOf<KitShortProgress>()

        for (kit in kits) {
            var productsInWork = 0
            var productsDone = 0
            val lockedProductsIdSet = mutableSetOf<Long>()
            val repairOperations = mutableListOf<ExtendedAction>()
            val defectedProductsQuantity = defectedProductsCountByKitId[kit.id] ?: 0
            val controlProgress = mutableMapOf<String, Int>()

            for (action in actionsByKitId[kit.id] ?: listOf()) {
                if (action.operationType == operationTypes.first().id && !action.repair) {
                    productsInWork++
                }
                if (action.operationType == operationTypes.last().id && !action.repair) {
                    productsDone++
                }
                if (action.repair) {
                    repairOperations.add(action)
                }
            }

            for (controlAction in controlActionsByKitId[kit.id] ?: listOf()) {
                if (controlAction.successful) {
                    controlProgress.merge(controlAction.controlType, 1) { quantity, _ -> quantity + 1 }
                } else {
                    if (repairOperations.find {
                            it.productId == controlAction.productId &&
                                    it.operationType == controlAction.operationType &&
                                    it.doneTime > controlAction.doneTime
                        } == null) {
                        lockedProductsIdSet.add(controlAction.productId)
                        if (controlAction.operationType == operationTypes.last().id) {
                            productsDone--
                        }
                    }
                }
            }

            kitsProgress.add(
                KitShortProgress(
                    kit,
                    productsInWork,
                    productsDone,
                    controlProgress,
                    lockedProductsIdSet.size,
                    defectedProductsQuantity
                )
            )
        }

        return kitsProgress
    }
}