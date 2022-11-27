package com.zrnns.justhttprequest.storage

import android.content.Context
import android.content.SharedPreferences

class StoreManager(
    private val context: Context,
) {
    private val shared: SharedPreferences = context.getSharedPreferences("default", Context.MODE_PRIVATE)

    fun saveURL(url: String) {
        shared.edit().putString("url", url).apply()
    }
    fun getURL(): String {
        return shared.getString("url", "unspecified")!!
    }
}