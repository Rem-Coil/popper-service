package com.remcoil.service.bobbin

import com.remcoil.dao.bobbin.BobbinDao
import com.remcoil.data.model.bobbin.Bobbin

class BobbinService(private val dao: BobbinDao) {

    fun getAll(): List<Bobbin> {
        return dao.getAll()
    }

    fun getById(id: Int): Bobbin? {
        return dao.getById(id)
    }

    fun getByTask(taskId: Int): List<Bobbin> {
        return dao.getByTaskId(taskId)
    }

}