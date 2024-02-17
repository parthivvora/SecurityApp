package com.example.securityapp.fcmService

import android.app.Activity
import android.content.Context
import android.util.Log
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.securityapp.R
import org.json.JSONException
import org.json.JSONObject

class FcmNotificationsSender(
    private var userFcmToken: String?,
    private var title: String,
    private var body: String,
    var mContext: Context,
    private var mActivity: Activity
) {
    private var requestQueue: RequestQueue? = null
    private val postUrl = "https://fcm.googleapis.com/fcm/send"
    private val fcmServerKey =
        "AAAAKOMxxsM:APA91bF8PrmUWDY3OinmCKvNsW9SVviP7k7CBuYSHFH5WvCVT48PL5oZfbzyiaOnnDv72Kn2q8_aK31jaFjOR0AFT3948ALlCt-dgf9TUsl3Jsen8rzATwDRlEhBMDgbwUn8UAVmiYF5"

    fun SendNotifications() {
        requestQueue = Volley.newRequestQueue(mActivity)
        val mainObj = JSONObject()
        try {
            mainObj.put("to", userFcmToken)
            val notiObject = JSONObject()
            notiObject.put("title", title)
            notiObject.put("body", body)
            notiObject.put("icon", R.drawable.app_icon_logo)
            mainObj.put("notification", notiObject)
            val request: JsonObjectRequest =
                object : JsonObjectRequest(Method.POST, postUrl, mainObj, Response.Listener {
                    // code run is got response
                }, Response.ErrorListener {
                    // code run is got error
                }) {
                    @Throws(AuthFailureError::class)
                    override fun getHeaders(): Map<String, String> {
                        val header: MutableMap<String, String> = HashMap()
                        header["content-type"] = "application/json"
                        header["authorization"] = "key=$fcmServerKey"
                        return header
                    }
                }
            requestQueue!!.add(request)
        } catch (e: JSONException) {
            e.printStackTrace()
            Log.d(
                "error in sending notification",
                "parthiv: ${e.message}"
            )
        }
    }
}
