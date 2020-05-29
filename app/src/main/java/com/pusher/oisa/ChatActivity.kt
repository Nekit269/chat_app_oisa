package com.pusher.oisa

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_chat.*
import java.util.*
import kotlinx.android.synthetic.main.activity_chat.toolbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

private const val TAG = "ChatActivity"

class ChatActivity: AppCompatActivity() {
    private var isWhiteTheme = App.whiteTheme;

    override fun onCreate(savedInstanceState: Bundle?) {
        if(App.whiteTheme){
            setTheme(R.style.AppTheme_NoActionBar)
        }else{
            setTheme(R.style.AppBlack)
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        setSupportActionBar(toolbar)

        val actionBar = supportActionBar

        actionBar!!.title = getString(R.string.app_name)
        actionBar.setDisplayShowHomeEnabled(true)

        messageList.layoutManager = LinearLayoutManager(this)

        App.adapter = MessageAdapter(this)

        App.dbHandler.setUser()
        App.dbHandler.loadMessagesIntoAdapter(App.adapter)

        btnSend.setOnClickListener {
            if(txtMessage.text.isNotEmpty()) {
                App.adapter.changeFontSize()

                val message = Message(
                        App.user,
                        txtMessage.text.toString(),
                        Calendar.getInstance().timeInMillis
                )
                App.adapter.addMessage(message)
                messageList.scrollToPosition(App.adapter.itemCount - 1);
                App.dbHandler!!.addMessage(message)

                resetInput()

                val call = ChatService.create().postMessage(message)

                call.enqueue(object : Callback<String> {
                    override fun onResponse(call: Call<String>, response: Response<String>) {
                        val answer = response.body()
                        if (response.isSuccessful) {
                            val other_message = Message(
                                    "Ойся",
                                    answer!!,
                                    Calendar.getInstance().timeInMillis
                            )
                            App.adapter.addMessage(other_message)
                            messageList.scrollToPosition(App.adapter.itemCount - 1);

                            App.dbHandler.addMessage(other_message)
                        }else{
                            Log.e(TAG, response.code().toString());
                            Toast.makeText(applicationContext,"Ответ был неудачным", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<String>, t: Throwable) {
                        resetInput()
                        Log.e(TAG, t.toString());
                        Toast.makeText(applicationContext,"Ошибка доступа к серверу", Toast.LENGTH_SHORT).show()
                    }
                })
            } else {
                Toast.makeText(applicationContext,"Сообщение не должно быть пустым", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun resetInput() {
        // Clean text box
        txtMessage.text.clear()

        // Hide keyboard
//        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//        inputManager.hideSoftInputFromWindow(currentFocus!!.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item != null) {
            when(item.itemId){
                R.id.settings -> {
                    startActivity(Intent(this@ChatActivity, Settings::class.java))
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        if (isWhiteTheme != App.whiteTheme){
            finish()
            startActivity(Intent(this, ChatActivity::class.java))
        }
        messageList.adapter = App.adapter
        messageList.scrollToPosition(App.adapter.itemCount - 1);
        super.onResume()
    }

}