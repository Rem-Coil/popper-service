package com.remcoil.service

import com.remcoil.dao.BatchDao
import com.remcoil.model.dto.*
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.*
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test
import kotlin.test.assertEquals

class BatchesProgressTest {
    private val batchDao: BatchDao = mockk()
    private val productService: ProductService = mockk()
    private val actionService: ActionService = mockk()
    private val controlActionService: ControlActionService = mockk()

    private val now = Clock.System.now()

    private var batchList = mutableListOf<Batch>()
    private var productList = mutableListOf<Product>()
    private var extendedActionList = mutableListOf<ExtendedAction>()
    private var extendedControlActionList = mutableListOf<ExtendedControlAction>()

    private val batchService = BatchService(batchDao, productService, actionService, controlActionService)

    @BeforeEach
    fun resetCollections() {
        for (i in 1..3L) {
            batchList.add(Batch(i, i.toInt(), 1))
            for (j in 1..4) {
                productList.add(Product((i - 1) * 4 + j, j, true, i))
            }
        }

        for (i in 1..11L) {
            extendedActionList.add(
                ExtendedAction(
                    i,
                    now.plus(i, DateTimeUnit.MINUTE).toLocalDateTime(TimeZone.UTC),
                    false,
                    1,
                    1,
                    i,
                    true,
                    (i + 3) / 4,
                    1,
                    1
                )
            )
        }

        extendedActionList.add(
            ExtendedAction(12, now.plus(12, DateTimeUnit.MINUTE).toLocalDateTime(TimeZone.UTC), false, 2, 1, 1, true, 1, 1, 1)
        )

        for (i in 1..5L) {
            extendedControlActionList.add(
                ExtendedControlAction(
                    i,
                    now.plus(i + 4, DateTimeUnit.MINUTE).toLocalDateTime(TimeZone.UTC),
                    true,
                    ControlType.OTK,
                    "Good",
                    1,
                    1,
                    i,
                    true,
                    (i + 3) / 4,
                    1,
                    1
                )
            )
        }

        extendedControlActionList.add(
            ExtendedControlAction(6, now.plus(8, DateTimeUnit.MINUTE).toLocalDateTime(TimeZone.UTC), true, ControlType.TESTING, "Good", 0, 1, 1, true, 2, 1, 1)
        )

        coEvery { batchDao.getByKitId(any()) } returns batchList
        coEvery { productService.getProductsByBatchesId(any()) } returns productList
        coEvery { actionService.getActionsByKitId(any()) } returns extendedActionList
        coEvery { controlActionService.getControlActionsByKitId(any()) } returns extendedControlActionList
    }

    @Test
    fun `base case`(): Unit = runBlocking {
        val producedProgress = batchService.getBatchesProgressByKitId(1)
        val targetProgress = listOf(
            BatchProgress(1, 1, mapOf(1L to 4, 2L to 1), mapOf(ControlType.OTK to 4, ControlType.TESTING to 0), 0, 0),
            BatchProgress(2, 2, mapOf(1L to 4), mapOf(ControlType.OTK to 1, ControlType.TESTING to 1), 0, 0),
            BatchProgress(3, 3, mapOf(1L to 3), mapOf(ControlType.OTK to 0, ControlType.TESTING to 0), 0, 0)
        )

        assertEquals(targetProgress, producedProgress)
    }

    @Test
    fun `with defected product`() = runBlocking {
        productList[0] = Product(1, 1, false, 1)
        extendedActionList[0] = ExtendedAction(1, now.plus(1, DateTimeUnit.MINUTE).toLocalDateTime(TimeZone.UTC), false, 1, 1, 1, false, 1, 1, 1)
        extendedActionList[11] = ExtendedAction(12, now.plus(12, DateTimeUnit.MINUTE).toLocalDateTime(TimeZone.UTC), false, 2, 1, 1, false, 1, 1, 1)
        extendedControlActionList[0] = ExtendedControlAction(1, now.plus(5, DateTimeUnit.MINUTE).toLocalDateTime(TimeZone.UTC), true, ControlType.OTK, "Good", 1, 1, 1, false, 1, 1, 1)

        val producedProgress = batchService.getBatchesProgressByKitId(1)
        val targetProgress = listOf(
            BatchProgress(1, 1, mapOf(1L to 3), mapOf(ControlType.OTK to 3, ControlType.TESTING to 0), 0, 1),
            BatchProgress(2, 2, mapOf(1L to 4), mapOf(ControlType.OTK to 1, ControlType.TESTING to 1), 0, 0),
            BatchProgress(3, 3, mapOf(1L to 3), mapOf(ControlType.OTK to 0, ControlType.TESTING to 0), 0, 0)
        )

        assertEquals(targetProgress, producedProgress)
    }

