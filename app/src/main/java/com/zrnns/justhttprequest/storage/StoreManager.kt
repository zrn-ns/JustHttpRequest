package com.zrnns.justhttprequest.storage

import android.content.Context
import android.content.SharedPreferences
import com.android.volley.Request
import com.android.volley.Request.Method

class StoreManager(
    private val context: Context,
) {
    enum class HttpMethod {
        GET {
            override fun toVolleyRequestMethod(): Int {
                return Request.Method.GET
            }},
        POST {
            override fun toVolleyRequestMethod(): Int {
                return Request.Method.POST
            }},
        PUT {
            override fun toVolleyRequestMethod(): Int {
                return Request.Method.PUT
            }},
        DELETE {
            override fun toVolleyRequestMethod(): Int {
                return Request.Method.DELETE
            }},
        PATCH {
            override fun toVolleyRequestMethod(): Int {
                return Request.Method.PATCH
            }};

        abstract fun toVolleyRequestMethod(): Int
    }

    private val shared: SharedPreferences = context.getSharedPreferences("default", Context.MODE_PRIVATE)

    fun saveURL(url: String) {
        shared.edit().putString("url", url).apply()
    }
    fun getURL(): String {
        return shared.getString("url", "unspecified")!!
    }
    fun saveMethod(method: HttpMethod) {
        shared.edit().putString("method", method.toString()).apply()
    }
    fun getMethod(): HttpMethod {
        return HttpMethod.valueOf(shared.getString("method", "GET")!!)
    }
}