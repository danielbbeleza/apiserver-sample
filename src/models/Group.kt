package com.danielbbeleza.apiserver.models

import org.jetbrains.exposed.sql.Table

data class Group(val id: Int, val name: String): Table()