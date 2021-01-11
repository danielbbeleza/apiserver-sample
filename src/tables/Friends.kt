package com.danielbbeleza.apiserver.tables

import org.jetbrains.exposed.sql.Table

object Friends : Table() {
    val id = integer("id").primaryKey().autoIncrement()
    val name = varchar("name", 100)
    val phoneNumber = varchar("phone_number", 500)
}