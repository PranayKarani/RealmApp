package com.realmapp

import android.util.Log
import io.realm.Realm
import io.realm.log.LogLevel
import io.realm.log.RealmLog
import io.realm.mongodb.App
import io.realm.mongodb.AppConfiguration
import io.realm.mongodb.Credentials
import io.realm.mongodb.sync.ProgressMode
import io.realm.mongodb.sync.SyncConfiguration
import io.realm.mongodb.sync.SyncSession
import java.security.SecureRandom

object RealmDataSource {


    private val appID = "juicyapp-dekji"
    private val realmEmail = "superman@juicy.com"
    private val realmPass = "123456"

    private lateinit var realmApp: App
    private lateinit var realmUserData: Realm
    private lateinit var realmCatalogData: Realm
    private var userDataSyncConfig: SyncConfiguration? = null // sync_tag = 123
    private var catalogDataSyncConfig: SyncConfiguration? = null // sync_tag = ind
    var key = ByteArray(64)

    fun login(onSuccess: () -> Unit, onError: (err: Throwable) -> Unit) {

        realmApp = App(AppConfiguration.Builder(appID).build())
        val creds = Credentials.emailPassword(realmEmail, realmPass)
        realmApp.loginAsync(creds) {

            if (it.isSuccess) {
                onSuccess()
                RealmLog.setLevel(LogLevel.ALL)
            } else {
                onError(it.error)
            }

        }

    }

    fun startUserDataSync(userId: String) {


        val handler = SyncSession.ClientResetHandler { session, error ->
            Log.e("taaag", "Client Reset required for: ${session.configuration.serverUrl} for error: $error")
        }

        SecureRandom().apply {
            this.nextBytes(key)
        }

        if (userDataSyncConfig == null) {
            userDataSyncConfig = SyncConfiguration.Builder(realmApp.currentUser(), userId) // sync_tag
                .allowQueriesOnUiThread(false)
                .allowWritesOnUiThread(false)
                .clientResetHandler(handler)
                .waitForInitialRemoteData()
                .errorHandler { session, error ->
                    Log.e("taaag", "Session: ${session.state}, Error: ${error.errorMessage}")
                    error.printStackTrace()
                }
                .build()

            Realm.getInstanceAsync(userDataSyncConfig!!, object : Realm.Callback() {
                override fun onSuccess(realm: Realm) {

                    this@RealmDataSource.realmUserData = realm

                    Log.i("taaag", "user data sync opened")
                    val session = realmApp.sync.getSession(userDataSyncConfig)
                    session.addDownloadProgressListener(ProgressMode.INDEFINITELY) {
                        Log.d("taaag", "user data downloading: ${it.transferredBytes}")
                    }
                    session.start()
                    Log.i("taaag", "user data sync started: ${session.isConnected}")
                }
            })
        }


    }

    fun startCatalogDataSync(country: String) {

        val handler = SyncSession.ClientResetHandler { session, error ->
            Log.e("taaag", "Client Reset required for: ${session.configuration.serverUrl} for error: $error")
        }

        if (catalogDataSyncConfig == null) {
            catalogDataSyncConfig = SyncConfiguration.Builder(realmApp.currentUser(), country)
                .allowQueriesOnUiThread(false)
                .allowWritesOnUiThread(false)
                .clientResetHandler(handler)
                .waitForInitialRemoteData()
                .errorHandler { session, error ->
                    Log.e("taaag", "Session: ${session.state}, Error: ${error.errorMessage}")
                    error.printStackTrace()
                }
                .build()

            Realm.getInstanceAsync(catalogDataSyncConfig!!, object : Realm.Callback() {
                override fun onSuccess(realm: Realm) {

                    this@RealmDataSource.realmCatalogData = realm
                    Log.i("taaag", "catalog sync opened")
                    val session = realmApp.sync.getSession(catalogDataSyncConfig)
                    session.addDownloadProgressListener(ProgressMode.INDEFINITELY) {
                        Log.d("taaag", "catalog downloading: ${it.transferredBytes}")
                    }
                    session.start()
                    Log.i("taaag", "catalog sync started: ${session.isConnected}")
                }
            })
        }


    }

}