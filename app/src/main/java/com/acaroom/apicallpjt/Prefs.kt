package com.acaroom.apicallpjt

import android.content.Context
import android.content.Context.MODE_PRIVATE

//shared preference
//간단한 설정 값을 앱 내부 DB에 저장하기에 용이한 내부 저장소 (앱 삭제시 데이터도 소거됨)
class Prefs(context: Context) {
    private val preNm = "mPref"
    private val prefs = context.getSharedPreferences(preNm,MODE_PRIVATE)

    //토큰을 get ,set 메소드로 이용
    var token:String?
    get() = prefs.getString("token" , null)
    set(value) = prefs.edit().putString("token",value).apply()


}