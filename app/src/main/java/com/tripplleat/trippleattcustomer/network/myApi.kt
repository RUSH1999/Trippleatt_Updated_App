package com.tripplleat.trippleattcustomer.network

import android.content.Context
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.tripplleat.trippleattcustomer.ui.auth.business.jsonListener
import org.json.JSONException

class myApi (private  val ctx: Context, private val listener:jsonListener, val url: String) {

    val requestQueue = Volley.newRequestQueue(ctx)

    fun Jsonparse(){
        val request = JsonObjectRequest(Request.Method.GET, url, null, { response ->
            try {
                listener?.onfinished(response)
            } catch (e: JSONException) {
                listener?.onFailed(e.message.toString())
            }
        }, { error ->
            listener?.onFailed(error.message.toString())})
        requestQueue.add(request)
    }


}

