package com.example.gymapp.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase

// By making the constructor private, we prevent that any other class can directly create an
// instance of AppDatabase using new AppDatabase(...). We force that the instance can only be
// obtained through the static method getInstance().
class FitnessSportsDatabase private constructor(context: Context) {

    private val databaseHelper: DatabaseHelper = DatabaseHelper(context)
    fun getReadableDatabase() : SQLiteDatabase = databaseHelper.readableDatabase
    fun getWritableDatabase() : SQLiteDatabase = databaseHelper.writableDatabase

    companion object {
        @Volatile
        private var INSTANCE: FitnessSportsDatabase? = null

        fun getInstance(context: Context): FitnessSportsDatabase {
            // If the instance already exists, it returns it.
            // If not, it safely creates a new one (synchronized).
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: FitnessSportsDatabase(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}