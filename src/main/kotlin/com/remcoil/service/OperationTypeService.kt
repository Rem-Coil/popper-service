package com.remcoil.service

import com.remcoil.dao.OperationTypeDao
import com.remcoil.model.dto.OperationType

class OperationTypeService(
    private val operationTypeDao: OperationTypeDao
) {
    suspend fun getOperationTypesBySpecificationId(id: Long): List<OperationType> {
        return operationTypeDao.getBySpecificationId(id)
    }

    suspend fun createOperationType(operationType: OperationType): OperationType {
        return operationTypeDao.create(operationType)
    }

    suspend fun createBatchOperationTypes(operationTypes: List<OperationType>): List<OperationType> {
        return operationTypeDao.batchCreate(operationTypes)
    }

    suspend fun updateOperationType(operationType: OperationType) {
        operationTypeDao.update(operationType)
    }

    suspend fun deleteOperationTypeById(id: Long) {
        operationTypeDao.deleteById(id)
    }

    suspend fun updateBatchOperationTypes(
        updatedOperationTypes: List<OperationType>,
        oldOperationTypes: List<OperationType>
    ) {
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
    }
}