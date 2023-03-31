package com.remcoil.service.employee

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.remcoil.config.JwtConfig
import com.remcoil.dao.employee.EmployeeDao
import com.remcoil.data.model.employee.Employee
import com.remcoil.data.model.employee.EmployeeCredentials
import com.remcoil.data.model.employee.EmployeeRole
import com.remcoil.utils.exceptions.WrongParamException
import com.remcoil.utils.logger
import io.ktor.server.plugins.*
import java.util.*


class EmployeeService(private val employeeDao: EmployeeDao, private val config: JwtConfig) {

    suspend fun getActiveEmployee(credentials: EmployeeCredentials): String? {
        val operator = employeeDao.getByPhone(credentials.phone)

        if (operator != null && credentials.password == operator.password && operator.active) {
            logger.info("Вернули оператора - ${operator.firstName} ${operator.surname}")
            return generateToken(operator)
        }
        logger.info("Оператор не найден")
        return null
    }

    suspend fun getAllEmployees(onlyActive: Boolean): List<Employee> {
        val operators = employeeDao.getAll(onlyActive)
        logger.info("Отдали всех операторов")
        return operators
    }

    suspend fun createEmployee(employee: Employee): String? {
        val op = if (employeeDao.isNotExist(employee) && checkRole(employee)) generateToken(
            employeeDao.create(employee)
        ) else null
        if (op == null) {
            logger.info("Некорректные данные")
        } else {
            logger.info("Сохранили данные об операторе")
        }
        return op
    }

    suspend fun setEmployeeState(id: Long, active: Boolean) {
        if (employeeDao.isExist(id)) {
            employeeDao.setActive(id, active)
        } else {
            throw NotFoundException("Оператор не найден")
        }
        logger.info("Оператор заархивирован")
    }

    suspend fun deleteEmployee(id: Long) {
        employeeDao.deleteById(id)
        logger.info("Данные об операторе удалены")
    }

    suspend fun updateEmployee(employee: Employee) {
        if (checkRole(employee) && employeeDao.isExist(employee.id)) {
            employeeDao.update(employee)
        } else {
            throw WrongParamException("Некорректные данные")
        }
        logger.info("Обновили данные оператора")
    }

    private fun generateToken(employee: Employee) = JWT.create()
        .withClaim("id", employee.id)
        .withClaim("first_name", employee.firstName)
        .withClaim("second_name", employee.secondName)
        .withClaim("surname", employee.surname)
        .withClaim("phone", employee.phone)
        .withClaim("role", employee.role)
        .withExpiresAt(Date(System.currentTimeMillis() + config.time))
        .sign(Algorithm.HMAC256(config.secret))

//    TODO: Проверять роли при сериализации
    private fun checkRole(employee: Employee): Boolean {
        if (employee.role == EmployeeRole.OPERATOR.type ||
            employee.role == EmployeeRole.QUALITY_ENGINEER.type ||
            employee.role == EmployeeRole.ADMIN.type
        ) {
            return true
        }
        return false
    }
}