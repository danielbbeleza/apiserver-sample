package com.danielbbeleza.apiserver.daos

import com.danielbbeleza.apiserver.models.Friend
import java.io.Closeable

interface FriendsDao : Closeable {
    fun init()
    fun createFriend(name: String, phoneNumber: String)
    fun updateFriend(id: Int, name: String, phoneNumber: String)
    fun deleteFriend(id: Int)
    fun getFriend(id: Int): Friend?
    fun getAllFriends(): List<Friend>
}