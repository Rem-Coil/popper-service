package com.remcoil.service.v2

import com.remcoil.dao.v2.OperationTypeDao
import com.remcoil.data.model.v2.OperationType
import com.remcoil.utils.logger

class OperationTypeService(
    private val operationTypeDao: OperationTypeDao
) {
    suspend fun getOperationTypesBySpecificationId(id: Long): List<OperationType> {
        val operationTypes = operationTypeDao.getBySpecificationId(id)
        logger.info("Возвращены типы операции для ТЗ с id=$id")
        return operationTypes
    }

    suspend fun createOperationType(operationType: OperationType): OperationType {
        val createdOperationType = operationTypeDao.create(operationType)
        logger.info("Создан тип операции с id=${createdOperationType.id}")
        return createdOperationType
    }

    suspend fun createBatchOperationTypes(operationTypes: List<OperationType>): List<OperationType> {
        val createdOperationTypes = operationTypeDao.batchCreate(operationTypes)
        logger.info("Создано ${createdOperationTypes.size} типов операций")
        return createdOperationTypes
    }

    suspend fun updateOperationType(operationType: OperationType) {
        operationTypeDao.update(operationType)
        logger.info("Обновлен тип операций с id=${operationType.id}")
    }

    suspend fun deleteOperationTypeById(id: Long) {
        operationTypeDao.deleteById(id)
        logger.info("Удален тип операции с id=$id")
    }

    suspend fun updateBatchOperationTypes(updatedOperationTypes: List<OperationType>, oldOperationTypes: List<OperationType>) {
        if (updatedOperationTypes == oldOperationTypes) {
            return
        }

        val updatedOperationTypesIdSet = updatedOperationTypes.map { it.id }.toSet()
        val deletedOperationTypesIdList =
            oldOperationTypes.filter { !updatedOperationTypesIdSet.contains(it.id) }.map { it.id }

        operationTypeDao.deleteByIdList(deletedOperationTypesIdList)

        for (type in updatedOperationTypes) {
            if (operationTypeDao.update(type) < 1) {
                operationTypeDao.create(type)
            }
        }
        logger.info("Обновлен набор операций")
    }
}