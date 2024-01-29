package com.rtknits.rt_knits_showroom_logger.api

import android.util.Base64
import android.util.Log
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


const val HOST: String = "https://10.0.0.222:443";

//const val DB_name: String = "Marketing Store"
const val DB_name: String = "Garment"

//const val Garment_DB_name: String = "Garment"

class APIService(private val username: String, private val password: String) {

    var connected = false;
    var ready = false;
    private var client = getTrustingClient()

    init {

//        val json = """
//            {"fmDataSource":
//                [ {
//                    "database": "$Garment_DB_name",
//                    "username" : "$username",
//                    "password" : "$password"
//                   }
//                ]
//            }
//        """.trimIndent()

        val formBody: RequestBody = FormBody.Builder()
            .build()
        val request = Request.Builder()
            .url("$HOST/fmi/data/v2/databases/$DB_name/sessions")
            .post(formBody)
            .header(
                "Authorization",
                "Basic ${
                    Base64.encodeToString(
                        "$username:$password".toByteArray(),
                        Base64.NO_WRAP
                    )
                }"
            ) // Use the extended Claris account name and password encoded in Base64
            .build()

        // Execute the request and get the response
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Handle the failure
                e.printStackTrace()
                ready = true
                connected = false
            }

            override fun onResponse(call: Call, response: Response) {
                ready = true
                connected = if (response.isSuccessful) {
                    // Parse the response body as JSON
                    val json = response.body?.string()?.let { JSONObject(it) }
                    // Get the access token from the JSON object
                    val token = json?.getJSONObject("response")?.getString("token")
                    // Do something with the token
                    Log.d("Token", token ?: "")
                    true;
                } else {
                    // Handle the error
                    Log.e("Error", response.message)
                    false;
                }
            }
        })
    }
}

fun getTrustingClient(): OkHttpClient {
    // create a client that trusts ALL certificates
    // this is because by default, android doesn't trust the custom certificate of FileMaker
    // We can safely trust all certs as this is an internal app
    val sslContext: SSLContext = SSLContext.getInstance("SSL")
    sslContext.init(null, trustAllCerts, java.security.SecureRandom());
    return OkHttpClient.Builder()
        .sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
        .hostnameVerifier { hostname, session -> true }
        .build()
}

var trustAllCerts = arrayOf<TrustManager>(
    object : X509TrustManager {
        override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
        override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
        override fun getAcceptedIssuers(): Array<X509Certificate> {
            return arrayOf()
        }
    }
)