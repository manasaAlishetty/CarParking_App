package com.example.parkingcars


import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_billing_data.*
import kotlinx.android.synthetic.main.activity_user_car_plate.*
import kotlinx.android.synthetic.main.activity_user_car_plate.cancel


private const val PERMIT_MSG = "Show Permission to store"
private const val CARD_LIST = "Show Card info"
private const val REQUEST_CVV = "Request Card info"

class BillingData : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_billing_data)

        cardNum.addTextChangedListener(object : TextWatcher {
            var size = 0
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                size = cardNum.text.length

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (s != null && size < s.length) {
                    if ((s.length + 1) % 5 == 0 && s.length < 15) {
                        cardNum.setText("${cardNum.text}-")
                        cardNum.setSelection(cardNum.text.length)
                    }

                }
            }

        })

        cardExp.addTextChangedListener(object : TextWatcher {
            var size = 0
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                size = cardExp.text.length

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (s != null && size < s.length) {
                    if ((s.length + 1) % 3 == 0 && s.length < 3) {
                        cardExp.setText("${cardExp.text}/")
                        cardExp.setSelection(cardExp.text.length)
                    }

                }
            }

        })


        cancel.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
        }

        pay.setOnClickListener {
            if (TextUtils.isEmpty(cardNum.text) ||
                TextUtils.isEmpty(cvcNum.text) ||
                validateCVV() ||
                TextUtils.isEmpty(cardExp.text)
            ) {
                showDialogBox(false, "Entered data is not correct.")
                return@setOnClickListener
            }

            showDialogBox(false, PERMIT_MSG)

//            showDialogBox(true, "")
        }

        cardNum.setOnEditorActionListener { v, actionId, event ->
            if (actionId == 0 || actionId == EditorInfo.IME_ACTION_DONE) {
                cardExp.requestFocus()
            }
            false
        }
        cardExp.setOnEditorActionListener { v, actionId, event ->
            if (actionId == 0 || actionId == EditorInfo.IME_ACTION_DONE) {
                cvcNum.requestFocus()
            }
            false
        }

        cvcNum.setOnEditorActionListener { v, actionId, event ->
            if (actionId == 0 || actionId == EditorInfo.IME_ACTION_DONE) {
                if (TextUtils.isEmpty(cardNum.text) ||
                    TextUtils.isEmpty(cvcNum.text) ||
                    validateCVV() ||
                    TextUtils.isEmpty(cardExp.text)
                ) {
                    showDialogBox(false, "Entered data is not correct.")
                    return@setOnEditorActionListener false
                }

                showDialogBox(false, PERMIT_MSG)
            }
            false
        }

        savCrd.setOnClickListener {
            showDialogBox(false, CARD_LIST)
        }
    }


    private fun showDialogBox(isSuccess: Boolean, message: String) {

        val alertDialog1 = AlertDialog.Builder(
            this
        )
        when {
            message == REQUEST_CVV -> {
                alertDialog1.setTitle("Enter CVV")
                alertDialog1.setMessage("No payment is stored in memory")

                alertDialog1.setIcon(R.drawable.ic_baseline_save_alt_24)
                alertDialog1.setNegativeButton("CLOSE") { _, _ ->
                    alertDialog1.create().cancel()
                }
            }
            message == CARD_LIST -> {
                val cardList = Storage.getCardData(this)

                if (cardList.isEmpty()) {
                    alertDialog1.setTitle("Payment Info")
                    alertDialog1.setMessage("No payment is stored in memory")

                    alertDialog1.setIcon(R.drawable.ic_baseline_save_alt_24)
                    alertDialog1.setNegativeButton("CLOSE") { _, _ ->
                        alertDialog1.create().cancel()
                    }

                } else {
                    alertDialog1.setTitle("Choose Payment Info")
                    alertDialog1.setIcon(R.drawable.ic_baseline_save_alt_24)

                    val list: Array<String> =
                        cardList.map { "Card No: XXXX-XXXX-XXXX-" + it.cardNum + "\nExpiry Date: XX/" + it.expDate }
                            .toTypedArray()
                    alertDialog1.setItems(list) { dialog, item ->
                        showCvvRequest(cardList[item])
//                        showDialogBox(true, REQUEST_CVV)
                    }
                }


            }
            message == PERMIT_MSG -> {
                if (cardNum.text.toString().startsWith("XXXX")){
                    showDialogBox(true, "")
                    return
                }

                alertDialog1.setTitle("Save Payment Info")
                alertDialog1.setMessage("Would you like to store payment info for future usage?")

                alertDialog1.setIcon(R.drawable.ic_baseline_save_alt_24)
                alertDialog1.setPositiveButton("YES") { _, _ ->
                    // store info
                    Storage.storeCardData(
                        this, PayInfo(
                            cardNum.text.takeLast(4).toString(), cardExp.text.takeLast(
                                2
                            ).toString()
                        )
                    )

                    showDialogBox(true, "")
                }
                alertDialog1.setNegativeButton("NO") { _, _ ->
                    // store info

                    showDialogBox(true, "")
                }
            }
            isSuccess -> {
                alertDialog1.setTitle("Payment Successful")

                alertDialog1.setIcon(R.drawable.ic_success)


                alertDialog1.setNeutralButton(
                    "OK"
                ) { _, _ ->
                    val intent = Intent(this, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    startActivity(intent)
                }
            }
            else -> {
                alertDialog1.setTitle("Alert Dialog")

                alertDialog1.setMessage("Please entered required data.")

                alertDialog1.setIcon(R.drawable.ic_failed)


                alertDialog1.setNeutralButton(
                    "OK",
                    DialogInterface.OnClickListener { dialog, which ->
                        if (TextUtils.isEmpty(cardNum.text)) {
                            cardNum.error = "Please enter the Credit Card Number"
                        }
                        if (TextUtils.isEmpty(cvcNum.text)) {
                            cvcNum.error = "Please enter the CVV Number"
                        }
                        if (TextUtils.isEmpty(cardExp.text)) {
                            cardExp.error = "Please enter the Card Expiry Date"
                        }

                            if (validateCVV()) {
                                cardExp.error = "Please enter the valid Card Expiry Date"
                            }

                    })
            }
        }
        alertDialog1.create().show()
    }

    fun validateCVV(): Boolean = try {
        cardExp.text.toString().take(2).toInt() > 12
    } catch (e: Exception) {

        false
    }
    private fun showCvvRequest(payInfo: PayInfo) {
        cardNum.setText("XXXX-XXXX-XXXX-" + payInfo.cardNum)
        cardExp.setText("XX/" + payInfo.expDate)
        cvcNum.requestFocus()

    }

    data class PayInfo(val cardNum: String, val expDate: String)
}