    @Test
    fun `unsuccessful control with no repair`() = runBlocking {
        extendedControlActionList[0] = ExtendedControlAction(1, now.plus(5, DateTimeUnit.MINUTE).toLocalDateTime(TimeZone.UTC), false, ControlType.OTK, "No Good", 1, 1, 1, true, 1, 1, 1)
        extendedControlActionList.add(
            ExtendedControlAction(1000, now.plus(5000, DateTimeUnit.MINUTE).toLocalDateTime(TimeZone.UTC), false, ControlType.TESTING, "No Good", 2, 1, 1, true, 1, 1, 1)
        )

        val targetProgress = listOf(
            BatchProgress(1, 1, mapOf(1L to 3, 2L to 0), mapOf(ControlType.OTK to 3, ControlType.TESTING to 0), 1, 0),
            BatchProgress(2, 2, mapOf(1L to 4), mapOf(ControlType.OTK to 1, ControlType.TESTING to 1), 0, 0),
            BatchProgress(3, 3, mapOf(1L to 3), mapOf(ControlType.OTK to 0, ControlType.TESTING to 0), 0, 0)
        )
        val producedProgress = batchService.getBatchesProgressByKitId(1)

        assertEquals(targetProgress, producedProgress)
    }

    @Test
    fun `with correct repair`() = runBlocking {
        extendedControlActionList[0] = ExtendedControlAction(1, now.plus(5, DateTimeUnit.MINUTE).toLocalDateTime(TimeZone.UTC), false, ControlType.OTK, "No Good", 1, 1, 1, true, 1, 1, 1)
        extendedActionList.add(
            ExtendedAction(1, now.plus(6, DateTimeUnit.MINUTE).toLocalDateTime(TimeZone.UTC), true, 1, 1, 1, true, 1, 1, 1)
        )

        val producedProgress = batchService.getBatchesProgressByKitId(1)
        val targetProgress = listOf(
            BatchProgress(1, 1, mapOf(1L to 4, 2L to 1), mapOf(ControlType.OTK to 3, ControlType.TESTING to 0), 0, 0),
            BatchProgress(2, 2, mapOf(1L to 4), mapOf(ControlType.OTK to 1, ControlType.TESTING to 1), 0, 0),
            BatchProgress(3, 3, mapOf(1L to 3), mapOf(ControlType.OTK to 0, ControlType.TESTING to 0), 0, 0)
        )

        assertEquals(targetProgress, producedProgress)
    }

    @Test
    fun `with repair before control`() = runBlocking {
        extendedControlActionList[0] = ExtendedControlAction(1, now.plus(5, DateTimeUnit.MINUTE).toLocalDateTime(TimeZone.UTC), false, ControlType.OTK, "No Good", 1, 1, 1, true, 1, 1, 1)
        extendedActionList.add(
            ExtendedAction(1, now.plus(4, DateTimeUnit.MINUTE).toLocalDateTime(TimeZone.UTC), true, 1, 1, 1, true, 1, 1, 1)
        )

        val producedProgress = batchService.getBatchesProgressByKitId(1)
        val targetProgress = listOf(
            BatchProgress(1, 1, mapOf(1L to 3, 2L to 1), mapOf(ControlType.OTK to 3, ControlType.TESTING to 0), 1, 0),
            BatchProgress(2, 2, mapOf(1L to 4), mapOf(ControlType.OTK to 1, ControlType.TESTING to 1), 0, 0),
            BatchProgress(3, 3, mapOf(1L to 3), mapOf(ControlType.OTK to 0, ControlType.TESTING to 0), 0, 0)
        )

        assertEquals(targetProgress, producedProgress)
    }
}