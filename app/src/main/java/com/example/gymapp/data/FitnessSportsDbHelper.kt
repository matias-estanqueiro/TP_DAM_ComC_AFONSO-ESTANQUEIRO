package com.example.gymapp.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


class FitnessSportsDbHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
        override fun onCreate(db: SQLiteDatabase?) {
            val createUsersTable = """
                CREATE TABLE $TABLE_USERS (
                    $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                    $COLUMN_MAIL TEXT UNIQUE NOT NULL,
                    $COLUMN_PASSWORD TEXT NOT NULL
                )
            """.trimIndent()

            if (db != null) db.execSQL(createUsersTable) else throw NullPointerException("Expression 'db' must not be null")

        }

        override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
            db?.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
            onCreate(db)
        }

        companion object {
            const val DATABASE_NAME = "FitnessSports.db"
            const val DATABASE_VERSION = 1

            const val TABLE_USERS = "users"
            const val COLUMN_ID = "id"
            const val COLUMN_MAIL = "mail"
            const val COLUMN_PASSWORD = "password"
        }
    }