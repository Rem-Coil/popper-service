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

    fun getByTask(taskId: Int): List<Bobbin> {
        val bobbins = dao.getByTaskId(taskId)
        logger.info("Отдали катушки по ТЗ - $taskId")
        return bobbins
    }

}