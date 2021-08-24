package com.acaroom.apicallpjt.apiService

import com.acaroom.apicallpjt.data_domain.LoginReqDto
import com.acaroom.apicallpjt.data_domain.LoginResDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface TokenService {

    @POST("/api/login")
    fun login(@Body loginRequest: LoginReqDto): Call<LoginResDto>

    @POST("/api/tokenCheck")
    fun check() :Call<LoginResDto>
}