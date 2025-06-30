package com.example.gymapp.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

import com.example.gymapp.utils.encryptPassword

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    // Database tables structure definitions
    companion object {
        private const val DATABASE_NAME = "fitnessSports.db"
        private const val DATABASE_VERSION = 19

        // Users Table
        const val TABLE_USERS = "users"
        const val COLUMN_USER_ID = "id"
        const val COLUMN_USER_EMAIL = "email"
        const val COLUMN_USER_PASSWORD = "password"

        // Activities Table
        const val TABLE_ACTIVITIES = "activities"
        const val COLUMN_ACTIVITY_ID = "id"
        const val COLUMN_ACTIVITY_NAME = "name"
        const val COLUMN_ACTIVITY_PRICE = "price"

        // Membership Plans Table
        const val TABLE_MEMBERSHIPS = "memberships"
        const val COLUMN_MEMBERSHIP_ID = "id"
        const val COLUMN_MEMBERSHIP_NAME = "name"
        const val COLUMN_MEMBERSHIP_PRICE = "price"

        // Payment Methods Table
        const val TABLE_PAYMENTS_METHODS = "payments_methods"
        const val COLUMN_PAYMENT_METHOD_ID = "id"
        const val COLUMN_PAYMENT_METHOD_DESCRIPTION = "description"

        // Clients Table
        const val TABLE_CLIENTS = "clients"
        const val COLUMN_CLIENT_ID = "id"
        const val COLUMN_CLIENT_DNI = "dni"
        const val COLUMN_CLIENT_NAME = "name"
        const val COLUMN_CLIENT_SURNAME = "surname"
        const val COLUMN_CLIENT_STREET = "street"
        const val COLUMN_CLIENT_NUMBER = "street_number"
        const val COLUMN_CLIENT_DISTRICT = "district"
        const val COLUMN_CLIENT_PHONE = "phone"
        const val COLUMN_CLIENT_EMAIL = "email"
        // 0 => client; 1 => member
        const val COLUMN_CLIENT_TYPE = "type"
        const val COLUMN_CLIENT_PLAN = "membership_id"

        // Membership Payments Table
        const val TABLE_MEMBERSHIPS_PAYMENTS = "payments_memberships"
        const val COLUMN_MEMBERSHIP_PAYMENT_ID = "id"
        const val COLUMN_MEMBERSHIP_PAYMENT_MEMBERSHIP_ID = "membership_id"
        const val COLUMN_MEMBERSHIP_PAYMENT_METHOD_ID = "payment_method_id"
        const val COLUMN_MEMBERSHIP_PAYMENT_CLIENT_ID = "client_id"
        const val COLUMN_MEMBERSHIP_PAYMENT_AMOUNT = "amount"
        const val COLUMN_MEMBERSHIP_PAYMENT_DATE = "date"
        const val COLUMN_MEMBERSHIP_PAYMENT_DUE_DATE = "due_date"

        // Activities Payments Table
        const val TABLE_ACTIVITIES_PAYMENTS = "payments_activities"
        const val COLUMN_ACTIVITY_PAYMENT_ID = "id"
        const val COLUMN_ACTIVITY_PAYMENT_ACTIVITY_ID = "activity_id"
        const val COLUMN_ACTIVITY_PAYMENT_METHOD_ID = "payment_method_id"
        const val COLUMN_ACTIVITY_PAYMENT_CLIENT_ID = "client_id"
        const val COLUMN_ACTIVITY_PAYMENT_DATE = "date"

    }

    override fun onCreate(db: SQLiteDatabase) {

        // Tables Creation
        val createUsersTable = """
            CREATE TABLE $TABLE_USERS (
                $COLUMN_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USER_EMAIL TEXT UNIQUE NOT NULL,
                $COLUMN_USER_PASSWORD TEXT NOT NULL
            )""".trimIndent()

        val createActivitiesTable = """
            CREATE TABLE $TABLE_ACTIVITIES (
                $COLUMN_ACTIVITY_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_ACTIVITY_NAME TEXT UNIQUE NOT NULL,
                $COLUMN_ACTIVITY_PRICE INTEGER NOT NULL
            )""".trimIndent()

        val createMembershipsTable = """
            CREATE TABLE $TABLE_MEMBERSHIPS (
                    $COLUMN_MEMBERSHIP_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                    $COLUMN_MEMBERSHIP_NAME TEXT NOT NULL,
                    $COLUMN_MEMBERSHIP_PRICE INTEGER NOT NULL
            )""".trimIndent()

        val createPaymentsMethodsTable = """
            CREATE TABLE $TABLE_PAYMENTS_METHODS (
                    $COLUMN_PAYMENT_METHOD_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                    $COLUMN_PAYMENT_METHOD_DESCRIPTION TEXT NOT NULL UNIQUE
            )""".trimIndent()

        val createClientsTable = """
            CREATE TABLE $TABLE_CLIENTS (
                $COLUMN_CLIENT_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_CLIENT_DNI TEXT UNIQUE NOT NULL,
                $COLUMN_CLIENT_NAME TEXT NOT NULL,
                $COLUMN_CLIENT_SURNAME TEXT NOT NULL,
                $COLUMN_CLIENT_STREET TEXT,
                $COLUMN_CLIENT_NUMBER TEXT,
                $COLUMN_CLIENT_DISTRICT TEXT,
                $COLUMN_CLIENT_PHONE TEXT,
                $COLUMN_CLIENT_EMAIL TEXT UNIQUE,
                $COLUMN_CLIENT_TYPE INTEGER NOT NULL,
                $COLUMN_CLIENT_PLAN INTEGER NOT NULL,
                FOREIGN KEY($COLUMN_CLIENT_PLAN) REFERENCES $TABLE_MEMBERSHIPS($COLUMN_MEMBERSHIP_ID)
            )""".trimIndent()

        val createPaymentsMembershipsTable = """
            CREATE TABLE $TABLE_MEMBERSHIPS_PAYMENTS (
                $COLUMN_MEMBERSHIP_PAYMENT_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_MEMBERSHIP_PAYMENT_MEMBERSHIP_ID INTEGER NOT NULL,
                $COLUMN_MEMBERSHIP_PAYMENT_METHOD_ID INTEGER NOT NULL,
                $COLUMN_MEMBERSHIP_PAYMENT_CLIENT_ID INTEGER NOT NULL,
                $COLUMN_MEMBERSHIP_PAYMENT_AMOUNT INTEGER NOT NULL,
                $COLUMN_MEMBERSHIP_PAYMENT_DATE DATE NOT NULL,
                $COLUMN_MEMBERSHIP_PAYMENT_DUE_DATE DATE NOT NULL,
                FOREIGN KEY($COLUMN_MEMBERSHIP_PAYMENT_MEMBERSHIP_ID) REFERENCES $TABLE_MEMBERSHIPS($COLUMN_MEMBERSHIP_ID),
                FOREIGN KEY($COLUMN_MEMBERSHIP_PAYMENT_METHOD_ID) REFERENCES $TABLE_PAYMENTS_METHODS($COLUMN_PAYMENT_METHOD_ID),
                FOREIGN KEY($COLUMN_MEMBERSHIP_PAYMENT_CLIENT_ID) REFERENCES $TABLE_CLIENTS($COLUMN_CLIENT_ID)
            )
            """.trimIndent()

        val createPaymentsActivitiesTable = """
            CREATE TABLE $TABLE_ACTIVITIES_PAYMENTS (
                $COLUMN_ACTIVITY_PAYMENT_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_ACTIVITY_PAYMENT_ACTIVITY_ID INTEGER NOT NULL,
                $COLUMN_ACTIVITY_PAYMENT_METHOD_ID INTEGER NOT NULL,
                $COLUMN_ACTIVITY_PAYMENT_CLIENT_ID INTEGER NOT NULL,
                $COLUMN_ACTIVITY_PAYMENT_DATE DATE NOT NULL,
                FOREIGN KEY($COLUMN_ACTIVITY_PAYMENT_ACTIVITY_ID) REFERENCES $TABLE_ACTIVITIES($COLUMN_ACTIVITY_ID),
                FOREIGN KEY($COLUMN_ACTIVITY_PAYMENT_METHOD_ID) REFERENCES $TABLE_PAYMENTS_METHODS($COLUMN_PAYMENT_METHOD_ID),
                FOREIGN KEY($COLUMN_ACTIVITY_PAYMENT_CLIENT_ID) REFERENCES $TABLE_CLIENTS($COLUMN_CLIENT_ID)
            )""".trimIndent()

        db.execSQL(createUsersTable)
        db.execSQL(createActivitiesTable)
        db.execSQL(createMembershipsTable)
        db.execSQL(createPaymentsMethodsTable)
        db.execSQL(createClientsTable)
        db.execSQL(createPaymentsMembershipsTable)
        db.execSQL(createPaymentsActivitiesTable)

        // Insert tables data

        // Users
        val adminUserData = ContentValues().apply {
            put(COLUMN_USER_EMAIL, "admin@fitness.com")
            put(COLUMN_USER_PASSWORD, encryptPassword("admin1234"))
        }
        db.insert(TABLE_USERS, null, adminUserData)

        // Activities
        val activities : List<Pair<String,Int>> = listOf(
            Pair("FUNCTIONAL", 1500),
            Pair("PILATES", 3000),
            Pair("ZUMBA", 1000),
            Pair("YOGA", 2000),
            Pair("POWER JUMP", 3000)
        )
        for (activity in activities) {
            val activityData = ContentValues().apply {
                put(COLUMN_ACTIVITY_NAME, activity.first)
                put(COLUMN_ACTIVITY_PRICE, activity.second)
            }
            db.insert(TABLE_ACTIVITIES, null, activityData)
        }

        //Memberships
        val memberships : List<Pair<String,Int>> = listOf(
            Pair("NO PLAN", 0),
            Pair("BRONZE", 35000),
            Pair("SILVER", 45000),
            Pair("GOLD", 55000),
            Pair("PLATINUM", 70000)
        )
        for (membership in memberships) {
            val membershipData = ContentValues().apply {
                put(COLUMN_MEMBERSHIP_NAME, membership.first)
                put(COLUMN_MEMBERSHIP_PRICE, membership.second)
            }
            db.insert(TABLE_MEMBERSHIPS, null, membershipData)
        }


        // Payments Methods
        val paymentsMethods = listOf("CASH", "QR", "DEBIT CARD", "CREDIT CARD")
        for (method in paymentsMethods) {
            val paymentData = ContentValues().apply {
                put(COLUMN_PAYMENT_METHOD_DESCRIPTION, method)
            }
            db.insert(TABLE_PAYMENTS_METHODS, null, paymentData)
        }

        // Clients & Members
        val clientsList = mutableListOf<ContentValues>()

        val names = listOf("MARTIN", "SOFIA", "DIEGO", "VALERIA", "PABLO", "LAURA", "ENZO", "CAMILA", "FACUNDO", "ANDREA")
        val surnames = listOf("GOMEZ", "PEREZ", "RODRIGUEZ", "FERNANDEZ", "DIAZ", "CASTRO", "RUIZ", "SILVA", "PEREYRA", "NUNEZ")
        val streets = listOf("AVENIDA CORRIENTES", "CALLE FALSA", "RUTA 3", "LIBERTAD", "SAN MARTIN", "BELGRANO", "LAS HERAS", "CORDOBA", "ENTRE RIOS", "9 DE JULIO")
        val districts = listOf("CABA", "CORDOBA", "BUENOS AIRES", "ROSARIO", "MENDOZA", "LA PLATA", "SALTA", "TUCUMAN", "CORRIENTES", "MAR DEL PLATA")
        val phonePrefixes = listOf("11", "221", "341", "351", "261", "387", "381", "379")
        val dniBaseMember = 20000000
        val dniBaseClient = 30000000

        // Members
        for (i in 0 until 5) {
            val dni = (dniBaseMember + i + 1).toString()
            val plan = (2..5).random()
            val name = names[i]
            val surname = surnames[i]
            val street = streets[i]
            val number = (100 + i * 100).toString()
            val district = districts[i]
            val phone = phonePrefixes.random() + (1000000 + i * 12345).toString()
            val email = "${name.lowercase()}.${surname.lowercase()}@member.com.ar"

            clientsList.add(ContentValues().apply {
                put(COLUMN_CLIENT_DNI, dni)
                put(COLUMN_CLIENT_NAME, name)
                put(COLUMN_CLIENT_SURNAME, surname)
                put(COLUMN_CLIENT_STREET, street)
                put(COLUMN_CLIENT_NUMBER, number)
                put(COLUMN_CLIENT_DISTRICT, district)
                put(COLUMN_CLIENT_PHONE, phone)
                put(COLUMN_CLIENT_EMAIL, email)
                put(COLUMN_CLIENT_TYPE, 1)
                put(COLUMN_CLIENT_PLAN, plan)
            })
        }

        // Clients
        for (i in 5 until 10) {
            val dni = (dniBaseClient + i + 1).toString()
            val name = names[i]
            val surname = surnames[i]
            val street = streets[i]
            val number = (2000 + i * 50).toString()
            val district = districts[i]
            val phone = phonePrefixes.random() + (2000000 + i * 67890).toString()
            val email = "${name.lowercase()}.${surname.lowercase()}@client.com.ar"

            clientsList.add(ContentValues().apply {
                put(COLUMN_CLIENT_DNI, dni)
                put(COLUMN_CLIENT_NAME, name)
                put(COLUMN_CLIENT_SURNAME, surname)
                put(COLUMN_CLIENT_STREET, street)
                put(COLUMN_CLIENT_NUMBER, number)
                put(COLUMN_CLIENT_DISTRICT, district)
                put(COLUMN_CLIENT_PHONE, phone)
                put(COLUMN_CLIENT_EMAIL, email)
                put(COLUMN_CLIENT_TYPE, 0)
                put(COLUMN_CLIENT_PLAN, 1)
            })
        }

        for (client in clientsList) {
            db.insert(TABLE_CLIENTS, null, client)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ACTIVITIES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_MEMBERSHIPS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PAYMENTS_METHODS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CLIENTS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_MEMBERSHIPS_PAYMENTS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ACTIVITIES_PAYMENTS")

        onCreate(db)
    }
}