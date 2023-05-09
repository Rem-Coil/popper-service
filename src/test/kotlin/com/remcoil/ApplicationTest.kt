package com.remcoil

import com.remcoil.data.model.v2.ExtendedAction
import com.remcoil.data.model.v2.ExtendedControlAction
import com.remcoil.data.model.v2.OperationType
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.system.measureTimeMillis
import kotlin.test.Test

class ApplicationTest {
    private val now = Clock.System.now().toLocalDateTime(TimeZone.UTC)

    @Test
    fun applicationTest() {
        val actionsByKit: Map<Long, MutableList<ExtendedAction>> = mutableMapOf(1L to mutableListOf())
        val controlActionsByKit: Map<Long, MutableList<ExtendedControlAction>> = mutableMapOf(1L to mutableListOf())
        val operationTypes = mutableListOf(OperationType(1, "first", 1, 1))

        for (i in 1..1_000_000L) {
            actionsByKit[1]!!.add(ExtendedAction(i, now, false, 1, 1, i%1000, true, 1, 1, 1))
        }
        for (i in 1..1_000_000L) {
            controlActionsByKit[1]!!.add(ExtendedControlAction(i, now, true, "test", "Good",  1, 1, i%1000, true, 1, 1, 1))
        }

        val time = measureTimeMillis {
            actionsByKit[1]?.filter { it.operationType == operationTypes[0].id }
                ?.groupBy { it.productId }?.keys?.size ?: 0
        }
        println(time)
    }

    @Test
    fun foo() {
        println((300) % 301)
    }
}