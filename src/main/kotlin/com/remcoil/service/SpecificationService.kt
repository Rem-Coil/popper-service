package com.remcoil.service

import com.remcoil.dao.SpecificationDao
import com.remcoil.model.dto.SpecificationPostRequest
import com.remcoil.model.dto.SpecificationPutRequest
import com.remcoil.model.dto.SpecificationResponse
import com.remcoil.utils.exceptions.EntryDoesNotExistException

class SpecificationService(
    private val specificationDao: SpecificationDao,
    private val operationTypeService: OperationTypeService
) {

    suspend fun getAllSpecifications(): List<SpecificationResponse> {
        val specifications = specificationDao.getAll()
        for (specificationResponse in specifications) {
            specificationResponse.operationTypes =
                operationTypeService.getOperationTypesBySpecificationId(specificationResponse.id)
        }
        return specifications
    }

    suspend fun getSpecificationById(id: Long): SpecificationResponse {
        val specification =
            specificationDao.getById(id) ?: throw EntryDoesNotExistException("ТЗ с id = $id не существует")
        specification.operationTypes = operationTypeService.getOperationTypesBySpecificationId(specification.id)
        return specification
    }

    suspend fun deleteSpecificationById(id: Long) {
        specificationDao.deleteById(id)
    }

    suspend fun createSpecification(specificationRequest: SpecificationPostRequest): SpecificationResponse {
        val createdSpecification = specificationDao.create(specificationRequest.getSpecification())
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
        operationTypeService.updateBatchOperationTypes(
            specificationPutRequest.operationTypes,
            oldSpecification.operationTypes
        )
    }
}