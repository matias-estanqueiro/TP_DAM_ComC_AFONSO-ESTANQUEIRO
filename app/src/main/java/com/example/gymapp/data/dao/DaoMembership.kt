package com.example.gymapp.data.dao

import android.database.sqlite.SQLiteDatabase

import com.example.gymapp.data.DatabaseHelper.Companion.TABLE_MEMBERSHIPS
import com.example.gymapp.data.DatabaseHelper.Companion.COLUMN_MEMBERSHIP_ID
import com.example.gymapp.data.DatabaseHelper.Companion.COLUMN_MEMBERSHIP_NAME
import com.example.gymapp.data.DatabaseHelper.Companion.COLUMN_MEMBERSHIP_PRICE
import com.example.gymapp.data.dt.DtMembership

class DaoMembership(private val dbWrite: SQLiteDatabase, private val dbRead: SQLiteDatabase) {

    /**
     * Retrieves all memberships registered in the database.
     *
     * @return A list of [DtMembership] objects representing all memberships.
     * Returns an empty list if there are no memberships or if an error occurs.
     */
    fun getAllMemberships(): List<DtMembership> {
        val memberships = mutableListOf<DtMembership>()
        val cursor = dbRead.query(
            TABLE_MEMBERSHIPS,
            arrayOf(COLUMN_MEMBERSHIP_ID, COLUMN_MEMBERSHIP_NAME, COLUMN_MEMBERSHIP_PRICE),
            null, null, null, null, null,
        )

        cursor.use {
            while (it.moveToNext()) {
                val id = it.getInt(it.getColumnIndexOrThrow(COLUMN_MEMBERSHIP_ID))
                val name = it.getString(it.getColumnIndexOrThrow(COLUMN_MEMBERSHIP_NAME))
                val price = it.getInt(it.getColumnIndexOrThrow(COLUMN_MEMBERSHIP_PRICE))
                memberships.add(DtMembership(id, name, price))
            }
        }
        return memberships
    }

    /**
     * Retrieves a specific membership from the database by its ID.
     *
     * @param id The ID of the membership to retrieve.
     * @return A [DtMembership] object if found, or `null` otherwise.
     */
    fun getMembershipById(id: Int): DtMembership? {
        val cursor = dbRead.query(
            TABLE_MEMBERSHIPS,
            arrayOf(COLUMN_MEMBERSHIP_ID, COLUMN_MEMBERSHIP_NAME, COLUMN_MEMBERSHIP_PRICE),
            "$COLUMN_MEMBERSHIP_ID = ?",
            arrayOf(id.toString()),
            null, null, null
        )

        var membership: DtMembership? = null
        cursor.use {
            if (it.moveToFirst()) {
                val foundId = it.getInt(it.getColumnIndexOrThrow(COLUMN_MEMBERSHIP_ID))
                val name = it.getString(it.getColumnIndexOrThrow(COLUMN_MEMBERSHIP_NAME))
                val price = it.getInt(it.getColumnIndexOrThrow(COLUMN_MEMBERSHIP_PRICE))
                membership = DtMembership(foundId, name, price)
            }
        }
        return membership
    }
}