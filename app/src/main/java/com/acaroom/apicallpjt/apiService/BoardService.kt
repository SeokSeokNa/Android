package com.acaroom.apicallpjt.apiService

import com.acaroom.apicallpjt.data_domain.BoardDto
import com.acaroom.apicallpjt.data_domain.BoardFormDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface BoardService {

    @GET("api/v1/board/list")
    fun findBoardList(@Header(value = "Authorization") token : String?): Call<List<BoardDto>>

    @POST("api/v1/board/new")
    fun postBoard(@Header(value = "Authorization") token : String?,
                  @Body boardForm: BoardFormDto
    ):Call<Number>


    @Multipart
    @POST("api/v1/board/new")
    fun postBoard2(@Header(value = "Authorization") token : String?,
                   @PartMap() partMap:HashMap<String,RequestBody>,
                   @Part imgFile : ArrayList<MultipartBody.Part>,
    ):Call<Number>
}