package com.mooncowpines.kinostats.utils

import android.util.Log
import org.json.JSONObject
import retrofit2.Response

fun <T> Response<T>.getErrorMessage(): String {
    return try {
        val errorBodyString = this.errorBody()?.string()
        var backendMessage: String? = null

        if (!errorBodyString.isNullOrBlank()) {
            val jsonObject = JSONObject(errorBodyString)
            backendMessage = jsonObject.optString("error", "")
            if (backendMessage.isEmpty()) backendMessage = null
        }

        backendMessage ?: when (this.code()) {
            400 -> "Invalid request"
            401 -> "Invalid credentials"
            404 -> "Resource not found"
            500 -> "Internal server error"
            else -> "An error occurred (${this.code()})"
        }
    } catch (e: Exception) {
        Log.e("ErrorHandler", "Error parsing response", e)
        "Unknown error processing request"
    }
}