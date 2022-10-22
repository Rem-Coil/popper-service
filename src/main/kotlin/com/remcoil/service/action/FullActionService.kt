package com.remcoil.service.action

import com.remcoil.dao.action.FullActionDao
import com.remcoil.data.model.action.full.FullAction
import com.remcoil.utils.logger

class FullActionService(
    private val fullActionDao: FullActionDao
) {
    suspend fun getAllFull(): List<FullAction> {
        val actions = fullActionDao.getAll()
        logger.info("Отдали все операции")
        return actions
    }

    suspend fun getFullByBatchId(batchId: Long): List<FullAction> {
        val actions = fullActionDao.getByBatchId(batchId)
        logger.info("Отдали все операции по ТЗ - $batchId")
        return actions
    }

    suspend fun getFullByTaskId(taskId: Int): List<FullAction> {
        val actions = fullActionDao.getByTaskId(taskId)
        logger.info("Отдали все операции по ТЗ - $taskId")
        return actions
    }

    suspend fun getFullByBobbinId(bobbinId: Long): List<FullAction> {
        val actions = fullActionDao.getByBobbinId(bobbinId)
        logger.info("Отдали все операции по катушке с id = $bobbinId")
        return actions
    }

    suspend fun getFullById(id: Long): FullAction? {
        val actions = fullActionDao.getById(id)
        logger.info("Отдали операцию с id - $id")
        return actions
    }
}