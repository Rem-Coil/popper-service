package com.remcoil.service.v2

import com.remcoil.dao.v2.SpecificationDao
import com.remcoil.data.model.v2.Specification
import com.remcoil.data.model.v2.SpecificationResponse
import com.remcoil.utils.exceptions.EntryDoesNotExistException
import com.remcoil.utils.logger

class SpecificationService(
    private val specificationDao: SpecificationDao,
) {

    suspend fun getAllSpecifications(): List<SpecificationResponse> {
        val specifications = specificationDao.getAll()
        logger.info("Вернули все ТЗ")
        return specifications
    }

    suspend fun getSpecificationById(id: Long): SpecificationResponse {
        val specification = specificationDao.getById(id) ?: throw EntryDoesNotExistException("ТЗ с id = $id не существует")
        logger.info("Вернули данные от ТЗ - $id")
        return specification
    }

    suspend fun deleteSpecificationById(id: Long) {
        specificationDao.deleteById(id)
        logger.info("Данные о ТЗ удалены")
    }

    suspend fun createSpecification(specification: Specification): SpecificationResponse {
        val createdSpecification = specificationDao.create(specification)
        logger.info("Создано ТЗ - ${createdSpecification.specificationTitle}")
        return SpecificationResponse(createdSpecification, 0)
    }

    suspend fun updateSpecification(specification: Specification): SpecificationResponse {
        specificationDao.update(specification)
        logger.info("Обновили ТЗ")
        return getSpecificationById(specification.id)
    }
}