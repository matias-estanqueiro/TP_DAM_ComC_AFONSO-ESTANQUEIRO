package com.example.gymapp.data.dao

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.example.gymapp.utils.ActionResult

import com.example.gymapp.data.DatabaseHelper.Companion.TABLE_CLIENTS
import com.example.gymapp.data.DatabaseHelper.Companion.COLUMN_CLIENT_DNI
import com.example.gymapp.data.DatabaseHelper.Companion.COLUMN_CLIENT_NAME
import com.example.gymapp.data.DatabaseHelper.Companion.COLUMN_CLIENT_SURNAME
import com.example.gymapp.data.DatabaseHelper.Companion.COLUMN_CLIENT_STREET
import com.example.gymapp.data.DatabaseHelper.Companion.COLUMN_CLIENT_NUMBER
import com.example.gymapp.data.DatabaseHelper.Companion.COLUMN_CLIENT_DISTRICT
import com.example.gymapp.data.DatabaseHelper.Companion.COLUMN_CLIENT_PHONE
import com.example.gymapp.data.DatabaseHelper.Companion.COLUMN_CLIENT_EMAIL
import com.example.gymapp.data.DatabaseHelper.Companion.COLUMN_CLIENT_ID
import com.example.gymapp.data.DatabaseHelper.Companion.COLUMN_CLIENT_TYPE
import com.example.gymapp.data.DatabaseHelper.Companion.COLUMN_CLIENT_PLAN
import com.example.gymapp.data.DatabaseHelper.Companion.COLUMN_MEMBERSHIP_ID
import com.example.gymapp.data.DatabaseHelper.Companion.COLUMN_MEMBERSHIP_NAME
import com.example.gymapp.data.DatabaseHelper.Companion.TABLE_MEMBERSHIPS
import com.example.gymapp.data.dt.DtClient


class DaoClient(private val dbWrite: SQLiteDatabase, private val dbRead: SQLiteDatabase) {

    /**
     * Register a new customer in the database, including uniqueness validations
     * for email and DNI.
     *
     * @param client The [DtClient] object containing the data of the client to register.
     * @return A [ActionResult] that indicates the result of the operation
     */
    fun registerClient(client: DtClient): ActionResult {
        if (client.email != null && checkClientEmail(client.email!!)) {
            return ActionResult.DATA_EXISTS
        }
        if (client.dni != null && checkClientDNI(client.dni!!)) {
            return ActionResult.DATA_EXISTS
        }

        val values = ContentValues().apply {
            put(COLUMN_CLIENT_DNI, client.dni)
            put(COLUMN_CLIENT_NAME, client.name)
            put(COLUMN_CLIENT_SURNAME, client.surname)
            put(COLUMN_CLIENT_STREET, client.street)
            put(COLUMN_CLIENT_NUMBER, client.streetNumber)
            put(COLUMN_CLIENT_DISTRICT, client.district)
            put(COLUMN_CLIENT_PHONE, client.phone)
            put(COLUMN_CLIENT_EMAIL, client.email)
            put(COLUMN_CLIENT_TYPE, client.type)
            put(COLUMN_CLIENT_PLAN, client.plan)
        }

        return try {
            val result = dbWrite.insert(TABLE_CLIENTS, null, values)
            if (result != -1L) ActionResult.SUCCESS else ActionResult.ERROR
        } catch (e: Exception) {
            e.printStackTrace()
            ActionResult.ERROR
        }
    }

    /**
     * Retrieves a client's details if they are a member (type = 1), based on their DNI.
     * Designed to get data for a "member ID card" view.
     *
     * @param dni The DNI of the client to look up.
     * @return A [DtClient] object with its data if the client is found and is a member (type 1),
     * otherwise returns null.
     */
    fun getMemberByDni(dni: String): DtClient? {
        if (!checkClientIsMember(dni)) {
            return null
        }

        val query = """
            SELECT
                c.$COLUMN_CLIENT_ID,
                c.$COLUMN_CLIENT_DNI,
                c.$COLUMN_CLIENT_NAME AS client_name_alias,
                c.$COLUMN_CLIENT_SURNAME,
                c.$COLUMN_CLIENT_TYPE,
                c.$COLUMN_CLIENT_PLAN,
                m.$COLUMN_MEMBERSHIP_NAME AS plan_name_alias
            FROM
                $TABLE_CLIENTS AS c
            LEFT JOIN
                $TABLE_MEMBERSHIPS AS m ON c.$COLUMN_CLIENT_PLAN = m.$COLUMN_MEMBERSHIP_ID
            WHERE
                c.$COLUMN_CLIENT_DNI = ? AND c.$COLUMN_CLIENT_TYPE = 1
        """.trimIndent()

        val cursor = dbRead.rawQuery(query, arrayOf(dni))

        var memberClient: DtClient? = null
        cursor.use {
            if (it.moveToFirst()) {
                val id = it.getInt(it.getColumnIndexOrThrow(COLUMN_CLIENT_ID))
                val foundDni = it.getString(it.getColumnIndexOrThrow(COLUMN_CLIENT_DNI))
                val name = it.getString(it.getColumnIndexOrThrow("client_name_alias"))
                val surname = it.getString(it.getColumnIndexOrThrow(COLUMN_CLIENT_SURNAME))
                val membershipId = if (!it.isNull(it.getColumnIndexOrThrow(COLUMN_CLIENT_PLAN))) {
                    it.getInt(it.getColumnIndexOrThrow(COLUMN_CLIENT_PLAN))
                } else {
                    0
                }
                val planName = if (!it.isNull(it.getColumnIndexOrThrow("plan_name_alias"))) {
                    it.getString(it.getColumnIndexOrThrow("plan_name_alias"))
                } else {
                    null
                }
                memberClient = DtClient(
                    id = id,
                    dni = foundDni,
                    name = name,
                    surname = surname,
                    plan = membershipId,
                    planName = planName
                )
            }
        }
        return memberClient
    }

