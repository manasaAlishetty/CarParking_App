package com.example.parkingcars

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import java.lang.reflect.Type


class Storage {
    companion object {

        fun storeCardData(context: BillingData, info: BillingData.PayInfo) {
            val sharedPref = context.getPreferences(Context.MODE_PRIVATE) ?: return

            val list = getCardData(context).toMutableList()
            list.add(info)
            val result = convertListToString(list)


            with(sharedPref.edit()) {
                putString("payInfo", result)
                apply()
            }
        }

        private fun <T> convertListToString(list: List<T>?): String {
            return Gson().toJson(list)
        }

        fun getCardData(context: BillingData): List<BillingData.PayInfo> = runBlocking {
            var test = emptyList<BillingData.PayInfo>()
            val job = GlobalScope.launch(Dispatchers.IO) {
                val result = async {
                    val sharedPref = context.getPreferences(Context.MODE_PRIVATE)
                    val serializedObject: String? = sharedPref.getString("payInfo", null)
                    test = if (serializedObject != null) {
                        val type: Type = object : TypeToken<List<BillingData.PayInfo>?>() {}.type
                        Gson().fromJson(serializedObject, type)
                    } else
                        emptyList()
                    test
                }
                test = result.await()
            }
            job.join()
            return@runBlocking test

        }


        fun getData(context: Context) {

        }
    }
}