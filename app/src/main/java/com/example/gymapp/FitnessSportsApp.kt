package com.example.gymapp

import android.app.Application
import android.database.sqlite.SQLiteDatabase
import com.example.gymapp.data.FitnessSportsDatabase
import com.example.gymapp.data.DaoUser

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FitnessSportsApp: Application() {

    private lateinit var _dbWrite: SQLiteDatabase
    private lateinit var _dbRead: SQLiteDatabase
    private lateinit var _daoUser: DaoUser

    val daoUser: DaoUser
        get() = _daoUser

    private val applicationScope = CoroutineScope(Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        applicationScope.launch {
            val appDatabase = FitnessSportsDatabase.getInstance(applicationContext)
            _dbWrite = appDatabase.getWritableDatabase()
            _dbRead = appDatabase.getReadableDatabase()
            _daoUser = DaoUser(_dbWrite, _dbRead)
        }
    }
}