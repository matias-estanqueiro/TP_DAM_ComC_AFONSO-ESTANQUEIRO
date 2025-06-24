package com.example.gymapp

import android.app.Application
import android.database.sqlite.SQLiteDatabase
import com.example.gymapp.data.dao.DaoActivity
import com.example.gymapp.data.FitnessSportsDatabase
import com.example.gymapp.data.dao.DaoUser
import com.example.gymapp.data.dao.DaoClient
import com.example.gymapp.data.dao.DaoMembership
import com.example.gymapp.data.dao.DaoPaymentType
import com.example.gymapp.data.payments.DaoPayment

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FitnessSportsApp: Application() {

    private lateinit var _dbWrite: SQLiteDatabase
    private lateinit var _dbRead: SQLiteDatabase

    private lateinit var _daoUser: DaoUser
    private lateinit var _daoClient : DaoClient
    private lateinit var _daoActivity : DaoActivity
    private lateinit var _daoPaymentType : DaoPaymentType
    private lateinit var _daoPayment : DaoPayment
    private lateinit var _daoMembership : DaoMembership

    val daoUser: DaoUser
        get() = _daoUser

    val daoClient : DaoClient
        get() = _daoClient

    val daoActivity : DaoActivity
        get() = _daoActivity

    val daoPaymentType : DaoPaymentType
        get() = _daoPaymentType

    val daoPayment : DaoPayment
        get() = _daoPayment

    val daoMembership : DaoMembership
        get() = _daoMembership

    private val applicationScope = CoroutineScope(Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        applicationScope.launch {
            val appDatabase = FitnessSportsDatabase.getInstance(applicationContext)
            _dbWrite = appDatabase.getWritableDatabase()
            _dbRead = appDatabase.getReadableDatabase()

            _daoUser = DaoUser(_dbWrite, _dbRead)
            _daoClient = DaoClient(_dbWrite, _dbRead)
            _daoActivity = DaoActivity(_dbWrite, _dbRead)
            _daoPaymentType = DaoPaymentType(_dbWrite, _dbRead)
            _daoPayment = DaoPayment(_dbWrite, _dbRead)
            _daoMembership = DaoMembership(_dbWrite, _dbRead)
        }
    }
}