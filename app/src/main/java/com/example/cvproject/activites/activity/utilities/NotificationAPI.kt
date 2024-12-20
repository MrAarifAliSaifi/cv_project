package com.example.cvproject.activites.activity.utilities

import com.example.cvproject.activites.activity.`interface`.NotificationInterface
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NotificationAPI {

    private var retrofit: Retrofit? = null

    fun sendNotification(): NotificationInterface {
        if(retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl("https://fcm.googleapis.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit!!.create(NotificationInterface::class.java)
    }
}
