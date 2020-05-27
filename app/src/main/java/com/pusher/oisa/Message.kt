package com.pusher.oisa

data class Login(var login:String,
                 var password:String)

data class CreateAccount(var login:String,
                         var password:String)

data class Message(var user:String,
                   var message:String,
                   var time:Long)

data class SaveHistory(var user:String,
                       var users:List<String>,
                       var messages:List<String>,
                       var times:List<Long>)

data class LoadHistory(var user:String)

data class History(var users:List<String>,
                   var messages:List<String>,
                   var times:List<Long>)