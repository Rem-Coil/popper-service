package com.remcoil.service

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.remcoil.config.JwtConfig
import com.remcoil.dao.EmployeeDao
import com.remcoil.model.dto.Employee
import com.remcoil.model.dto.EmployeeCredentials
import com.remcoil.utils.exceptions.DuplicateValueException
import com.remcoil.utils.exceptions.EntryDoesNotExistException
import com.remcoil.utils.exceptions.WrongParamException
import java.util.*


class EmployeeService(private val employeeDao: EmployeeDao, private val config: JwtConfig) {

    suspend fun getEmployeeByCredentials(credentials: EmployeeCredentials): String {
        val employee = employeeDao.getByPhone(credentials.phone)
            ?: throw EntryDoesNotExistException("Employee with this number was not found")
        if (credentials.password == employee.password && employee.active) {
            return generateToken(employee)
        } else throw WrongParamException("Wrong password")
    }

    suspend fun getAllEmployees(onlyActive: Boolean): List<Employee> {
        return employeeDao.getAll(onlyActive)
    }

    suspend fun createEmployee(employee: Employee): String {
        if (employeeDao.isNotExist(employee)) {
            val createdEmployee = employeeDao.create(employee)
            return generateToken(createdEmployee)
        } else throw DuplicateValueException("Employee with this number already exists")
    }

    suspend fun updateEmployee(employee: Employee) {
        employeeDao.update(employee)
    }

    suspend fun updateEmployeeState(id: Long, active: Boolean) {
        employeeDao.updateState(id, active)
    }

    suspend fun deleteEmployeeById(id: Long) {
        employeeDao.deleteById(id)
    }

    private fun generateToken(employee: Employee) = JWT.create()
        .withClaim("id", employee.id)
        .withClaim("first_name", employee.firstName)
        .withClaim("last_name", employee.lastName)
        .withClaim("phone", employee.phone)
        .withClaim("role", employee.role.name)
        .withExpiresAt(Date(System.currentTimeMillis() + config.time))
        .sign(Algorithm.HMAC256(config.secret))

}