package com.acaroom.apicallpjt

import android.app.Application
//최초 실행부
class App:Application() {
    companion object{
        lateinit var prefs: Prefs
    }

    override fun onCreate() {
        prefs = Prefs(applicationContext)
        super.onCreate()
    }
}