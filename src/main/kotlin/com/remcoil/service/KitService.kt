package com.remcoil.service

import com.remcoil.dao.KitDao
import com.remcoil.model.dto.*
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

    suspend fun getKitProgressById(id: Long): KitDetailedProgress {
        val kit = getKitById(id)
        val operationTypes = operationTypeService.getOperationTypesBySpecificationId(kit.specificationId)
        val batchesProgress = batchService.getBatchesProgressByKitId(kit.id)

        return KitDetailedProgress(
            kit,
            operationTypes,
            batchesProgress
        )
    }

    suspend fun getKitsProgress(): MutableList<KitBriefProgress> {
        val kitsProgress = mutableListOf<KitBriefProgress>()

        val kitsBySpecificationId = kitDao.getAllWithSpecification().groupBy { it.specificationId }

        for (specification in kitsBySpecificationId) {
            val operationTypes = operationTypeService.getOperationTypesBySpecificationId(specification.key)
                .sortedBy { it.sequenceNumber }
            val actionsByKitId =
                actionService.getActionsBySpecificationId(specification.key).filter { it.active }.groupBy { it.kitId }
            val controlActionsByKitId =
                controlActionService.getControlActionsBySpecificationId(specification.key).filter { it.active }
                    .groupBy { it.kitId }
            val defectedProductsCountByKitId = productService.getProductsBySpecificationId(specification.key)
                .filter { !it.active }
                .groupingBy { it.kitId }
                .eachCount()

            for (kit in specification.value) {
                val kitBriefProgress = getKitBriefProgress(
                    kit,
                    operationTypes,
                    actionsByKitId,
                    controlActionsByKitId,
                    defectedProductsCountByKitId[kit.id] ?: 0
                )
                kitsProgress.add(kitBriefProgress)
            }

        }

        return kitsProgress
    }

    private fun getKitBriefProgress(
        kit: ExtendedKit,
        operationTypes: List<OperationType>,
        actionsByKitId: Map<Long, List<ExtendedAction>>,
        controlActionsByKitId: Map<Long, List<ExtendedControlAction>>,
        defectedProductsQuantity: Int
    ): KitBriefProgress {
        var productsInWork = 0
        var productsDone = 0
        val lockedProductsIdSet = mutableSetOf<Long>()
        val repairOperations = mutableListOf<ExtendedAction>()
        val controlProgress = mutableMapOf(ControlType.OTK to 0, ControlType.TESTING to 0)

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
                controlProgress[controlAction.controlType] = controlProgress[controlAction.controlType]!! + 1
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

        return KitBriefProgress(
            kit,
            productsInWork,
            productsDone,
            controlProgress,
            lockedProductsIdSet.size,
            defectedProductsQuantity
        )
    }
}