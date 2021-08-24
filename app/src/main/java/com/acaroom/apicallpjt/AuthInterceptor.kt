package com.acaroom.apicallpjt

import okhttp3.Interceptor
import okhttp3.Response


/*
일일이 api 요청할때마다 헤더에 토큰을 추가해서 보내기 귀찮아서 쓴 클래스
OkHttp3 Interceptor를 이용한것이다!!
 */
class AuthInterceptor:Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var req = chain.request().newBuilder().addHeader("Authorization",App.prefs.token ?: "").build()

        return chain.proceed(req)
    }
}