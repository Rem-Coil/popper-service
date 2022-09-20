package com.remcoil.service.bobbin

import com.remcoil.dao.bobbin.BobbinDao
import com.remcoil.data.model.batch.Batch
import com.remcoil.data.model.bobbin.Bobbin
import com.remcoil.utils.logger

class BobbinService(private val bobbinDao: BobbinDao) {

    suspend fun getAll(): List<Bobbin> {
        val bobbins = bobbinDao.getAll()
        logger.info("Отдали все катушки")
        return bobbins
    }

    suspend fun getById(id: Long): Bobbin? {
        val bobbin = bobbinDao.getById(id)
        logger.info("Отдали катушку - $id")
        return bobbin
    }

    suspend fun getByBatchId(batchId: Long): List<Bobbin> {
        val bobbins = bobbinDao.getByBatchId(batchId)
        logger.info("Отдали катушки партии - $batchId")
        return bobbins
    }

    suspend fun getByBatches(batches : List<Batch>): List<Bobbin> {
        val bobbins = bobbinDao.getByBatchesId(batches.map { batch -> batch.id })
        logger.info("Отдали катушки для партий $batches")
        return bobbins
    }

    suspend fun createBobbin(bobbin: Bobbin): Bobbin {
        val createdBobbin = bobbinDao.createBobbin(bobbin)
        logger.info("Добавили катушку с id = ${createdBobbin.id}")
        return createdBobbin
    }

    suspend fun trueDeleteById(bobbinId: Long) {
        bobbinDao.deleteById(bobbinId)
        logger.info("Удалили катушку с id = $bobbinId")
    }

    suspend fun deactivateById(bobbinId: Long) {
        val bobbin = getById(bobbinId)
        if (bobbin != null) {
            bobbinDao.deactivateById(bobbinId)
            logger.info("Забраковали катушка с id = $bobbinId")
            bobbin.active = true
            createBobbin(bobbin)
        }
    }

    suspend fun isActive(bobbinId: Long) : Boolean {
        val bobbin = getById(bobbinId)
        return bobbin != null && bobbin.active
    }

    suspend fun updateBobbin(bobbin: Bobbin) {
        bobbinDao.updateBobbin(bobbin)
        logger.info("Обновили катушку с id = ${bobbin.id}")
    }
}