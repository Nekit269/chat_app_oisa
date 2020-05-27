package com.pusher.oisa

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar.title = getString(R.string.app_name)

        btnLogin.setOnClickListener {
            if (username.text.isNotEmpty() and password.text.isNotEmpty()) {
//                val user = username.text.toString()
//                val password = password.text.toString()
                val login = Login(
                        username.text.toString(),
                        password.text.toString()
                )
//                startActivity(Intent(this@MainActivity, ChatActivity::class.java))
//                Toast.makeText(applicationContext,txtMessage.text.toString(), Toast.LENGTH_SHORT).show()
                val call = ChatService.create().postLogin(login)

                call.enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
//                        val msg = response.toString()
//                        Log.e("Login", msg)
                        if(response.isSuccessful){
                            App.user = login.login
                            startActivity(Intent(this@MainActivity, ChatActivity::class.java))
                        } else {
                            if (response.code() == 404){
                                Toast.makeText(applicationContext,"Неправильное имя или пароль", Toast.LENGTH_SHORT).show()
                            } else {
                                Log.e("Login", response.code().toString());
                                Toast.makeText(applicationContext,"Ошибка обработки запроса", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Log.e("Login", t.toString());
                        Toast.makeText(applicationContext,"Ошибка доступа к серверу", Toast.LENGTH_SHORT).show()
                    }
                })
            } else if (username.text.isEmpty()){
                Toast.makeText(applicationContext,"Заполните имя пользователя", Toast.LENGTH_SHORT).show()
            } else if (password.text.isEmpty()){
                Toast.makeText(applicationContext,"Введите пароль", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(applicationContext,"Введите данные пользователя", Toast.LENGTH_SHORT).show()
            }
        }

        create_account_button.setOnClickListener {
            if (username.text.isNotEmpty() and password.text.isNotEmpty()) {
                val createAccount = CreateAccount(
                        username.text.toString(),
                        password.text.toString()
                )

//                Toast.makeText(applicationContext,username.text.toString(), Toast.LENGTH_SHORT).show()
                Log.e("Login", username.text.toString());
                Log.e("Login", password.text.toString());
                val call = ChatService.create().postCreateAccount(createAccount)

                call.enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
//                        val msg = response.toString()
//                        Log.e("Login", msg)
                        if(response.isSuccessful){
                            Toast.makeText(applicationContext,"Аккаунт создан. Теперь вы можете войти", Toast.LENGTH_LONG).show()
                        } else {
                            if (response.code() == 401) {
                                Toast.makeText(applicationContext, "Пользователь с таким именем уже есть", Toast.LENGTH_SHORT).show()
                            } else {
                                Log.e("Login", response.code().toString());
                                Toast.makeText(applicationContext, "Не удалось создать аккаунт", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Log.e("Login", t.toString());
                        Toast.makeText(applicationContext,"Ошибка доступа к серверу", Toast.LENGTH_SHORT).show()
                    }
                })
            } else if (username.text.isEmpty()){
                Toast.makeText(applicationContext,"Заполните имя пользователя", Toast.LENGTH_SHORT).show()
            } else if (password.text.isEmpty()){
                Toast.makeText(applicationContext,"Введите пароль", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(applicationContext,"Введите данные пользователя", Toast.LENGTH_SHORT).show()
            }
        }
    }
}