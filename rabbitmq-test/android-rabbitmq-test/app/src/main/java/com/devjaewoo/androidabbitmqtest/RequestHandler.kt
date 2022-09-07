package com.devjaewoo.androidabbitmqtest

import android.graphics.Bitmap
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import com.android.volley.*
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

const val TAG = "RequestHandler"
const val SERVER_URL = "http://192.168.25.7:8080"

object RequestHandler {
    var accessToken: String = "" //LoginActivity에서 Login 완료 후 Token 받아 초기화
    private val requestQueue = Volley.newRequestQueue(ApplicationManager.applicationContext)

    private val defaultErrorListener = Response.ErrorListener { error ->
        if (error.networkResponse != null) {
            val body = JSONObject(String(error.networkResponse.data))
            val errorMessage = body.getString("message")
            Log.e(TAG, "defaultErrorListener: code: ${error.networkResponse.statusCode} message: $errorMessage")
            Toast.makeText(ApplicationManager.applicationContext, errorMessage, Toast.LENGTH_SHORT).show()
        } else {
            Log.e(TAG, "defaultErrorListener: null")
            Toast.makeText(ApplicationManager.applicationContext, "null", Toast.LENGTH_SHORT).show()
        }
    }

    fun request(
        url: String,
        jsonObject: JSONObject?,
        responseListener: Response.Listener<JSONObject>,
        errorListener: Response.ErrorListener?,
        requestWithToken: Boolean,
        method: Int
    ) {

        val requestURL = SERVER_URL + url
        Log.d(TAG, "request: $requestURL with data $jsonObject")

        val jsonObjectRequest = object : JsonObjectRequest(
            method,
            requestURL,
            jsonObject,
            responseListener,
            errorListener ?: defaultErrorListener
        ) {

            override fun getHeaders(): MutableMap<String, String> {
                return if (requestWithToken) HashMap<String, String>().apply { put("Authorization", "Bearer $accessToken") } else super.getHeaders()
            }
        }

        requestQueue.add(jsonObjectRequest)
    }

    fun imageRequest(
        url: String,
        responseListener: Response.Listener<Bitmap>,
        errorListener: Response.ErrorListener?,
        requestWithToken: Boolean,
        maxWidth: Int = 0,
        maxHeight: Int = 0,
        scaleType: ImageView.ScaleType = ImageView.ScaleType.CENTER_CROP,
        decodeConfig: Bitmap.Config? = null
    ) {

        val requestURL = SERVER_URL + url
        val imageRequest = object : ImageRequest(
            requestURL,
            responseListener,
            maxWidth,
            maxHeight,
            scaleType,
            decodeConfig,
            errorListener ?: defaultErrorListener
        ) {

            override fun getHeaders(): MutableMap<String, String> {
                return if (requestWithToken) HashMap<String, String>().apply { put("Authorization", "Bearer $accessToken") } else super.getHeaders()
            }
        }

        requestQueue.add(imageRequest)
    }
}