package com.remcoil.service

import com.remcoil.dao.KitDao
import com.remcoil.model.dto.*
import com.remcoil.utils.exceptions.EntryDoesNotExistException

class KitService(
    private val kitDao: KitDao,
    private val batchService: BatchService,
    private val operationTypeService: OperationTypeService,
    private val productService: ProductService,
    private val actionService: ActionService,
    private val controlActionService: ControlActionService,
    private val acceptanceActionService: AcceptanceActionService
) {

    suspend fun getAllKits(): List<Kit> {
        return kitDao.getAll()
    }

    suspend fun getKitById(id: Long): Kit {
        return kitDao.getById(id) ?: throw EntryDoesNotExistException("Kit with id = $id not found")
    }

    suspend fun deleteKitById(id: Long) {
        kitDao.deleteById(id)
    }

    suspend fun createKit(kit: Kit): Kit {
        val created = kitDao.create(kit)
        batchService.createByKit(created, startNumber = 1)
        return created
    }

    suspend fun updateKit(kit: Kit) {
        val oldKit = getKitById(kit.id)
        kitDao.update(kit)

        batchService.updateBatchSize(oldKit, kit)
        batchService.updateBatchesQuantity(oldKit, kit)
    }

    suspend fun getKitProgressById(id: Long): KitDetailedProgress {
        val kit = getKitById(id)
        val operationTypes = operationTypeService.getOperationTypesBySpecificationId(kit.specificationId)
        val batchesProgress = batchService.getBatchesProgressByKitId(kit)

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
            val actionsByKitId = actionService.getActionsBySpecificationId(specification.key)
                .filter { it.active }
                .groupBy { it.kitId }
            val controlActionsByKitId = controlActionService.getBySpecificationId(specification.key)
                .filter { it.active }
                .groupBy { it.kitId }
            val acceptanceActionsByKitId = acceptanceActionService.getBySpecificationId(specification.key)
                .filter { it.active }
                .groupBy { it.kitId }
            val defectedProductsCountByKitId = productService.getProductsBySpecificationId(specification.key)
                .filter { !it.active }
                .groupingBy { it.kitId }
                .eachCount()

            for (kit in specification.value) {
                val kitBriefProgress = getKitBriefProgress(
                    kit,
                    operationTypes,
                    actionsByKitId[kit.id] ?: listOf(),
                    controlActionsByKitId[kit.id] ?: listOf(),
                    acceptanceActionsByKitId[kit.id] ?: listOf(),
                    defectedProductsCountByKitId[kit.id] ?: 0,
                    )
                kitsProgress.add(kitBriefProgress)
            }

        }

        return kitsProgress
    }

    private fun getKitBriefProgress(
        kit: ExtendedKit,
        operationTypes: List<OperationType>,
        actionsByKitId: List<ExtendedAction>,
        controlActionsByKitId: List<ExtendedControlAction>,
        acceptanceActionsByKitId: List<ExtendedAcceptanceAction>,
        defectedProductsQuantity: Int
    ): KitBriefProgress {
        var batchesAccepted = 0
        var productsInWork = 0
        var productsDone = 0
        val lockedProductsIdSet = mutableSetOf<Long>()
        val repairOperations = mutableListOf<ExtendedAction>()
        val controlProgress = mutableMapOf(ControlType.OTK to 0, ControlType.TESTING to 0)


        for (action in actionsByKitId) {
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

        for (controlAction in controlActionsByKitId) {
            if (controlAction.successful) {
                controlProgress[controlAction.controlType] = controlProgress[controlAction.controlType]!! + 1
            } else {
                if (controlAction.needRepair && repairOperations.find {
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

        for (pair in acceptanceActionsByKitId.groupingBy { it.batchId }.eachCount()) {
            if ((100 * pair.value / kit.batchSize) >= kit.acceptancePercentage) {
                batchesAccepted += 1
            }
        }

        return KitBriefProgress(
            kit,
            batchesAccepted,
            productsInWork,
            productsDone,
            controlProgress,
            lockedProductsIdSet.size,
            defectedProductsQuantity
        )
    }
}