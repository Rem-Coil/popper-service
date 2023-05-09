package com.remcoil.service

import com.remcoil.dao.v2.BatchDao
import com.remcoil.data.model.v2.*
import com.remcoil.service.v2.ActionService
import com.remcoil.service.v2.BatchService
import com.remcoil.service.v2.ControlActionService
import com.remcoil.service.v2.ProductService
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

    private var batchList = listOf<Batch>()
    private var productList = mutableListOf<Product>()
    private var extendedActionList = mutableListOf<ExtendedAction>()
    private var extendedControlActionList = mutableListOf<ExtendedControlAction>()

    private val batchService = BatchService(batchDao, productService, actionService, controlActionService)

    @BeforeEach
    fun resetCollections() {
        batchList = mutableListOf(
            Batch(1, 1, 1),
            Batch(2, 2, 1),
            Batch(3, 3, 1)
        )
        productList = mutableListOf(
            Product(1, 1, true, 1),
            Product(2, 2, true, 1),
            Product(3, 3, true, 1),
            Product(4, 4, true, 1),
            Product(5, 1, true, 2),
            Product(6, 2, true, 2),
            Product(7, 3, true, 2),
            Product(8, 4, true, 2),
            Product(9, 1, true, 3),
            Product(10, 2, true, 3),
            Product(11, 3, true, 3),
            Product(12, 4, true, 3)
        )
        extendedActionList = mutableListOf(
            ExtendedAction(
                1,
                now.plus(1, DateTimeUnit.MINUTE).toLocalDateTime(TimeZone.UTC),
                false,
                1,
                1,
                1,
                true,
                1,
                1,
                1
            ),
            ExtendedAction(
                2,
                now.plus(2, DateTimeUnit.MINUTE).toLocalDateTime(TimeZone.UTC),
                false,
                1,
                1,
                2,
                true,
                1,
                1,
                1
            ),
            ExtendedAction(
                3,
                now.plus(3, DateTimeUnit.MINUTE).toLocalDateTime(TimeZone.UTC),
                false,
                1,
                1,
                3,
                true,
                1,
                1,
                1
            ),
            ExtendedAction(
                4,
                now.plus(4, DateTimeUnit.MINUTE).toLocalDateTime(TimeZone.UTC),
                false,
                1,
                1,
                4,
                true,
                1,
                1,
                1
            ),
            ExtendedAction(
                5,
                now.plus(5, DateTimeUnit.MINUTE).toLocalDateTime(TimeZone.UTC),
                false,
                1,
                1,
                5,
                true,
                2,
                1,
                1
            ),
            ExtendedAction(
                6,
                now.plus(6, DateTimeUnit.MINUTE).toLocalDateTime(TimeZone.UTC),
                false,
                1,
                1,
                6,
                true,
                2,
                1,
                1
            ),
            ExtendedAction(
                7,
                now.plus(7, DateTimeUnit.MINUTE).toLocalDateTime(TimeZone.UTC),
                false,
                1,
                1,
                7,
                true,
                2,
                1,
                1
            ),
            ExtendedAction(
                8,
                now.plus(8, DateTimeUnit.MINUTE).toLocalDateTime(TimeZone.UTC),
                false,
                1,
                1,
                8,
                true,
                2,
                1,
                1
            ),
            ExtendedAction(
                9,
                now.plus(9, DateTimeUnit.MINUTE).toLocalDateTime(TimeZone.UTC),
                false,
                1,
                1,
                9,
                true,
                3,
                1,
                1
            ),
            ExtendedAction(
                10,
                now.plus(10, DateTimeUnit.MINUTE).toLocalDateTime(TimeZone.UTC),
                false,
                1,
                1,
                10,
                true,
                3,
                1,
                1
            ),
            ExtendedAction(
                11,
                now.plus(11, DateTimeUnit.MINUTE).toLocalDateTime(TimeZone.UTC),
                false,
                1,
                1,
                11,
                true,
                3,
                1,
                1
            ),

            ExtendedAction(
                12,
                now.plus(12, DateTimeUnit.MINUTE).toLocalDateTime(TimeZone.UTC),
                false,
                2,
                1,
                1,
                true,
                1,
                1,
                1
            ),
        )
        extendedControlActionList = mutableListOf(
            ExtendedControlAction(
                1,
                now.plus(5, DateTimeUnit.MINUTE).toLocalDateTime(TimeZone.UTC),
                true,
                "OTK",
                "Good",
                1,
                1,
                1,
                true,
                1,
                1,
                1
            ),
            ExtendedControlAction(
                2,
                now.plus(6, DateTimeUnit.MINUTE).toLocalDateTime(TimeZone.UTC),
                true,
                "OTK",
                "Good",
                1,
                1,
                2,
                true,
                1,
                1,
                1
            ),
            ExtendedControlAction(
                3,
                now.plus(7, DateTimeUnit.MINUTE).toLocalDateTime(TimeZone.UTC),
                true,
                "OTK",
                "Good",
                1,
                1,
                3,
                true,
                1,
                1,1

            ),
            ExtendedControlAction(
                4,
                now.plus(8, DateTimeUnit.MINUTE).toLocalDateTime(TimeZone.UTC),
                true,
                "OTK",
                "Good",
                1,
                1,
                4,
                true,
                1,
                1,1
            ),
            ExtendedControlAction(
                5,
                now.plus(8, DateTimeUnit.MINUTE).toLocalDateTime(TimeZone.UTC),
                true,
                "OTK",
                "Good",
                1,
                1,
                5,
                true,
                2,
                1,1
            ),
            ExtendedControlAction(
                6,
                now.plus(8, DateTimeUnit.MINUTE).toLocalDateTime(TimeZone.UTC),
                true,
                "Test",
                "Good",
                0,
                1,
                1,
                true,
                2,
                1,1
            )
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
            BatchProgress(1, 1, mapOf(1L to 4, 2L to 1), mapOf("OTK" to 4), 0, 0),
            BatchProgress(2, 2, mapOf(1L to 4), mapOf("OTK" to 1, "Test" to 1), 0, 0),
            BatchProgress(3, 3, mapOf(1L to 3), mapOf(), 0, 0)
        )

        assertEquals(targetProgress, producedProgress)
    }

    @Test
    fun `with defected product`() = runBlocking {
        productList[0] = Product(1, 1, false, 1)
        extendedActionList[0] = ExtendedAction(1, now.plus(1, DateTimeUnit.MINUTE).toLocalDateTime(TimeZone.UTC), false, 1, 1, 1, false, 1, 1,1)
        extendedActionList[11] = ExtendedAction(12, now.plus(12, DateTimeUnit.MINUTE).toLocalDateTime(TimeZone.UTC), false, 2, 1, 1, false, 1, 1,1)
        extendedControlActionList[0] = ExtendedControlAction(1, now.plus(5, DateTimeUnit.MINUTE).toLocalDateTime(TimeZone.UTC), true, "OTK", "Good", 1, 1, 1, false, 1, 1,1)

        val producedProgress = batchService.getBatchesProgressByKitId(1)
        val targetProgress = listOf(
            BatchProgress(1, 1, mapOf(1L to 3), mapOf("OTK" to 3), 0, 1),
            BatchProgress(2, 2, mapOf(1L to 4), mapOf("OTK" to 1, "Test" to 1), 0, 0),
            BatchProgress(3, 3, mapOf(1L to 3), mapOf(), 0, 0)
        )

        assertEquals(targetProgress, producedProgress)
    }

    @Test
    fun `unsuccessful control with no repair`() = runBlocking {
        extendedControlActionList[0] = ExtendedControlAction(1, now.plus(5, DateTimeUnit.MINUTE).toLocalDateTime(TimeZone.UTC), false, "OTK", "No Good", 1, 1, 1, true, 1, 1,1)
        extendedControlActionList.add(ExtendedControlAction(1000, now.plus(5000, DateTimeUnit.MINUTE).toLocalDateTime(TimeZone.UTC), false, "Test", "No Good", 2, 1, 1, true, 1, 1,1))

        val targetProgress = listOf(
            BatchProgress(1, 1, mapOf(1L to 3, 2L to 0), mapOf("OTK" to 3), 1, 0),
            BatchProgress(2, 2, mapOf(1L to 4), mapOf("OTK" to 1, "Test" to 1), 0, 0),
            BatchProgress(3, 3, mapOf(1L to 3), mapOf(), 0, 0)
        )
        val producedProgress = batchService.getBatchesProgressByKitId(1)

        assertEquals(targetProgress, producedProgress)
    }

    @Test
    fun `with correct repair`() = runBlocking {
        extendedControlActionList[0] = ExtendedControlAction(1, now.plus(5, DateTimeUnit.MINUTE).toLocalDateTime(TimeZone.UTC), false, "OTK", "No Good", 1, 1, 1, true, 1, 1,1)
        extendedActionList.add(ExtendedAction(1, now.plus(6, DateTimeUnit.MINUTE).toLocalDateTime(TimeZone.UTC), true, 1, 1, 1, true, 1, 1,1))

        val producedProgress = batchService.getBatchesProgressByKitId(1)
        val targetProgress = listOf(
            BatchProgress(1, 1, mapOf(1L to 4, 2L to 1), mapOf("OTK" to 3), 0, 0),
            BatchProgress(2, 2, mapOf(1L to 4), mapOf("OTK" to 1, "Test" to 1), 0, 0),
            BatchProgress(3, 3, mapOf(1L to 3), mapOf(), 0, 0)
        )

        assertEquals(targetProgress, producedProgress)
    }

    @Test
    fun `with repair before control`() = runBlocking {
        extendedControlActionList[0] = ExtendedControlAction(1, now.plus(5, DateTimeUnit.MINUTE).toLocalDateTime(TimeZone.UTC), false, "OTK", "No Good", 1, 1, 1, true, 1, 1,1)
        extendedActionList.add(ExtendedAction(1, now.plus(4, DateTimeUnit.MINUTE).toLocalDateTime(TimeZone.UTC), true, 1, 1, 1, true, 1, 1,1))

        val producedProgress = batchService.getBatchesProgressByKitId(1)
        val targetProgress = listOf(
            BatchProgress(1, 1, mapOf(1L to 3, 2L to 1), mapOf("OTK" to 3), 1, 0),
            BatchProgress(2, 2, mapOf(1L to 4), mapOf("OTK" to 1, "Test" to 1), 0, 0),
            BatchProgress(3, 3, mapOf(1L to 3), mapOf(), 0, 0)
        )

        assertEquals(targetProgress, producedProgress)
    }
}