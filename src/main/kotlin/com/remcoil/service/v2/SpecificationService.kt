package com.remcoil.service.v2

import com.remcoil.dao.v2.SpecificationDao
import com.remcoil.data.model.v2.SpecificationPostRequest
import com.remcoil.data.model.v2.SpecificationPutRequest
import com.remcoil.data.model.v2.SpecificationResponse
import com.remcoil.utils.exceptions.EntryDoesNotExistException
import com.remcoil.utils.logger

class SpecificationService(
    private val specificationDao: SpecificationDao,
    private val operationTypeService: OperationTypeService
) {

    suspend fun getAllSpecifications(): List<SpecificationResponse> {
        val specifications = specificationDao.getAll()
        logger.info("Получили все ТЗ")
        for (specificationResponse in specifications) {
            specificationResponse.operationTypes =
                operationTypeService.getOperationTypesBySpecificationId(specificationResponse.id)
        }
        return specifications
    }

    suspend fun getSpecificationById(id: Long): SpecificationResponse {
        val specification =
            specificationDao.getById(id) ?: throw EntryDoesNotExistException("ТЗ с id = $id не существует")
        logger.info("Получили данные от ТЗ - $id")
        specification.operationTypes = operationTypeService.getOperationTypesBySpecificationId(specification.id)
        return specification
    }

    suspend fun deleteSpecificationById(id: Long) {
        specificationDao.deleteById(id)
        logger.info("Данные о ТЗ удалены")
    }

    suspend fun createSpecification(specificationRequest: SpecificationPostRequest): SpecificationResponse {
        val createdSpecification = specificationDao.create(specificationRequest.getSpecification())
        logger.info("Создано ТЗ - ${createdSpecification.specificationTitle}")
        val createdOperationTypes =
            operationTypeService.createBatchOperationTypes(specificationRequest.operationTypes.map {
                it.toOperationType(
                    createdSpecification.id
                )
            })
        return SpecificationResponse(createdSpecification, 0, createdOperationTypes)
    }

    suspend fun updateSpecification(specificationPutRequest: SpecificationPutRequest) {
        val oldSpecification = getSpecificationById(specificationPutRequest.id)
        specificationDao.update(specificationPutRequest.getSpecification())
        logger.info("Обновили ТЗ")
        operationTypeService.updateBatchOperationTypes(
            specificationPutRequest.operationTypes,
            oldSpecification.operationTypes
        )
    }
}