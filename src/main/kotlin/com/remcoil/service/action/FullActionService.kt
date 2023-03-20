package com.remcoil.service.action

import com.remcoil.dao.action.FullActionDao
import com.remcoil.data.model.action.full.*
import com.remcoil.utils.logger

class FullActionService(
    private val fullActionDao: FullActionDao
) {
    suspend fun getAllFull(): List<FlatFullAction> {
        val actions = fullActionDao.getAll()
        logger.info("Отдали все операции")
        return actions
    }
//
//    suspend fun getFullByTaskId(taskId: Int): TaskFullAction? {
//        val actions = fullActionDao.getBySpecificationId(taskId)
//        logger.info("Отдали все операции по ТЗ c id = $taskId")
//        return if (actions.isEmpty()) null else TaskFullAction.toTaskFullAction(actions)
//    }
//
//    suspend fun getFullByBatchId(batchId: Long): BatchFullAction? {
//        val actions = fullActionDao.getByBatchId(batchId)
//        logger.info("Отдали все операции по партии с id = $batchId")
//        return if (actions.isEmpty()) null else BatchFullAction.toBatchFullAction(actions)
//    }
//
//    suspend fun getFullByBobbinId(bobbinId: Long): BobbinFullAction? {
//        val actions = fullActionDao.getByProductId(bobbinId)
//        logger.info("Отдали все операции по катушке с id = $bobbinId")
//        return if (actions.isEmpty()) null else BobbinFullAction.toBobbinFullAction(actions)
//    }
//
//    suspend fun getFullById(id: Long): FlatFullAction? {
//        val action = fullActionDao.getById(id)
//        logger.info("Отдали операцию с id - $id")
//        return action
//    }
}