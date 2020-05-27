package com.pusher.oisa

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import kotlinx.android.synthetic.main.activity_chat.*
import java.util.*


class DatabaseHandler(context: Context, tableName: String) :
        SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    private val TABLE_NAME = tableName

    override fun onCreate(db: SQLiteDatabase?) {

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Called when the database needs to be upgraded
    }

    fun setUser() {
        val db = this.writableDatabase
        val CREATE_TABLE = "CREATE TABLE IF NOT EXISTS $TABLE_NAME " +
                "($ID Integer PRIMARY KEY, $USER TEXT, $MESSAGE TEXT, $TIME Integer)"
        val _success = db?.execSQL(CREATE_TABLE)
        Log.v("Create DB", _success.toString())
    }

    fun addMessage(message: Message): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(USER, message.user)
        values.put(MESSAGE, message.message)
        values.put(TIME, message.time)
        val _success = db.insert(TABLE_NAME, null, values)
        db.close()
        Log.v("InsertedID", "$_success")
        return (Integer.parseInt("$_success") != -1)
    }

    fun addMessages(users: List<String>, texts : List<String>, times : List<Long>){
        val db = this.writableDatabase
        val count = users.count()
        for(i in 0 until count){
            val message = Message(
                    users[i],
                    texts[i],
                    times[i]
            )
            addMessage(message)
        }
    }

    fun clear(){
        val db = this.writableDatabase
        val DROP_TABLE = "DROP TABLE IF EXISTS $TABLE_NAME"
        val _success = db?.execSQL(DROP_TABLE)
        Log.v("Clear DB", _success.toString())
    }

    fun loadMessagesIntoAdapter(adapter: MessageAdapter){
        val db = readableDatabase
        val selectALLQuery = "SELECT * FROM $TABLE_NAME"
        val cursor = db.rawQuery(selectALLQuery, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    var user = cursor.getString(cursor.getColumnIndex(USER))
                    var text = cursor.getString(cursor.getColumnIndex(MESSAGE))
                    var time = cursor.getString(cursor.getColumnIndex(TIME)).toLong()

                    val message = Message(
                            user,
                            text,
                            time
                    )

                    adapter.addMessage(message)
                } while (cursor.moveToNext())
            }
        }
        cursor.close()
        db.close()
    }

    fun getMessages(users: MutableList<String>, texts : MutableList<String>, times : MutableList<Long>){
        val tex : List<String>
        val db = readableDatabase
        val selectALLQuery = "SELECT * FROM $TABLE_NAME"
        val cursor = db.rawQuery(selectALLQuery, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    var user = cursor.getString(cursor.getColumnIndex(USER))
                    var text = cursor.getString(cursor.getColumnIndex(MESSAGE))
                    var time = cursor.getString(cursor.getColumnIndex(TIME)).toLong()

                    users.add(user)
                    texts.add(text)
                    times.add(time)
                } while (cursor.moveToNext())
            }
        }
        cursor.close()
        db.close()
    }

    companion object {
        private const val DB_NAME = "AppDB"
        private const val DB_VERSION = 1;
        private const val ID = "id"
        private const val USER = "User"
        private const val MESSAGE = "Message"
        private const val TIME = "Time"
    }
}

