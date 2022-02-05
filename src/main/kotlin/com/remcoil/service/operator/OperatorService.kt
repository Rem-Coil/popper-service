package com.remcoil.service.operator

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.remcoil.config.JwtConfig
import com.remcoil.data.model.operator.OperatorCredentials
import com.remcoil.dao.operator.OperatorDao
import com.remcoil.data.model.operator.Operator
import com.remcoil.utils.logger
import java.util.*
import kotlin.math.log


class OperatorService(private val dao: OperatorDao, private val config: JwtConfig) {

    fun getOperator(credentials: OperatorCredentials): String? {
        val operator = dao.getOperator(credentials.phone)

        if (operator != null && credentials.password == operator.password) {
            logger.info("Вернули оператора - ${operator.firstName} ${operator.surname}")
            return generateToken(operator)
        }
        logger.info("Оператор не найден")
        return null
    }

    fun getAllOperators(onlyActive: Boolean): List<Operator> {
        val operators = dao.getAllOperators(onlyActive)
        logger.info("Отдали всех операторов")
        return operators
    }

    fun createOperator(operator: Operator): String? {
        val op = if (dao.isNotExist(operator)) generateToken(dao.createOperator(operator)) else null
        if (op == null) {
            logger.info("Оператор с такими данными уже существует")
        } else {
            logger.info("Сохранили данные об операторе")
        }
        return op
    }

    fun deleteOperator(id: Int) {
        dao.deleteOperator(id)
        logger.info("Данные об операторе удалены")
    }

    private fun generateToken(operator: Operator) = JWT.create()
        .withClaim("id", operator.id)
        .withClaim("first_name", operator.firstName)
        .withClaim("second_name", operator.secondName)
        .withClaim("surname", operator.surname)
        .withClaim("phone", operator.phone)
        .withExpiresAt(Date(System.currentTimeMillis() + config.time))
        .sign(Algorithm.HMAC256(config.secret))
}