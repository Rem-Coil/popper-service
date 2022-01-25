package com.remcoil.utils

import com.remcoil.utils.exceptions.DatabaseException
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync

suspend fun <T> safetySuspendTransactionAsync(
    db: Database,
    statement: suspend Transaction.() -> T
): T {
    try {
        return suspendedTransactionAsync(
            Dispatchers.IO,
            db,
            TransactionManager.manager.defaultIsolationLevel,
            statement
        ).await()
    } catch (e: ExposedSQLException) {
        throw DatabaseException(e.message.toString())
    }
}