package com.acaroom.apicallpjt.apiService

import com.acaroom.apicallpjt.App
import com.acaroom.apicallpjt.data_domain.BoardDto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface BoardService {

    @GET("api/v1/board/list")
    fun findBoardList(@Header(value = "Authorization") token : String?): Call<List<BoardDto>>
}