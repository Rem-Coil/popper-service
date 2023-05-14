package com.remcoil.service.operator

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.remcoil.config.JwtConfig
import com.remcoil.dao.operator.OperatorDao
import com.remcoil.data.model.operator.Operator
import com.remcoil.data.model.operator.OperatorCredentials
import com.remcoil.data.model.operator.OperatorRole
import com.remcoil.utils.exceptions.WrongParamException
import com.remcoil.utils.logger
import io.ktor.server.plugins.*
import java.util.*


class OperatorService(private val operatorDao: OperatorDao, private val config: JwtConfig) {

    suspend fun getActiveOperator(credentials: OperatorCredentials): String? {
        val operator = operatorDao.getOperator(credentials.phone)

        if (operator != null && credentials.password == operator.password && operator.active) {
            logger.info("Вернули оператора - ${operator.firstName} ${operator.surname}")
            return generateToken(operator)
        }
        logger.info("Оператор не найден")
        return null
    }

    suspend fun getAllOperators(onlyActive: Boolean): List<Operator> {
        val operators = operatorDao.getAllOperators(onlyActive)
        logger.info("Отдали всех операторов")
        return operators
    }

    suspend fun createOperator(operator: Operator): String? {
        val op = if (operatorDao.isNotExist(operator) && checkRole(operator)) generateToken(
            operatorDao.createOperator(operator)
        ) else null
        if (op == null) {
            logger.info("Некорректные данные")
        } else {
            logger.info("Сохранили данные об операторе")
        }
        return op
    }

    suspend fun setOperatorState(id: Int, active: Boolean) {
        if (operatorDao.isExist(id)) {
            operatorDao.setActive(id, active)
        } else {
            throw NotFoundException("Оператор не найден")
        }
        logger.info("Оператор заархивирован")
    }

    suspend fun deleteOperator(id: Int) {
        operatorDao.deleteOperator(id)
        logger.info("Данные об операторе удалены")
    }

    suspend fun updateOperator(operator: Operator) {
        if (checkRole(operator) && operatorDao.isExist(operator.id)) {
            operatorDao.updateOperator(operator)
        } else {
            throw WrongParamException("Некорректные данные")
        }
        logger.info("Обновили данные оператора")
    }

    private fun generateToken(operator: Operator) = JWT.create()
        .withClaim("id", operator.id)
        .withClaim("first_name", operator.firstName)
        .withClaim("second_name", operator.secondName)
        .withClaim("surname", operator.surname)
        .withClaim("phone", operator.phone)
        .withClaim("role", operator.role)
        .withExpiresAt(Date(System.currentTimeMillis() + config.time))
        .sign(Algorithm.HMAC256(config.secret))

    private fun checkRole(operator: Operator): Boolean {
        if (operator.role == OperatorRole.OPERATOR.type ||
            operator.role == OperatorRole.QUALITY_ENGINEER.type ||
            operator.role == OperatorRole.ADMIN.type
        ) {
            return true
        }
        return false
    }
}