package com.acaroom.apicallpjt.apiService

import com.acaroom.apicallpjt.data_domain.BoardDto
import com.acaroom.apicallpjt.data_domain.BoardFormDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface BoardService {

    @GET("api/v1/board/list")
    fun findBoardList(): Call<List<BoardDto>>

    @POST("api/v1/board/new")
    fun postBoard(@Body boardForm: BoardFormDto): Call<Number>


    @Multipart
    @POST("api/v1/board/new")
    fun postBoard2(
        @PartMap() partMap: HashMap<String, RequestBody>,
        @Part imgFile: ArrayList<MultipartBody.Part>,
    ): Call<Number>

    @GET("api/v1/boardDetail/{id}") //id는 boardNo
    fun findBoardDetail(@Path("id") id: Int): Call<BoardDto>

    @DELETE("api/v1/boardDelete/{id}")
    fun deleteBoard(@Path("id") id:Int):Call<Long>
}