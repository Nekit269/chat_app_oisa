package com.pusher.oisa

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.activity_settings.toolbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class Settings : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        if(App.whiteTheme){
            setTheme(R.style.AppTheme_NoActionBar)
        }else{
            setTheme(R.style.AppBlack)
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val actBar = toolbar
        setSupportActionBar(actBar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        supportActionBar!!.setTitle(R.string.settings)

        switch1.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                // The switch is enabled/checked
                App.whiteTheme = false
                finish()
                startActivity(Intent(this, Settings::class.java))
            } else {
                // The switch is disabled
                App.whiteTheme = true
                finish()
                startActivity(Intent(this, Settings::class.java))
            }
        }

        val sizes = arrayOf("10","12","14","16","18","20")

        // Initializing an ArrayAdapter
        val adapter = ArrayAdapter(
                this, // Context
                android.R.layout.simple_spinner_item, // Layout
                sizes // Array
        )

        // Set the drop down view resource
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)

        // Finally, data bind the spinner object with adapter
        spinner.adapter = adapter;

        // Set an on item selected listener for spinner object
        spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent:AdapterView<*>, view: View, position: Int, id: Long){
                // Display the selected item text on text view
                App.fontSize = parent.getItemAtPosition(position).toString().toInt()
                Log.d("Set", App.fontSize.toString())
                currentFontSizeText.text = App.fontSize.toString()

                App.adapter.changeFontSize()
                App.adapter.clear()
                App.dbHandler.loadMessagesIntoAdapter(App.adapter)
            }

            override fun onNothingSelected(parent: AdapterView<*>){
                currentFontSizeText.text = App.fontSize.toString()
            }
        }
        Log.d("Init", App.fontSize.toString())
        currentFontSizeText.text = App.fontSize.toString()

        button_save.setOnClickListener {
            val users = mutableListOf<String>()
            val texts = mutableListOf<String>()
            val times = mutableListOf<Long>()

            App.dbHandler.getMessages(users, texts, times)
            Log.v("Save", App.user.toString())
            val save = SaveHistory(
                    App.user.toString(),
                    users.toList(),
                    texts.toList(),
                    times.toList()
            )
            val call = ChatService.create().postSendHistory(save)

            call.enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
//                        val msg = response.toString()
//                        Log.e("Login", msg)
                    if(response.isSuccessful){
                        Toast.makeText(applicationContext,"История сохранена", Toast.LENGTH_SHORT).show()
                    } else {
                        if (response.code() == 404){
                            Toast.makeText(applicationContext,"Неправильное имя или пароль", Toast.LENGTH_SHORT).show()
                        } else {
                            Log.e("Save_history", response.code().toString());
                            Toast.makeText(applicationContext,"Ошибка обработки запроса", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.e("Save_hitory", t.toString());
                    Toast.makeText(applicationContext,"Ошибка доступа к серверу", Toast.LENGTH_SHORT).show()
                }
            })
        }

        button_load.setOnClickListener {
            val users = mutableListOf<String>()
            val texts = mutableListOf<String>()
            val times = mutableListOf<Long>()

            App.dbHandler.getMessages(users, texts, times)
            Log.v("Load", App.user.toString())
            val save = LoadHistory(
                    App.user.toString()
            )

            val call = ChatService.create().postLoadHistory(save)

            call.enqueue(object : Callback<History> {
                override fun onResponse(call: Call<History>, response: Response<History>) {
//                        val msg = response.toString()
//                        Log.e("Login", msg)
                    if(response.isSuccessful){
                        val data = response.body()
                        if(data != null){
                            App.dbHandler.clear()
                            App.dbHandler.setUser()
                            App.dbHandler.addMessages(data.users, data.messages, data.times)
                            App.adapter.clear()
                            App.adapter.addMessages(data.users, data.messages, data.times)
                            Toast.makeText(applicationContext,"История загружена", Toast.LENGTH_SHORT).show()
                        }else{
                            Toast.makeText(applicationContext,"История повреждена", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        if (response.code() == 401){
                            Toast.makeText(applicationContext,"Не найдено сохранение", Toast.LENGTH_SHORT).show()
                        } else if (response.code() == 404){
                            Toast.makeText(applicationContext,"Неправильное имя или пароль", Toast.LENGTH_SHORT).show()
                        } else {
                            Log.e("Save_history", response.code().toString());
                            Toast.makeText(applicationContext,"Ошибка обработки запроса", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onFailure(call: Call<History>, t: Throwable) {
                    Log.e("Save_history", t.toString());
                    Toast.makeText(applicationContext,"Ошибка доступа к серверу", Toast.LENGTH_SHORT).show()
                }
            })
        }

        button_del.setOnClickListener {
            App.adapter.clear()
            App.dbHandler.clear()
            Toast.makeText(applicationContext,"", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item != null) {
            when(item.itemId){
                android.R.id.home -> {
//                    startActivity(Intent(this@Settings, ChatActivity::class.java))
                    finish()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }
}
