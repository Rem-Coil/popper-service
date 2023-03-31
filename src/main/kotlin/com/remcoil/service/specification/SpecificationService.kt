package com.remcoil.service.specification

import com.remcoil.dao.specification.SpecificationActionDao
import com.remcoil.dao.specification.SpecificationDao
import com.remcoil.data.model.specification.Specification
import com.remcoil.data.model.specification.SpecificationRequestDto
import com.remcoil.data.model.specification.SpecificationResponseDto
import com.remcoil.data.model.specification.action.SpecificationAction
import com.remcoil.service.kit.KitService
import com.remcoil.utils.exceptions.EntryDoesNotExistException
import com.remcoil.utils.logger

class SpecificationService(
    private val specificationDao: SpecificationDao,
    private val specificationActionDao: SpecificationActionDao,
    private val kitService: KitService
) {

    suspend fun getAllSpecifications(): List<SpecificationResponseDto> {
        val tasks = specificationDao.getAll()
        logger.info("Вернули все ТЗ")
        for (task in tasks) {
            task.actionSet = specificationActionDao.getBySpecificationId(task.id)
        }
        return tasks
    }

    suspend fun getSpecificationById(id: Long): SpecificationResponseDto {
        val task = specificationDao.getById(id) ?: throw EntryDoesNotExistException("ТЗ с id = $id не существует")
        logger.info("Вернули данные от ТЗ - $id")
        task.actionSet = specificationActionDao.getBySpecificationId(task.id)
        return task
    }

    suspend fun deleteSpecificationById(id: Long) {
        specificationDao.deleteById(id)
        logger.info("Данные о ТЗ удалены")
    }

    suspend fun createSpecification(specificationRequestDto: SpecificationRequestDto): SpecificationResponseDto {
        val createdSpecification = specificationDao.create(Specification(specificationRequestDto))
        logger.info("Создано ТЗ - ${createdSpecification.specificationTitle}")

        val createdActions = specificationActionDao.batchCreate(specificationRequestDto.actionSet.map {
            SpecificationAction(
                id = 0,
                actionType = it.actionType,
                sequenceNumber = it.sequenceNumber,
                specificationId = createdSpecification.id
            )
        })

        return SpecificationResponseDto(createdSpecification, 0, createdActions)
    }

    suspend fun updateSpecification(specificationRequestDto: SpecificationRequestDto): SpecificationResponseDto {
        val oldSpecification = getSpecificationById(specificationRequestDto.id)
        specificationDao.update(Specification(specificationRequestDto))
        logger.info("Обновили ТЗ")

        updateKitNumber(oldSpecification, specificationRequestDto)
        updateActions(oldSpecification, specificationRequestDto)

        return getSpecificationById(specificationRequestDto.id)
    }

    private suspend fun updateActions(
        oldSpecification: SpecificationResponseDto,
        specificationRequestDto: SpecificationRequestDto
    ) {
        if (oldSpecification.actionSet != specificationRequestDto.actionSet) {
            val updatedActionsIdSet = specificationRequestDto.actionSet.map { it.id }
            val actionsToDelete = oldSpecification.actionSet.filter { !updatedActionsIdSet.contains(it.id) }

            for (action in actionsToDelete) {
                specificationActionDao.deleteById(action.id)
            }

            for (action in specificationRequestDto.actionSet) {
                if (specificationActionDao.update(action) < 1) {
                    specificationActionDao.create(action)
                }
            }
            logger.info("Обновили набор операций")
        }
    }

    //  TODO: Убрать каскадное обновление номеров, вычислять при запросе
    private suspend fun updateKitNumber(
        oldSpecification: SpecificationResponseDto,
        specificationRequestDto: SpecificationRequestDto
    ) {
        if (oldSpecification.specificationTitle != specificationRequestDto.specificationTitle) {
            val kits = kitService.getKitsBySpecificationId(specificationRequestDto.id)
            for (kit in kits) {
                val numberTail = kit.kitNumber.substringAfterLast(" / ")
                kit.kitNumber = "${specificationRequestDto.specificationTitle} / $numberTail"
                kitService.updateKit(kit)
            }
            logger.info("Обновили номера")
        }
    }
}