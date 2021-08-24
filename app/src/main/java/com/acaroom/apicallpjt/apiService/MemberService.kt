package com.acaroom.apicallpjt.apiService

import com.acaroom.apicallpjt.data_domain.MemberDto
import retrofit2.Call
import retrofit2.http.*

interface MemberService {

    @GET("api/v1/user")
    fun findUser(@Header(value = "Authorization")token : String?):Call<List<MemberDto>>
}