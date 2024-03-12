package com.remcoil.dao

import com.remcoil.model.database.Employees
import com.remcoil.model.dto.Employee
import com.remcoil.utils.safetySuspendTransactionAsync
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class EmployeeDao(private val database: Database) {
    suspend fun getByPhone(phone: String): Employee? = safetySuspendTransactionAsync(database) {
        Employees
            .select { (Employees.phone eq phone) and (Employees.active eq true)}
            .map (::extractEmployee)
            .firstOrNull()
    }

    suspend fun getAll(onlyActive: Boolean): List<Employee> = safetySuspendTransactionAsync(database) {
        Employees
            .select { (Employees.active eq true) or (Employees.active eq onlyActive)}
            .map(::extractEmployee)
    }

    suspend fun getById(id: Long): Employee? = safetySuspendTransactionAsync(database) {
        Employees
            .select { Employees.id eq id }
            .map(::extractEmployee)
            .firstOrNull()
    }

    suspend fun isNotExist(employee: Employee): Boolean = safetySuspendTransactionAsync(database) {
        Employees
            .select { (Employees.active eq true) and (Employees.phone eq employee.phone) }
            .map(::extractEmployee)
            .isEmpty()
    }

    suspend fun create(employee: Employee): Employee = safetySuspendTransactionAsync(database) {
        val id = Employees.insertAndGetId {
            it[firstName] = employee.firstName
            it[lastName] = employee.lastName
            it[phone] = employee.phone
            it[password] = employee.password
            it[active] = true
            it[role] = employee.role.name
        }
        employee.copy(id = id.value)
    }

    suspend fun updateState(id: Long, active: Boolean) = safetySuspendTransactionAsync(database) {
        Employees.update({ Employees.id eq id }) {
            it[Employees.active] = active
        }
    }

    suspend fun update(employee: Employee) = safetySuspendTransactionAsync(database) {
        Employees.update({ Employees.id eq employee.id}) {
            it[firstName] = employee.firstName
            it[lastName] = employee.lastName
            it[phone] = employee.phone
            it[password] = employee.password
            it[active] = employee.active
            it[role] = employee.role.name
        }
    }

    suspend fun deleteById(id: Long) = safetySuspendTransactionAsync(database) {
        Employees.deleteWhere { Employees.id eq id }
    }

    private fun extractEmployee(row: ResultRow): Employee = Employee(
        row[Employees.id].value,
        row[Employees.firstName],
        row[Employees.lastName],
        row[Employees.phone],
        row[Employees.password],
        row[Employees.active],
        com.remcoil.model.dto.EmployeeRole.valueOf(row[Employees.role])
    )
}