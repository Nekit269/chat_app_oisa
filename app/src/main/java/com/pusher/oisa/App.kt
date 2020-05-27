package com.pusher.oisa

import android.app.Application

class App:Application() {
    companion object {
        lateinit var user:String
        var whiteTheme = true
        var fontSize = 14

        lateinit var adapter: MessageAdapter
        lateinit var dbHandler: DatabaseHandler
    }
}