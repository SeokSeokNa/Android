package com.acaroom.apicallpjt

import android.app.Application
import android.content.Context
import com.google.firebase.messaging.FirebaseMessaging
import okhttp3.internal.Internal.instance

//최초 실행부
class App:Application() {

    companion object{
        lateinit var prefs: Prefs
    }

    override fun onCreate() {
        prefs = Prefs(applicationContext)
        FirebaseMessaging.getInstance().subscribeToTopic("WeAreMemory_topic") //fcm 주제구독 방식으로 하기 위해 사용
        super.onCreate()
    }
}