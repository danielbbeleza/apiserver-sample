package com.danielbbeleza.apiserver.daos

import com.danielbbeleza.apiserver.models.Friend
import com.danielbbeleza.apiserver.tables.Friends
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class FriendsDaoImpl(private val database: Database) : FriendsDao {

    override fun init() {
        transaction(database) {
            SchemaUtils.create(Friends)
        }
    }

    override fun createFriend(name: String, phoneNumber: String) {
        transaction(database) {
            Friends.insert {
                it[Friends.name] = name
                it[Friends.phoneNumber] = phoneNumber
            }
        }
    }

    override fun updateFriend(id: Int, name: String, phoneNumber: String) {
        transaction(database) {
            Friends.update({ Friends.id eq id }) {
                it[Friends.name] = name
                it[Friends.phoneNumber] = phoneNumber
            }
        }
    }

    override fun deleteFriend(id: Int) {
        transaction(database) {
            Friends.deleteWhere { Friends.id eq id }
        }
    }

    override fun getFriend(id: Int): Friend? {
        return transaction(database) {
            Friends.select { Friends.id eq id }.map {
                Friend(
                    it[Friends.id],
                    it[Friends.name],
                    it[Friends.phoneNumber]
                )
            }.singleOrNull()
        }
    }

    override fun getAllFriends(): List<Friend> {
        return transaction(database) {
            Friends.selectAll().map {
                Friend(
                    it[Friends.id],
                    it[Friends.name],
                    it[Friends.phoneNumber]
                )
            }
        }
    }

    override fun close() {}
}