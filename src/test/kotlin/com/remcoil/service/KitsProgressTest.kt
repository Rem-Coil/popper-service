package com.remcoil.service

import com.remcoil.dao.v2.KitDao
import com.remcoil.data.model.v2.*
import com.remcoil.service.v2.*
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.*
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test
import kotlin.test.assertEquals

class KitsProgressTest {
    private val kitDao: KitDao = mockk()
    private val batchService: BatchService = mockk()
    private val operationTypeService: OperationTypeService = mockk()
    private val productService: ProductService = mockk()
    private val actionService: ActionService = mockk()
    private val controlActionService: ControlActionService = mockk()

    private val now = Clock.System.now()

    private var kitList = mutableListOf<Kit>()
    private var operationTypeList = mutableListOf<OperationType>()
    private var extendedActionList = mutableListOf<ExtendedAction>()
    private var extendedControlActionList = mutableListOf<ExtendedControlAction>()
    private var extendedProductList = mutableListOf<ExtendedProduct>()

    private val kitService =
        KitService(kitDao, batchService, operationTypeService, productService, actionService, controlActionService)

    @BeforeEach
    fun resetCollections() {
        for (i in 1..3L) {
            kitList.add(Kit(i, "1-1", 3, 100, 1))
            operationTypeList.add(OperationType(i, "Test", i.toInt(), 1))
            for (j in 1..300) {
                extendedProductList.add(ExtendedProduct((j % 301).toLong(), j, true, ((j + 99) / 100).toLong(), i))
            }
        }

        for (i in 1..500L) {
            extendedActionList.add(
                ExtendedAction(
                    i,
                    now.plus(i, DateTimeUnit.MINUTE).toLocalDateTime(TimeZone.UTC),
                    false,
                    1,
                    1,
                    i,
                    true,
                    (i + 99) / 100,
                    (i + 299) / 300,
                    1
                )
            )
        }
        for (i in 1..350L) {
            extendedActionList.add(
                ExtendedAction(
                    500 + i,
                    now.plus(i, DateTimeUnit.MINUTE).toLocalDateTime(TimeZone.UTC),
                    false,
                    3,
                    1,
                    i,
                    true,
                    (i + 99) / 100,
                    (i + 299) / 300,
                    1
                )
            )
        }

        for (i in 1..350L) {
            extendedControlActionList.add(
                ExtendedControlAction(
                    i,
                    now.plus(i + 1, DateTimeUnit.MINUTE).toLocalDateTime(TimeZone.UTC),
                    true,
                    "Test",
                    "Good",
                    1,
                    1,
                    i,
                    true,
                    (i + 99) / 100,
                    (i + 299) / 300,
                    1
                )
            )
        }
        for (i in 1..150L) {
            extendedControlActionList.add(
                ExtendedControlAction(
                    350 + i,
                    now.plus(i + 1, DateTimeUnit.MINUTE).toLocalDateTime(TimeZone.UTC),
                    true,
                    "OTK",
                    "Good",
                    1,
                    1,
                    i,
                    true,
                    (i + 99) / 100,
                    (i + 299) / 300,
                    1
                )
            )
        }

        coEvery { kitDao.getBySpecificationId(any()) } returns kitList
        coEvery { operationTypeService.getOperationTypesBySpecificationId(any()) } returns operationTypeList
        coEvery { actionService.getActionsBySpecificationId(any()) } returns extendedActionList
        coEvery { controlActionService.getControlActionsBySpecificationId(any()) } returns extendedControlActionList
        coEvery { productService.getProductsBySpecificationId(any()) } returns extendedProductList
    }

    @Test
    fun `base case`(): Unit = runBlocking {
        val producedProgress = kitService.getKitProgressBySpecificationId(1)
        val targetProgress = listOf(
            KitShortProgress(1, "1-1", 300, 300, 300, mapOf("Test" to 300, "OTK" to 150), 0, 0),
            KitShortProgress(2, "1-1", 300, 200, 50, mapOf("Test" to 50), 0, 0),
            KitShortProgress(3, "1-1", 300, 0, 0, mapOf(), 0, 0)
        )

        assertEquals(targetProgress, producedProgress)
    }

