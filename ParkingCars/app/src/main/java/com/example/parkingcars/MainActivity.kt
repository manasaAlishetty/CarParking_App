package com.example.parkingcars

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        strt.setOnClickListener {
            startActivity(Intent(this, UserCarPlate::class.java))
        }

        strt.setOnClickListener{
            if (TextUtils.isEmpty(zipcode.text)) {
                zipcode.error = "Please Enter ZipCode"
                return@setOnClickListener
            }
            val pattern = Regex("^\\d{5}(?:[-\\s]\\d{4})?\$")
            if(pattern.containsMatchIn(zipcode.text)) {
                startActivity(Intent(this, UserCarPlate::class.java))
            } else {
                zipcode.error = " Invalid ZipCode"
                return@setOnClickListener
            }
            startActivity(Intent(this, UserCarPlate::class.java))
        }


    }
}



