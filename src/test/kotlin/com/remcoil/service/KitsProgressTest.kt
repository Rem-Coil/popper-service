package com.remcoil.service

import com.remcoil.dao.KitDao
import com.remcoil.model.dto.*
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

    private var kitList = mutableListOf<ExtendedKit>()
    private var operationTypeList = mutableListOf<OperationType>()
    private var extendedActionList = mutableListOf<ExtendedAction>()
    private var extendedControlActionList = mutableListOf<ExtendedControlAction>()
    private var extendedProductList = mutableListOf<ExtendedProduct>()

    private val kitService =
        KitService(kitDao, batchService, operationTypeService, productService, actionService, controlActionService)

    @BeforeEach
    fun resetCollections() {
        for (i in 1..3L) {
            kitList.add(ExtendedKit(i, "1-1", 3, 100, (i + 1) / 2, "TAG-${(i + 1) / 2}", 10))
            operationTypeList.add(OperationType(i, "Test", i.toInt(), 1))
            for (j in 1..300) {
                extendedProductList.add(
                    ExtendedProduct(
                        (i - 1) * 300 + j,
                        j,
                        true,
                        (((i - 1) * 300 + j + 99) / 100),
                        i
                    )
                )
            }
        }

        for (i in 1..500L) {
            extendedActionList.add(
                ExtendedAction(i, now.plus(i, DateTimeUnit.MINUTE).toLocalDateTime(TimeZone.UTC), false, 1, 1, i, true, (i + 99) / 100, (i + 299) / 300, 1)
            )
        }
        for (i in 1..350L) {
            extendedActionList.add(
                ExtendedAction(500 + i, now.plus(i, DateTimeUnit.MINUTE).toLocalDateTime(TimeZone.UTC), false, 3, 1, i, true, (i + 99) / 100, (i + 299) / 300, 1)
            )
        }

        for (i in 1..350L) {
            extendedControlActionList.add(
                ExtendedControlAction(i, now.plus(i + 1, DateTimeUnit.MINUTE).toLocalDateTime(TimeZone.UTC), true, ControlType.TESTING, "Good", 1, 1, i, true, (i + 99) / 100, (i + 299) / 300, 1)
            )
        }
        for (i in 1..150L) {
            extendedControlActionList.add(
                ExtendedControlAction(350 + i, now.plus(i + 1, DateTimeUnit.MINUTE).toLocalDateTime(TimeZone.UTC), true, ControlType.OTK, "Good", 1, 1, i, true, (i + 99) / 100, (i + 299) / 300, 1)
            )
        }

        coEvery { kitDao.getAllWithSpecification() } returns kitList
        coEvery { operationTypeService.getOperationTypesBySpecificationId(any()) } returns operationTypeList
        coEvery { actionService.getActionsBySpecificationId(any()) } returns extendedActionList
        coEvery { controlActionService.getControlActionsBySpecificationId(any()) } returns extendedControlActionList
        coEvery { productService.getProductsBySpecificationId(any()) } returns extendedProductList
    }

    @Test
    fun `base case`(): Unit = runBlocking {
        val producedProgress = kitService.getKitsProgress()
        val targetProgress = listOf(
            KitBriefProgress(1, "TAG-1", 10, 1, "1-1", 300, 300, 300, mapOf(ControlType.OTK to 150, ControlType.TESTING to 300 ), 0, 0),
            KitBriefProgress(1, "TAG-1", 10, 2, "1-1", 300, 200, 50, mapOf(ControlType.OTK to 0, ControlType.TESTING to 50), 0, 0),
            KitBriefProgress(2, "TAG-2", 10, 3, "1-1", 300, 0, 0, mapOf(ControlType.OTK to 0, ControlType.TESTING to 0), 0, 0)
        )

        assertEquals(targetProgress, producedProgress)
    }

    @Test
    fun `with defected product`(): Unit = runBlocking {
        extendedProductList[0] = ExtendedProduct(1, 1, false, 1, 1)
        extendedActionList[0] = ExtendedAction(1, now.plus(1, DateTimeUnit.MINUTE).toLocalDateTime(TimeZone.UTC), false, 1, 1, 1, false, 1, 1, 1)
        extendedActionList[500] = ExtendedAction(501, now.plus(501, DateTimeUnit.MINUTE).toLocalDateTime(TimeZone.UTC), false, 3, 1, 1, false, 1, 1, 1)
        extendedControlActionList[0] = ExtendedControlAction(1, now.plus(1 + 1, DateTimeUnit.MINUTE).toLocalDateTime(TimeZone.UTC), true, ControlType.TESTING, "Good", 1, 1, 1, false, 1, 1, 1)
        extendedControlActionList[350] = ExtendedControlAction(351, now.plus(351 + 1, DateTimeUnit.MINUTE).toLocalDateTime(TimeZone.UTC), true, ControlType.OTK, "Good", 3, 1, 1, false, 1, 1, 1)

        val producedProgress = kitService.getKitsProgress()
        val targetProgress = listOf(
            KitBriefProgress(1, "TAG-1", 10, 1, "1-1", 300, 299, 299, mapOf(ControlType.OTK to 149, ControlType.TESTING to 299), 0, 1),
            KitBriefProgress(1, "TAG-1", 10, 2, "1-1", 300, 200, 50, mapOf(ControlType.OTK to 0, ControlType.TESTING to 50), 0, 0),
            KitBriefProgress(2, "TAG-2", 10, 3, "1-1", 300, 0, 0, mapOf(ControlType.OTK to 0, ControlType.TESTING to 0), 0, 0)
        )

        assertEquals(targetProgress, producedProgress)
    }

    @Test
    fun `unsuccessful control with no repair`(): Unit = runBlocking {
        extendedControlActionList[0] = ExtendedControlAction(1, now.plus(1 + 1, DateTimeUnit.MINUTE).toLocalDateTime(TimeZone.UTC), false, ControlType.TESTING, "No Good", 1, 1, 1, true, 1, 1, 1)
        extendedControlActionList[350] = ExtendedControlAction(351, now.plus(351 + 1, DateTimeUnit.MINUTE).toLocalDateTime(TimeZone.UTC), false, ControlType.OTK, "No Good", 3, 1, 1, true, 1, 1, 1)

        val producedProgress = kitService.getKitsProgress()
        val targetProgress = listOf(
            KitBriefProgress(1, "TAG-1", 10, 1, "1-1", 300, 300, 299, mapOf(ControlType.OTK to 149, ControlType.TESTING to 299), 1, 0),
            KitBriefProgress(1, "TAG-1", 10, 2, "1-1", 300, 200, 50, mapOf(ControlType.OTK to 0, ControlType.TESTING to 50), 0, 0),
            KitBriefProgress(2, "TAG-2", 10, 3, "1-1", 300, 0, 0, mapOf(ControlType.OTK to 0, ControlType.TESTING to 0), 0, 0)
        )

        assertEquals(targetProgress, producedProgress)
    }

    @Test
    fun `with correct repair`(): Unit = runBlocking {
        extendedControlActionList[0] = ExtendedControlAction(
            1, now.plus(1 + 1, DateTimeUnit.MINUTE).toLocalDateTime(TimeZone.UTC),
            false, ControlType.TESTING, "No Good", 3,
            1, 1, true, 1, 1, 1)
        extendedActionList.add(ExtendedAction(
            1000, now.plus(1000, DateTimeUnit.MINUTE).toLocalDateTime(TimeZone.UTC),
            true,3,1, 1,true, 1,
            1, 1)
        )

        val producedProgress = kitService.getKitsProgress()
        val targetProgress = listOf(
            KitBriefProgress(1, "TAG-1", 10,
                1, "1-1", 300, 300,300,
                mapOf(ControlType.OTK to 150, ControlType.TESTING to 299), 0, 0
            ),
            KitBriefProgress(1, "TAG-1", 10, 2, "1-1", 300, 200, 50, mapOf(ControlType.OTK to 0, ControlType.TESTING to 50), 0, 0),
            KitBriefProgress(2, "TAG-2", 10, 3, "1-1", 300, 0, 0, mapOf(ControlType.OTK to 0, ControlType.TESTING to 0), 0, 0)
        )

        assertEquals(targetProgress, producedProgress)
    }

    @Test
    fun `with repair before control`(): Unit = runBlocking {
        extendedControlActionList[0] = ExtendedControlAction(1, now.plus(1 + 1, DateTimeUnit.MINUTE).toLocalDateTime(TimeZone.UTC), false, ControlType.TESTING, "No Good", 3, 1, 1, true, 1, 1, 1)
        extendedActionList.add(
            ExtendedAction(1000, now.plus(1000, DateTimeUnit.MINUTE).toLocalDateTime(TimeZone.UTC), true, 3, 1, 1, true, 1, 1, 1)
        )
        extendedControlActionList[0] = ExtendedControlAction(1000, now.plus(1000 + 1, DateTimeUnit.MINUTE).toLocalDateTime(TimeZone.UTC), false, ControlType.TESTING, "Still No Good", 3, 1, 1, true, 1, 1, 1)

        val producedProgress = kitService.getKitsProgress()
        val targetProgress = listOf(
            KitBriefProgress(1, "TAG-1", 10, 1, "1-1", 300, 300, 299, mapOf(ControlType.OTK to 150, ControlType.TESTING to 299), 1, 0),
            KitBriefProgress(1, "TAG-1", 10, 2, "1-1", 300, 200, 50, mapOf(ControlType.OTK to 0, ControlType.TESTING to 50), 0, 0),
            KitBriefProgress(2, "TAG-2", 10, 3, "1-1", 300, 0, 0, mapOf(ControlType.OTK to 0, ControlType.TESTING to 0), 0, 0)
        )

        assertEquals(targetProgress, producedProgress)
    }
}
