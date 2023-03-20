package com.remcoil.service.specification

import com.remcoil.dao.specification.SpecificationDao
import com.remcoil.data.model.specification.Specification
import com.remcoil.service.kit.KitService
import com.remcoil.utils.exceptions.EntryDoesNotExistException
import com.remcoil.utils.logger

class SpecificationService(
    private val specificationDao: SpecificationDao,
    private val kitService: KitService
) {

    suspend fun getAllSpecifications(): List<Specification> {
        val tasks = specificationDao.getAll()
        logger.info("Вернули все ТЗ")
        return tasks
    }

    suspend fun getSpecificationById(id: Long): Specification {
        val task = specificationDao.getById(id)
        logger.info("Вернули данные от ТЗ - $id")
        return task ?: throw EntryDoesNotExistException("ТЗ с id = $id не существует")
    }

//    suspend fun getFullById(taskId: Int): FullTask {
//        val task = getById(taskId)
//        return toFullTask(task)
//    }
//
//    suspend fun getAllFull(): List<FullTask> {
//        val tasks = getAll()
//        val fullTasks = mutableListOf<FullTask>()
//
//        for (task in tasks) {
//            fullTasks.add(toFullTask(task))
//        }
//        return fullTasks
//    }
//
//    private suspend fun toFullTask(task: Specification): FullTask {
//        val batches = batchService.getFullByTaskId(task.id)
//        return FullTask(task.id, task.taskName, task.taskNumber, batches)
//    }

    suspend fun deleteSpecificationById(id: Long) {
        specificationDao.deleteById(id)
        logger.info("Данные о ТЗ удалены")
    }

    suspend fun createSpecification(specification: Specification): Specification {
        val createdSpecification = specificationDao.create(specification)
        logger.info("Создано ТЗ - ${createdSpecification.specificationTitle}")
        return createdSpecification
    }

    suspend fun updateSpecification(specification: Specification) {
        val oldSpecification = getSpecificationById(specification.id)
        specificationDao.update(specification)
        logger.info("Обновили ТЗ")
        if (oldSpecification.specificationTitle != specification.specificationTitle) {
            updateKitNumber(specification)
        }
    }

    private suspend fun updateKitNumber(specification: Specification) {
        val kits = kitService.getKitsBySpecificationId(specification.id)
        for (kit in kits) {
            val numberTail = kit.kitNumber.substringAfterLast(" / ")
            kit.kitNumber = "${specification.specificationTitle} / $numberTail"
            kitService.updateKit(kit)
        }
    }
}