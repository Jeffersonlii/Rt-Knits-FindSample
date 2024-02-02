package com.rtknits.rt_knits_showroom_logger.api

import android.util.Log
import kotlinx.coroutines.delay
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Credentials
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.internal.EMPTY_REQUEST
import org.json.JSONArray
import org.json.JSONObject
import ru.gildor.coroutines.okhttp.await
import java.io.IOException
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

const val HOST: String = "https://10.0.0.222:443";
const val DB_name: String = "Marketing Store"
const val SHOWROOMLOG_LAYOUT: String = "ShowroomLog"
const val SAMPLEGARMENT_LAYOUT: String = "ShowroomLogSampleGarment"

enum class Operation {
    ADDITION,
    REMOVAL
}

data class SimpleResponse(
    val success: Boolean,
    val errorMessage: String
)

data class SampleInfo(
    val sampleId: String,
    val customerName: String,
    val description: String
)

data class DataResponse(
    val success: Boolean,
    val sampleInfo: SampleInfo?,
)

// An connection to the marketing store database
// make sure the input user has FMRest enabled!
class APIService(private val username: String, private val password: String) {
    var connected = false;
    var ready = false;
    private var client = getTrustingClient()
    private var authKey: String? = null;
    private fun getAuthKey(): String {
        return authKey ?: throw Exception("auth key does not exist, log in failed");
    }

    init {
        login()
    }

    fun login(){
        val body = EMPTY_REQUEST
        val request = Request.Builder()
            .url("$HOST/fmi/data/vLatest/databases/$DB_name/sessions")
            .addHeader(
                "Authorization",
                Credentials.basic(username, password)
            )
            .addHeader("Content-Type", "application/json")
            .post(body)
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
                    authKey = token
                    true;
                } else {
                    // Handle the error
                    Log.e("Error", response.message)
                    false;
                }
            }
        })
    }

    suspend fun createShowRoomLog(
        sampleId: String,
        operation: Operation
    ): SimpleResponse {
        val json = JSONObject()
        val pairs = JSONObject()
            .put("kf_SampleId", sampleId)
            .put("Operation", operation.toString())

        json.put("fieldData", pairs)
        val body = json.toString().toRequestBody("".toMediaTypeOrNull())

        val request = Request.Builder()
            .url("$HOST/fmi/data/vLatest/databases/$DB_name/layouts/$SHOWROOMLOG_LAYOUT/records")
            .addHeader("Content-Type", "application/json")
            .addHeader(
                "Authorization",
                "Bearer ${getAuthKey()}"
            )
            .post(body)
            .build()
        val resp: Response = client.newCall(request).await()
        return if (resp.isSuccessful) {
            SimpleResponse(true, "")
        } else {
            val err = resp.body?.string() ?: "FileMaker reached but request rejected"
            resp.close()
            SimpleResponse(
                false,
                err
            )
        }
    }

    fun getSampleInformation(
        sampleId: String,
        onInfo: (DataResponse) -> Unit
    ) {
        val json = JSONObject()
        json.put(
            "query",
            JSONArray().put(
                JSONObject().put("kpSampleId", sampleId)
            )
        )
        val body = json.toString().toRequestBody("".toMediaTypeOrNull())

        val request = Request.Builder()
            .url("$HOST/fmi/data/vLatest/databases/$DB_name/layouts/$SAMPLEGARMENT_LAYOUT/_find")
            .addHeader("Content-Type", "application/json")
            .addHeader(
                "Authorization",
                "Bearer ${getAuthKey()}"
            )
            .post(body)
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
            }

            override fun onResponse(call: Call, response: Response) {
                var customerName = ""
                var desc = ""
                if (response.isSuccessful) {
                    val jsonString = response.body?.string() ?: ""

                    val jsonObject = JSONObject(jsonString)
                    val data = jsonObject.getJSONObject("response").getJSONArray("data")
                    if(data.length()==0){
                        onInfo(DataResponse(
                            response.isSuccessful,
                            null
                        ))
                        return
                    }

                    for (i in 0 until data.length()) {
                        val fieldData = data.getJSONObject(i).getJSONObject("fieldData")
                        customerName = fieldData.getString("kf_CustomerName")
                        desc = fieldData.getString("Description")

                    }
                }
                onInfo(
                    DataResponse(
                        response.isSuccessful,
                        SampleInfo(
                            customerName = customerName,
                            sampleId = sampleId,
                            description = desc
                        )
                    )
                )
            }
        })
    }

    fun getSamplePreviousOperation(
        sampleId: String,
        onInfo: (Operation?) -> Unit
    ) {
        val json = JSONObject()
        json.put(
            "query",
            JSONArray().put(
                JSONObject().put("kf_SampleId", sampleId)
            )
        ).put(
            "sort",
            JSONArray().put(
                JSONObject()
                    .put("fieldName", "LogTime")
                    .put("sortOrder","descend")
            )
        ).put("limit", 1)
        val body = json.toString().toRequestBody("".toMediaTypeOrNull())

        val request = Request.Builder()
            .url("$HOST/fmi/data/vLatest/databases/$DB_name/layouts/$SHOWROOMLOG_LAYOUT/_find")
            .addHeader("Content-Type", "application/json")
            .addHeader(
                "Authorization",
                "Bearer ${getAuthKey()}"
            )
            .post(body)
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onInfo(null)
            }

            override fun onResponse(call: Call, response: Response) {
                var opt: Operation? = null
                if (response.isSuccessful) {
                    val jsonString = response.body?.string() ?: ""

                    val jsonObject = JSONObject(jsonString)
                    val data = jsonObject.getJSONObject("response").getJSONArray("data")
                    for (i in 0 until data.length()) {
                        val fieldData = data.getJSONObject(i).getJSONObject("fieldData")
                        opt = when(fieldData.getString("Operation")){
                            Operation.ADDITION.toString() -> Operation.ADDITION
                            Operation.REMOVAL.toString() -> Operation.REMOVAL
                            else -> null
                        }
                    }
                }
                onInfo(opt)
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