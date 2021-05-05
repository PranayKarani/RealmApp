package com.realmapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        RealmDataSource.login(
            {
                Log.i("taaag", "auth success")
                RealmDataSource.startUserDataSync("123") // residents, orders
                RealmDataSource.startCatalogDataSync("ind") // meds, ins products, suppliers
            },
            { e ->
                Log.e("taaag", "auth error: ${e.localizedMessage}")
                e.printStackTrace()
            }
        )

    }

}