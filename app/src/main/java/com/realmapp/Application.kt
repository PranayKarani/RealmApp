package com.realmapp

import android.app.Application
import android.util.Log
import io.realm.Realm
import io.realm.mongodb.App
import io.realm.mongodb.AppConfiguration
import io.realm.mongodb.Credentials
import io.realm.mongodb.User
import io.realm.mongodb.sync.SyncConfiguration
import java.lang.Exception

class Application : Application() {

    companion object {
        lateinit var app: com.realmapp.Application
    }


    override fun onCreate() {
        super.onCreate()
        app = this

        Realm.init(this)


    }

}