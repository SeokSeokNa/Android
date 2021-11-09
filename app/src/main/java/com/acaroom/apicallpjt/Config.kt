package com.acaroom.apicallpjt

import android.app.Application
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.acaroom.apicallpjt.activity.LoginActivity
import com.acaroom.apicallpjt.apiService.TokenService
import okhttp3.*
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Config{
    //http://seokseokna.iptime.org:8080
    //http:172.30.1.7:8080
    //192.168.24.10
    const val  url: String = "http://13.209.99.127:8080/"
    const val image: String = url+"/images/"

    var api = Retrofit.Builder().baseUrl(url).addConverterFactory(GsonConverterFactory.create()).build().create(
        TokenService::class.java
    )


    fun getApiClient(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(url)
            .client(provideOkHttpClient(AppInterceptor()))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun provideOkHttpClient(
        interceptor: AppInterceptor
    ): OkHttpClient = OkHttpClient.Builder()
        .run {
            addInterceptor(interceptor)
            build()
        }

    /*
        일일이 api 요청할때마다 헤더에 토큰을 추가해서 보내기 귀찮아서 쓴 클래스
        OkHttp3 Interceptor를 이용한것이다!!
     */
    class AppInterceptor() :Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response= with(chain) {
            //현재 내부저장소에 저장되있는 토큰 만료시간을 보고 api 요청때마다 재발급 할지 안할지 결정함
            //모든 api 요청에 대하여 공통적으로 필요한 헤더파라미터 적용(서버에서 api요청을 받을때 토큰 검사를 하므로 토큰을 담음)

            val newRequest = request().newBuilder()

            var expireDate = App.prefs.expireDate

            if (expireDate != null && expireDate <= System.currentTimeMillis() &&App.prefs.token !=null) { // 토큰 만료시간을 넘었으면
                var res  = api.refresh(App.prefs.refreshToken.toString()).execute();

                if(res.isSuccessful) {
                    var newToken = res.body()?.accessToken;
                    var expireDate = res.body()?.expireDate;
                    Log.i("새로운 토큰!","${newToken}");
                    App.prefs.token = newToken
                    App.prefs.expireDate = expireDate
                } else { //리프레시 토큰 마저 만료되었을 경우
                    App.prefs.removeInfo()
                    val jsonObj = JSONObject(res.errorBody()!!.charStream().readText())
                    Log.i("에러!","${jsonObj.getString("message")}");
                    newRequest.addHeader("refreshToken",App.prefs.refreshToken.toString()) //에러처리를 fragment나 액티비티에 retrofit에서 해야되서 헤더 추가 후 서버에서
                    //리프레시 토큰에 관한 예외를 보내주면 받아서 처리하기 위해 헤더를 넣음 ...(비효율 적인거 같긴한데 ..)
                }

            }

            newRequest.addHeader("Authorization",App.prefs.token.toString())
            proceed(newRequest.build())
        }

    }

}


