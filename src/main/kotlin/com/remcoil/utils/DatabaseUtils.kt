package com.remcoil.utils

import com.remcoil.utils.exceptions.DatabaseException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.RuntimeException

suspend fun <T> safetySuspendTransaction(
    db: Database,
    statement: Transaction.() -> T
): T = withContext(Dispatchers.IO) {
    try {
        return@withContext transaction(db, statement)
//    } catch (e: ExposedSQLException) {
//        throw DatabaseException(e.message.toString())
    } catch (e: RuntimeException) {
        throw DatabaseException(e.message.toString())
    }
}