package com.remcoil.service.specification

import com.remcoil.dao.specification.SpecificationActionDao
import com.remcoil.data.model.specification.SpecificationAction
import com.remcoil.utils.exceptions.EntryDoesNotExistException
import com.remcoil.utils.logger

class SpecificationActionService(
    private val specificationActionDao: SpecificationActionDao
) {
    suspend fun getAllSpecificationActions(): List<SpecificationAction> {
        val specificationActions = specificationActionDao.getAll()
        logger.info("Отдали все типы операций")
        return specificationActions
    }

    suspend fun getSpecificationActionById(id: Long): SpecificationAction {
        val specificationAction = specificationActionDao.getById(id)
        logger.info("Отдали тип операции с id - $id")
        return specificationAction ?: throw EntryDoesNotExistException("Типа операции с id - $id не существует")
    }

    suspend fun createSpecificationAction(specificationAction: SpecificationAction): SpecificationAction {
        val createdSpecificationAction = specificationActionDao.create(specificationAction)
        logger.info("Создали тип операции")
        return createdSpecificationAction
    }

    suspend fun updateSpecificationAction(specificationAction: SpecificationAction): SpecificationAction {
        specificationActionDao.update(specificationAction)
        logger.info("Обновили тип операции")
        return specificationAction
    }

    suspend fun deleteSpecificationActionById(id: Long) {
        specificationActionDao.deleteById(id)
        logger.info("Тип операции удален")
    }
}