package com.remcoil.service.operator

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.remcoil.data.model.OperatorCredentials
import com.remcoil.dao.operator.OperatorDao
import com.remcoil.data.model.Operator
import java.util.*


class OperatorService(private val operatorDao: OperatorDao) {
    val expiredTime = 86400000
    val secret = "secret"

    fun getOperator(credentials: OperatorCredentials): String? {
        val operator = operatorDao.getOperator(credentials.phone)

        if (credentials.password == operator.password) {
            return generateToken(operator)
        }
        return null
    }

    fun createOperator(credentials: OperatorCredentials): String {
        val operator = operatorDao.createOperator(Operator(credentials))
        return generateToken(operator)
    }

    private fun generateToken(operator: Operator) = JWT.create()
        .withClaim("firstname", operator.firstname)
        .withClaim("second_name", operator.second_name)
        .withClaim("surname", operator.surname)
        .withClaim("phone", operator.phone)
        .withExpiresAt(Date(System.currentTimeMillis() + expiredTime))
        .sign(Algorithm.HMAC256(secret))
}