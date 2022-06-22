package com.remcoil.service.bobbin

import com.remcoil.dao.bobbin.BobbinDao
import com.remcoil.data.model.bobbin.Bobbin
import com.remcoil.utils.logger

class BobbinService(private val dao: BobbinDao) {

    fun getAll(): List<Bobbin> {
        val bobbins = dao.getAll()
        logger.info("Отдали все катушки")
        return bobbins
    }

    fun getById(id: Int): Bobbin? {
        val bobbin = dao.getById(id)
        logger.info("Отдали катушку - $id")
        return bobbin
    }

    fun getByBatchId(batchId: Long): List<Bobbin> {
        val bobbins = dao.getByBatchId(batchId)
        logger.info("Отдали катушки партии - $batchId")
        return bobbins
    }

    suspend fun createBobbin(bobbin: Bobbin): Bobbin {
        val createdBobbin = dao.createBobbin(bobbin)
        logger.info("Добавили катушку с id = ")
        return createdBobbin
    }
}