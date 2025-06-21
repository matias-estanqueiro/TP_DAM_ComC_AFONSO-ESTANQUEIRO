package com.example.gymapp

import android.app.Application
import android.database.sqlite.SQLiteDatabase
import com.example.gymapp.data.FitnessSportsDatabase
import com.example.gymapp.data.DaoUser
import com.example.gymapp.data.DaoClient

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FitnessSportsApp: Application() {

    private lateinit var _dbWrite: SQLiteDatabase
    private lateinit var _dbRead: SQLiteDatabase

    private lateinit var _daoUser: DaoUser
    private lateinit var _daoClient : DaoClient

    val daoUser: DaoUser
        get() = _daoUser

    val daoClient : DaoClient
        get() = _daoClient

    private val applicationScope = CoroutineScope(Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        applicationScope.launch {
            val appDatabase = FitnessSportsDatabase.getInstance(applicationContext)
            _dbWrite = appDatabase.getWritableDatabase()
            _dbRead = appDatabase.getReadableDatabase()

            _daoUser = DaoUser(_dbWrite, _dbRead)
            _daoClient = DaoClient(_dbWrite, _dbRead)
        }
    }
}