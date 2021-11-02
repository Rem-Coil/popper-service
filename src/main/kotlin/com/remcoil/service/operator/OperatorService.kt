package com.remcoil.service.operator

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.remcoil.config.JwtConfig
import com.remcoil.data.model.operator.OperatorCredentials
import com.remcoil.dao.operator.OperatorDao
import com.remcoil.data.model.operator.Operator
import java.util.*


class OperatorService(private val operatorDao: OperatorDao, private val config: JwtConfig) {

    fun getOperator(credentials: OperatorCredentials): String? {
        val operator = operatorDao.getOperator(credentials.phone)

        if (operator != null && credentials.password == operator.password) {
            return generateToken(operator)
        }
        return null
    }

    fun getAllOperators(): List<Operator> {
        return operatorDao.getAllOperators()
    }

    fun createOperator(operator: Operator): String {
        val operator = operatorDao.createOperator(operator)
        return generateToken(operator)
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