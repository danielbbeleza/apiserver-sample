package com.danielbbeleza.apiserver

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.Closeable

class ProductDao(private val database: Database) : DAOInterface {

    override fun init() {
        transaction(database) {
            SchemaUtils.create(Products)
        }
    }

    override fun createProduct(title: String, description: String, price: Int) {
        transaction(database) {
            Products.insert {
                it[Products.title] = title
                it[Products.description] = description
                it[Products.price] = price
            }
        }
    }

    override fun updateProduct(id: Int, title: String, description: String, price: Int) {
        transaction(database) {
            Products.update({ Products.id eq id }) {
                it[Products.title] = title
                it[Products.description] = description
                it[Products.price] = price
            }
        }
    }

    override fun deleteProduct(id: Int) {
        transaction(database) {
            Products.deleteWhere { Products.id eq id }
        }
    }

    override fun getProduct(id: Int): Product? {
        return transaction(database) {
            Products.select { Products.id eq id }.map {
                Product(
                    it[Products.id],
                    it[Products.title],
                    it[Products.description],
                    it[Products.price]
                )
            }.singleOrNull()
        }
    }

    override fun getAllProducts(): List<Product> {
        return transaction(database) {
            Products.selectAll().map {
                Product(
                    it[Products.id],
                    it[Products.title],
                    it[Products.description],
                    it[Products.price]
                )
            }
        }
    }

    override fun close() {}
}

interface DAOInterface : Closeable {
    fun init()
    fun createProduct(title: String, description: String, price: Int)
    fun updateProduct(id: Int, title: String, description: String, price: Int)
    fun deleteProduct(id: Int)
    fun getProduct(id: Int): Product?
    fun getAllProducts(): List<Product>
}