package com.danielbbeleza.apiserver.tables

import org.jetbrains.exposed.sql.Table

object Groups : Table() {
    val id = integer("id").primaryKey().autoIncrement()
    val name = varchar("name", 100)
}