    /**
     * Retrieves a client's full details if they are a regular client (type = 0), based on their DNI.
     * This function is useful for validating clients who are not members.
     *
     * @param dni The DNI of the client to look up.
     * @return A [DtClient] object with its data if the client is found and is a regular client (type 0),
     * otherwise returns null.
     */
    fun getClientByDni(dni: String): DtClient? {
        if (checkClientIsMember(dni)) {
            return null
        }

        val query = """
            SELECT
                $COLUMN_CLIENT_ID,
                $COLUMN_CLIENT_DNI,
                $COLUMN_CLIENT_NAME,
                $COLUMN_CLIENT_SURNAME
            FROM
                $TABLE_CLIENTS
            WHERE
                $COLUMN_CLIENT_DNI = ?
        """.trimIndent()

        val cursor = dbRead.rawQuery(query, arrayOf(dni))

        var regularClient: DtClient? = null
        cursor.use {
            if (it.moveToFirst()) {
                val id = it.getInt(it.getColumnIndexOrThrow(COLUMN_CLIENT_ID))
                val foundDni = it.getString(it.getColumnIndexOrThrow(COLUMN_CLIENT_DNI))
                val name = it.getString(it.getColumnIndexOrThrow(COLUMN_CLIENT_NAME))
                val surname = it.getString(it.getColumnIndexOrThrow(COLUMN_CLIENT_SURNAME))
                regularClient = DtClient(
                    id = id,
                    dni = foundDni,
                    name = name,
                    surname = surname)
            }
        }
        return regularClient
    }

    /**
     * Retrieves all clients from the database that are classified as members (type = 1).
     *
     * @return A list of [DtClient] objects representing the members.
     */
    fun getAllMembers(): List<DtClient> {
            val membersList = mutableListOf<DtClient>()
            val query = """
            SELECT
                c.$COLUMN_CLIENT_ID,
                c.$COLUMN_CLIENT_DNI,
                c.$COLUMN_CLIENT_NAME AS client_name_alias,
                c.$COLUMN_CLIENT_SURNAME,
                c.$COLUMN_CLIENT_TYPE,
                c.$COLUMN_CLIENT_PLAN,
                m.$COLUMN_MEMBERSHIP_NAME AS plan_name_alias
            FROM
                $TABLE_CLIENTS AS c
            LEFT JOIN
                $TABLE_MEMBERSHIPS AS m ON c.$COLUMN_CLIENT_PLAN = m.$COLUMN_MEMBERSHIP_ID
            WHERE
               c.$COLUMN_CLIENT_TYPE = 1
        """.trimIndent()

        val cursor = dbRead.rawQuery(query, null)

        cursor.use {
            while (it.moveToNext()) {
                val id = it.getInt(it.getColumnIndexOrThrow(COLUMN_CLIENT_ID))
                val dni = it.getString(it.getColumnIndexOrThrow(COLUMN_CLIENT_DNI))
                val name = it.getString(it.getColumnIndexOrThrow("client_name_alias"))
                val surname = it.getString(it.getColumnIndexOrThrow(COLUMN_CLIENT_SURNAME))
                val planName = if (!it.isNull(it.getColumnIndexOrThrow("plan_name_alias"))) {
                    it.getString(it.getColumnIndexOrThrow("plan_name_alias"))
                } else {
                    null
                }

                membersList.add(
                    DtClient(
                        id = id,
                        dni = dni,
                        name = name,
                        surname = surname,
                        planName = planName
                    )
                )
            }
        }
        return membersList
    }

    /**
     * Verify if a DNI is already registered in the database.
     * @param dni The DNI to verify.
     * @return true if the DNI already exists, false otherwise.
     */
    fun checkClientDNI(dni: String): Boolean {
        val cursor = dbRead.query(
            TABLE_CLIENTS,
            arrayOf(COLUMN_CLIENT_DNI),
            "$COLUMN_CLIENT_DNI = ?",
            arrayOf(dni),
            null, null, null
        )
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    /**
     * Verify if a client with the given email is already registered in the database.
     * @param email The email to verify.
     * @return true if the email already exists, false otherwise.
     */
    private fun checkClientEmail(email: String): Boolean {
        val cursor = dbRead.query(
            TABLE_CLIENTS,
            arrayOf(COLUMN_CLIENT_EMAIL),
            "$COLUMN_CLIENT_EMAIL = ?",
            arrayOf(email),
            null, null, null
        )
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    /**
     * Checks if a client with the given DNI is a member (type = 1).
     *
     * @param dni The DNI of the client to check.
     * @return true if the client is found and their type is 1, false otherwise.
     */
    private fun checkClientIsMember(dni: String) : Boolean {
        val cursor = dbRead.query(
            TABLE_CLIENTS,
            arrayOf(COLUMN_CLIENT_TYPE),
            "$COLUMN_CLIENT_DNI = ?",
            arrayOf(dni),
            null, null, null
        )

        cursor.use {
            if (it.moveToFirst()) {
                val clientType = it.getInt(it.getColumnIndexOrThrow(COLUMN_CLIENT_TYPE))
                return clientType == 1
            }
        }
        return false
    }
}