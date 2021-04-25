package com.example.parkingcars

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_user_car_plate.*
import java.time.Duration
import java.time.temporal.ChronoUnit
import java.util.*


class UserCarPlate : AppCompatActivity() {
    var entry: DateTime = DateTime(0, 0, 0, 0, 0)
    var exit: DateTime = DateTime(0, 0, 0, 0, 0)
    var totalHours = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_car_plate)
        cancel.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
        }
        next.setOnClickListener {

            if (TextUtils.isEmpty(carData.text)) {
                carData.error = "Please enter the Car Licence Plate Number"
                return@setOnClickListener
            }

            if (entryInfo.text.startsWith("Entry")) {
                val error = "Please select ${entryInfo.text}"
                Toast.makeText(this, error, Toast.LENGTH_LONG).show()

                return@setOnClickListener
            }
            if (exitInfo.text.startsWith("Exit")) {
                val error = "Please select ${exitInfo.text}"

                Toast.makeText(this, error, Toast.LENGTH_LONG).show()

                return@setOnClickListener
            }


            val alertDialog1 = AlertDialog.Builder(
                this
            )
            var hours = 0L
            val c=Calendar.getInstance()
            c.timeInMillis = getTimeStamp(exit) - getTimeStamp(entry)
            val hour = c.timeInMillis / (1000 * 60 * 60)
            val mins = ((c.timeInMillis  / (1000 * 60)) % 60)
            hours = hour
            hours =   if (mins>0){
                hours+1
            }else
                hour





            alertDialog1.setTitle("Charges Info")
            alertDialog1.setMessage("Total Time: $hour hour $mins minutes\nTotal Charge: $${hours*5}")

            alertDialog1.setIcon(R.drawable.ic_baseline_payment_24)
            alertDialog1.setNegativeButton("CLOSE") { _, _ ->
                alertDialog1.create().cancel()
            }
            alertDialog1.setPositiveButton("PAY") { _, _ ->
                startActivity(Intent(this, BillingData::class.java))
            }

            alertDialog1.create().show()

        }

        entryInfo.setOnClickListener {
            entry.view = it
            getDateTime(entry)
        }

        exitInfo.setOnClickListener {
            exit.view = it
            getDateTime(exit)
        }
    }

    fun showClock(info: DateTime) {
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)
        val timePickerDialog = TimePickerDialog(
            this,
            R.style.Pickers,
            { view, hourOfDay, minute ->


                info.hourOfDay = hourOfDay
                info.minute = minute

                if (getTimeStamp(entry)>getTimeStamp(exit) && getTimeStamp(exit)>0) {
                    Toast.makeText(this, "Please enter valid exit time", Toast.LENGTH_LONG).show()
                    info.hourOfDay = 0
                    info.minute = 0
                    showClock(info)
                    return@TimePickerDialog
                }
                val view = info.view as TextView
                view.text =
                    "${info.month}/${info.day}/${info.year}, ${info.hourOfDay}:${info.minute}"
            },
            hour,
            minute,
            false
        )

        timePickerDialog.show()
    }

    fun showDate(info: DateTime) {

        val c = Calendar.getInstance()
        val mYear = c[Calendar.YEAR]
        val mMonth = c[Calendar.MONTH]
        val mDay = c[Calendar.DAY_OF_MONTH]


        val datePickerDialog = DatePickerDialog(
            this,
            R.style.Pickers,
            { view, year, month, day ->
                info.day = day
                info.month = month
                info.year = year
                showClock(info)
            },
            mYear,
            mMonth,
            mDay
        )

        datePickerDialog.datePicker.minDate = if (entry.month > 0)
            getTimeStamp(entry)
        else
            c.timeInMillis
        datePickerDialog.show()
    }

    fun getDateTime(info: DateTime) {

        showDate(info)
    }

    fun getTimeStamp(info: DateTime): Long {
        val c = Calendar.getInstance()
        c.set(Calendar.YEAR, info.year)
        c.set(Calendar.DAY_OF_MONTH, info.day)
        c.set(Calendar.MONTH, info.month)
        c.set(Calendar.HOUR_OF_DAY, info.hourOfDay)
        c.set(Calendar.MINUTE, info.minute)
        return c.timeInMillis
    }


    fun getPaymentTime(): Map<String, Int> {
       var hours = 0
        val c=Calendar.getInstance()
        c.timeInMillis = getTimeStamp(exit) - getTimeStamp(entry)
        hours =   if (c.get(Calendar.MINUTE)>0){
            c.get(Calendar.HOUR)+1
        }else
            c.get(Calendar.HOUR)
        return mapOf("${c.get(Calendar.HOUR)}: ${c.get(Calendar.MINUTE)}" to hours*5)
    }
    data class DateTime(
        var month: Int,
        var day: Int,
        var year: Int,
        var hourOfDay: Int,
        var minute: Int,
        var view: View? = null
    )

}