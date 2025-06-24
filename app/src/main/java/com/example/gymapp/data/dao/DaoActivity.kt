package com.example.gymapp.data.dao

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.example.gymapp.utils.ActionResult

import com.example.gymapp.data.DatabaseHelper.Companion.TABLE_ACTIVITIES
import com.example.gymapp.data.DatabaseHelper.Companion.COLUMN_ACTIVITY_ID
import com.example.gymapp.data.DatabaseHelper.Companion.COLUMN_ACTIVITY_NAME
import com.example.gymapp.data.DatabaseHelper.Companion.COLUMN_ACTIVITY_PRICE
import com.example.gymapp.data.dt.DtActivity


class DaoActivity(private val dbWrite: SQLiteDatabase, private val dbRead: SQLiteDatabase) {

    /**
     * Retrieves all activities registered in the database.
     *
     * @return A list of [DtActivity] objects representing all activities.
     * Returns an empty list if there are no activities or if an error occurs.
     */
    fun getAllActivities(): List<DtActivity> {
        val activities = mutableListOf<DtActivity>()
        val cursor = dbRead.query(
            TABLE_ACTIVITIES,
            arrayOf(COLUMN_ACTIVITY_ID, COLUMN_ACTIVITY_NAME, COLUMN_ACTIVITY_PRICE),
            null, null, null, null, null
        )

        cursor.use {
            while (it.moveToNext()) {
                val id = it.getInt(it.getColumnIndexOrThrow(COLUMN_ACTIVITY_ID))
                val name = it.getString(it.getColumnIndexOrThrow(COLUMN_ACTIVITY_NAME))
                val price = it.getInt(it.getColumnIndexOrThrow(COLUMN_ACTIVITY_PRICE))
                activities.add(DtActivity(id, name, price))
            }
        }
        return activities
    }

    /**
     * Registers a new activity in the database.
     *
     * @param activity The [DtActivity] object to register.
     * @return A [ActionResult] that indicates the result of the operation
     */
    fun registerActivity(activity: DtActivity): ActionResult {
         if (checkActivityNameExists(activity.name)) {
             return ActionResult.DATA_EXISTS
         }

        val values = ContentValues().apply {
            put(COLUMN_ACTIVITY_NAME, activity.name)
            put(COLUMN_ACTIVITY_PRICE, activity.price)
        }
        return try {
            val result = dbWrite.insert(TABLE_ACTIVITIES, null, values)
            if (result != -1L) ActionResult.SUCCESS else ActionResult.ERROR
        } catch (e: Exception) {
            e.printStackTrace()
            ActionResult.ERROR
        }
    }

    /**
     * Updates an existing activity in the database.
     *
     * @param activity The [DtActivity] object with the updated data.
     * @return A [ActionResult] that indicates the result of the operation
     */
    fun updateActivity(activity: DtActivity): ActionResult {
        if (activity.id == null) {
            ActionResult.NOT_FOUND
        }

        val values = ContentValues().apply {
            put(COLUMN_ACTIVITY_NAME, activity.name)
            put(COLUMN_ACTIVITY_PRICE, activity.price)
        }
        return try {
            val rowsAffected = dbWrite.update(
                TABLE_ACTIVITIES,
                values,
                "$COLUMN_ACTIVITY_ID = ?",
                arrayOf(activity.id.toString())
            )
            if (rowsAffected > 0) ActionResult.SUCCESS else ActionResult.ERROR
        } catch (e: Exception) {
            ActionResult.ERROR
        }
    }

    /**
     * Removes an activity from the database by its ID.
     *
     * @param id The ID of the activity to delete.
     * @return true if the activity was successfully deleted, false otherwise.
     */
    fun deleteActivity(id: Int): ActionResult {
        return try {
            val rowsAffected = dbWrite.delete(
                TABLE_ACTIVITIES,
                "$COLUMN_ACTIVITY_ID = ?",
                arrayOf(id.toString())
            )
            if (rowsAffected > 0) ActionResult.SUCCESS else ActionResult.ERROR
        } catch (e: Exception) {
            ActionResult.ERROR
        }
    }

    /**
     * Retrieves the ID and price of an activity by its name.
     *
     * @param name The name of the activity.
     * @return A Pair where first is price and second is ID if found, null otherwise.
     */
    fun getActivityPriceAndIdByName(name: String): Pair<Int, Int>? {
        val cursor = dbRead.query(
            TABLE_ACTIVITIES,
            arrayOf(COLUMN_ACTIVITY_ID, COLUMN_ACTIVITY_PRICE),
            "$COLUMN_ACTIVITY_NAME = ?",
            arrayOf(name),
            null, null, null
        )

        var activityInfo: Pair<Int, Int>? = null
        cursor.use {
            if (it.moveToFirst()) {
                val id = it.getInt(it.getColumnIndexOrThrow(COLUMN_ACTIVITY_ID))
                val price = it.getInt(it.getColumnIndexOrThrow(COLUMN_ACTIVITY_PRICE))
                activityInfo = Pair(price, id)
            }
        }
        return activityInfo
    }

    private fun checkActivityNameExists(name: String): Boolean {
        val cursor = dbRead.query(
            TABLE_ACTIVITIES,
            arrayOf(COLUMN_ACTIVITY_NAME),
            "$COLUMN_ACTIVITY_NAME = ?",
            arrayOf(name),
            null, null, null
        )
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }
}