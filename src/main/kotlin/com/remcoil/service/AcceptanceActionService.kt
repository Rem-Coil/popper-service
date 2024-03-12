package com.remcoil.service

import com.remcoil.dao.AcceptanceActionDao
import com.remcoil.model.dto.AcceptanceAction
import com.remcoil.model.dto.ExtendedAcceptanceAction
import com.remcoil.utils.exceptions.EntryDoesNotExistException

class AcceptanceActionService(
    private val acceptanceActionDao: AcceptanceActionDao
) {
    suspend fun getAll(): List<AcceptanceAction> {
        return acceptanceActionDao.getAll()
    }

    suspend fun getByProductId(productId: Long): AcceptanceAction {
        return acceptanceActionDao.getByProductId(productId)
            ?: throw EntryDoesNotExistException("There is no acceptance action for product with id = $productId")
    }

    suspend fun getByKitId(kitId: Long): List<ExtendedAcceptanceAction> {
        return acceptanceActionDao.getByKitId(kitId)
    }

    suspend fun getBySpecificationId(specificationId: Long): List<ExtendedAcceptanceAction> {
        return acceptanceActionDao.getBySpecificationId(specificationId)
    }

    suspend fun create(acceptanceAction: AcceptanceAction): AcceptanceAction {
        return acceptanceActionDao.create(acceptanceAction)
    }

    suspend fun batchCreate(acceptanceActions: List<AcceptanceAction>): List<AcceptanceAction> {
        return acceptanceActionDao.batchCreate(acceptanceActions)
    }

    suspend fun update(acceptanceAction: AcceptanceAction): Int {
        return acceptanceActionDao.update(acceptanceAction)
    }

    suspend fun deleteById(id: Long): Int {
        return acceptanceActionDao.deleteById(id)
    }
}