    @Test
    fun `with defected product`(): Unit = runBlocking {
        extendedProductList[0] = ExtendedProduct(1, 1, false, 1, 1)
        extendedActionList[0] = ExtendedAction(1, now.plus(1, DateTimeUnit.MINUTE).toLocalDateTime(TimeZone.UTC), false, 1, 1, 1, false, 1, 1, 1)
        extendedActionList[500] = ExtendedAction(501, now.plus(501, DateTimeUnit.MINUTE).toLocalDateTime(TimeZone.UTC), false, 3, 1, 1, false, 1, 1, 1)
        extendedControlActionList[0] = ExtendedControlAction(1, now.plus(1 + 1, DateTimeUnit.MINUTE).toLocalDateTime(TimeZone.UTC), true, "Test", "Good", 1, 1, 1, false, 1, 1, 1)
        extendedControlActionList[350] = ExtendedControlAction(351, now.plus(351 + 1, DateTimeUnit.MINUTE).toLocalDateTime(TimeZone.UTC), true, "OTK", "Good", 3, 1, 1, false, 1, 1, 1)

        val producedProgress = kitService.getKitProgressBySpecificationId(1)
        val targetProgress = listOf(
            KitShortProgress(1, "1-1", 300, 299, 299, mapOf("Test" to 299, "OTK" to 149), 0, 1),
            KitShortProgress(2, "1-1", 300, 200, 50, mapOf("Test" to 50), 0, 0),
            KitShortProgress(3, "1-1", 300, 0, 0, mapOf(), 0, 0)
        )

        assertEquals(targetProgress, producedProgress)
    }

    @Test
    fun `unsuccessful control with no repair`(): Unit = runBlocking {
        extendedControlActionList[0] = ExtendedControlAction(1, now.plus(1 + 1, DateTimeUnit.MINUTE).toLocalDateTime(TimeZone.UTC), false, "Test", "No Good", 1, 1, 1, true, 1, 1, 1)
        extendedControlActionList[350] = ExtendedControlAction(351, now.plus(351 + 1, DateTimeUnit.MINUTE).toLocalDateTime(TimeZone.UTC), false, "OTK", "No Good", 3, 1, 1, true, 1, 1, 1)

        val producedProgress = kitService.getKitProgressBySpecificationId(1)
        val targetProgress = listOf(
            KitShortProgress(1, "1-1", 300, 300, 299, mapOf("Test" to 299, "OTK" to 149), 1, 0),
            KitShortProgress(2, "1-1", 300, 200, 50, mapOf("Test" to 50), 0, 0),
            KitShortProgress(3, "1-1", 300, 0, 0, mapOf(), 0, 0)
        )

        assertEquals(targetProgress, producedProgress)
    }

    @Test
    fun `with correct repair`(): Unit = runBlocking {
        extendedControlActionList[0] = ExtendedControlAction(1, now.plus(1 + 1, DateTimeUnit.MINUTE).toLocalDateTime(TimeZone.UTC), false, "Test", "No Good", 3, 1, 1, true, 1, 1, 1)
        extendedActionList.add(ExtendedAction(1000, now.plus(1000, DateTimeUnit.MINUTE).toLocalDateTime(TimeZone.UTC), true, 3, 1, 1, true, 1, 1, 1))

        val producedProgress = kitService.getKitProgressBySpecificationId(1)
        val targetProgress = listOf(
            KitShortProgress(1, "1-1", 300, 300, 300, mapOf("Test" to 299, "OTK" to 150), 0, 0),
            KitShortProgress(2, "1-1", 300, 200, 50, mapOf("Test" to 50), 0, 0),
            KitShortProgress(3, "1-1", 300, 0, 0, mapOf(), 0, 0)
        )

        assertEquals(targetProgress, producedProgress)
    }

    @Test
    fun `with repair before control`(): Unit = runBlocking {
        extendedControlActionList[0] = ExtendedControlAction(1, now.plus(1 + 1, DateTimeUnit.MINUTE).toLocalDateTime(TimeZone.UTC), false, "Test", "No Good", 3, 1, 1, true, 1, 1, 1)
        extendedActionList.add(ExtendedAction(1000, now.plus(1000, DateTimeUnit.MINUTE).toLocalDateTime(TimeZone.UTC), true, 3, 1, 1, true, 1, 1, 1))
        extendedControlActionList[0] = ExtendedControlAction(1000, now.plus(1000 + 1, DateTimeUnit.MINUTE).toLocalDateTime(TimeZone.UTC), false, "Test", "Still No Good", 3, 1, 1, true, 1, 1, 1)

        val producedProgress = kitService.getKitProgressBySpecificationId(1)
        val targetProgress = listOf(
            KitShortProgress(1, "1-1", 300, 300, 299, mapOf("Test" to 299, "OTK" to 150), 1, 0),
            KitShortProgress(2, "1-1", 300, 200, 50, mapOf("Test" to 50), 0, 0),
            KitShortProgress(3, "1-1", 300, 0, 0, mapOf(), 0, 0)
        )

        assertEquals(targetProgress, producedProgress)
    }
}
