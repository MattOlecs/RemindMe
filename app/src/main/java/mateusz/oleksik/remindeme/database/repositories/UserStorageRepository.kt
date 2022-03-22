package mateusz.oleksik.remindeme.database.repositories

import android.content.Context
import mateusz.oleksik.remindeme.User
import mateusz.oleksik.remindeme.database.DAOs.UserDAO

class UserStorageRepository(context: Context) : AbstractStorageRepository(context) {

    private var userDAO: UserDAO = RemindeMeDb.userDAO()

    suspend fun getAll(): List<User>{
        return userDAO.getAll()
    }

    suspend fun loadAllByIds(userIds: IntArray): List<User>{
        return userDAO.loadAllByIds(userIds)
    }

    suspend fun findByName(first: String, last: String): User {
        return userDAO.findByName(first, last)
    }

    suspend fun insertAll(vararg users: User){
        userDAO.insertAll(*users)
    }

    suspend fun delete(user: User){
        userDAO.delete(user)
    }
}