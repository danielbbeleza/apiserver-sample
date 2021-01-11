package com.danielbbeleza.apiserver

import com.danielbbeleza.apiserver.daos.FriendsDaoImpl
import com.danielbbeleza.apiserver.models.Friend
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.gson.*
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.jackson
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.jetbrains.exposed.sql.Database
import javax.naming.AuthenticationException

val dao = FriendsDaoImpl(
    Database.connect(
        "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;", driver =
        "org.h2.Driver"
    )
)

fun main(args: Array<String>) {
    val port = System.getenv("PORT")?.toInt() ?: 23567
    embeddedServer(Netty, port) {
        dao.init()
        install(CallLogging)
        install(ContentNegotiation) {
            gson {}
        }
        install(StatusPages) {
            exception<AuthenticationException> {
                call.respond(HttpStatusCode.Unauthorized)
            }
        }
        routing {
            setFriendsRoute()
        }
    }.start(wait = true)
}

fun Routing.setFriendsRoute() {
    route("/friends") {
        get {
            call.respond(mapOf("friends" to dao.getAllFriends()))
        }
        post {
            val friend = call.receive<Friend>()
            dao.createFriend(
                friend.name,
                friend.phoneNumber
            )
            call.respond(HttpStatusCode.OK)
        }
        put {
            val friend = call.receive<Friend>()
            dao.updateFriend(
                friend.id,
                friend.name,
                friend.phoneNumber
            )
            call.respond(HttpStatusCode.Accepted)
        }
        delete("/{id}") {
            val id = call.parameters["id"]
            id?.let {
                dao.deleteFriend(id.toInt())
            }
        }
        get("/{id}") {
            val id = call.parameters["id"]
            id?.let {
                val response = dao.getFriend(id.toInt())
                if (response != null) {
                    call.respond(response)
                } else {
                    call.respond("No such friend found")
                }
            }
        }
    }
}

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
}

