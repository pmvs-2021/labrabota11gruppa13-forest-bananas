package com.zlatamigas.testbottomnavigation

import android.content.ContentValues
import android.database.sqlite.SQLiteOpenHelper
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class DBController(val helper: SQLiteOpenHelper)
{
    private val FAVOURITES_TABLE_NAME = "favourites"
    private val REMINDERS_TABLE_NAME = "reminders"
    private val COLUMN_ID = "id"
    private val COLUMN_ROW_ID = "row_id"
    private val COLUMN_NAME = "name"
    private val COLUMN_ADD_DATE = "add_date"
    private val COLUMN_NOTIFY_DATE = "notify_date"

    fun addReminder(id: Int, name: String, date: Date): Boolean {
        val cv = ContentValues()
        cv.put(COLUMN_ID, id)
        cv.put(COLUMN_NAME, name)
        cv.put(COLUMN_NOTIFY_DATE, SimpleDateFormat().format(date))

        val db = helper.writableDatabase
        val result = db.insert(REMINDERS_TABLE_NAME, null, cv)
        return result != -1L
    }

    fun addFavourite(id: Int, name: String): Boolean {
        val cv = ContentValues()
        cv.put(COLUMN_ID, id)
        cv.put(COLUMN_NAME, name)
        cv.put(COLUMN_ADD_DATE, SimpleDateFormat().format(Date()))

        val db = helper.writableDatabase
        val result = db.insert(FAVOURITES_TABLE_NAME, null, cv)
        return result != -1L
    }

    fun isFavourite(id: Int): Boolean {
        val db = helper.readableDatabase
        val c = db.query(FAVOURITES_TABLE_NAME,
            null, "id=$id", null, null, null, null)
        var result = c.moveToFirst()
        c.close()
        return result
    }

    fun getReminders(): List<Reminder> {
        val db = helper.readableDatabase
        val c = db.query(REMINDERS_TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            null)

        val result = ArrayList<Reminder>()

        if (c.moveToFirst()) {
            val rowIdColIndex = c.getColumnIndex(COLUMN_ROW_ID)
            val idColIndex = c.getColumnIndex(COLUMN_ID)
            val nameColIndex = c.getColumnIndex(COLUMN_NAME)
            val dateColIndex = c.getColumnIndex(COLUMN_NOTIFY_DATE)
            do {
                result.add(
                    Reminder(
                        c.getInt(rowIdColIndex),
                        c.getInt(idColIndex),
                        c.getString(nameColIndex),
                        SimpleDateFormat().parse(c.getString(dateColIndex))
                    )
                )
            } while (c.moveToNext())
        }

        c.close()
        return result
    }

    fun getFavourites(): List<Favourite> {
        val db = helper.readableDatabase
        val c = db.query(FAVOURITES_TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            null)

        val result = ArrayList<Favourite>()

        if (c.moveToFirst()) {
            val idColIndex = c.getColumnIndex(COLUMN_ID)
            val nameColIndex = c.getColumnIndex(COLUMN_NAME)
            val dateColIndex = c.getColumnIndex(COLUMN_ADD_DATE)
            do {
                result.add(
                    Favourite(
                        c.getInt(idColIndex),
                        c.getString(nameColIndex),
                        SimpleDateFormat().parse(c.getString(dateColIndex))
                    )
                )
            } while (c.moveToNext())
        }

        c.close()
        return result
    }

    fun deleteReminder(id: Int): Int {
        val db = helper.readableDatabase
        return db.delete(REMINDERS_TABLE_NAME, "$COLUMN_ROW_ID = $id", null)
    }

    fun deleteFavourite(id: Int): Int {
        val db = helper.readableDatabase
        return db.delete(FAVOURITES_TABLE_NAME, "$COLUMN_ROW_ID = $id", null)
    }

    fun updateReminderDate(rowId: Int, newDate: Date): Int {
        val cv = ContentValues()
        cv.put(COLUMN_NOTIFY_DATE, SimpleDateFormat().format(newDate))

        val db = helper.writableDatabase
        return db.update(REMINDERS_TABLE_NAME, cv, "$COLUMN_ROW_ID = $rowId", null)
    }
}