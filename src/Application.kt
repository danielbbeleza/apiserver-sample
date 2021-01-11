package com.danielbbeleza.apiserver

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.gson.gson
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.jackson
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.jetbrains.exposed.sql.Database
import javax.naming.AuthenticationException

val dao = ProductDao(
    Database.connect(
        "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;", driver =
        "org.h2.Driver"
    )
)

fun main(args: Array<String>) {
    val port = System.getenv("PORT")?.toInt() ?: 23567
    embeddedServer(Netty, host = "tranquil-taiga-04479.herokuapp.com") {
        dao.init()
        install(CallLogging)
        install(ContentNegotiation) {
            jackson { }
        }
        install(StatusPages) {
            exception<AuthenticationException> {
                call.respond(HttpStatusCode.Unauthorized)
            }
        }
        routing {
            setProductsRoute()
        }
    }.start(wait = true)
}

fun Routing.setProductsRoute() {
    route("/products") {
        get {
            call.respond(mapOf("products" to dao.getAllProducts()))
        }
        post {
            val product = call.receive<Product>()
            dao.createProduct(
                product.title,
                product.description,
                product.price
            )
            call.respond(HttpStatusCode.OK)
        }
        put {
            val product = call.receive<Product>()
            dao.updateProduct(
                product.id,
                product.title,
                product.description,
                product.price
            )
            call.respond(HttpStatusCode.Accepted)
        }
        delete("/{id}") {
            val id = call.parameters["id"]
            id?.let {
                dao.deleteProduct(id.toInt())
            }
        }
        get("/{id}") {
            val id = call.parameters["id"]
            id?.let {
                val response = dao.getProduct(id.toInt())
                if (response != null) {
                    call.respond(response)
                } else {
                    call.respond("No such product found")
                }
            }
        }
    }
}

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
}

