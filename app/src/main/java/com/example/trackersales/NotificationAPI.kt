package com.example.trackersales

import com.example.trackersales.dataclass.PushNotifikasi
import com.example.trackersales.forfirebasecloudmessaging.Constants.Companion.CONTENT_TYPE
import com.example.trackersales.forfirebasecloudmessaging.Constants.Companion.SERVER_KEY
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NotificationAPI {

    @Headers("Authorization: key=$SERVER_KEY", "Content-Type:$CONTENT_TYPE")
    @POST("fcm/send")
    suspend fun postNotification(
        @Body notification: PushNotifikasi
    ): Response<ResponseBody>
}