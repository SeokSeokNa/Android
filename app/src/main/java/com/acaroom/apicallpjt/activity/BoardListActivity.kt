package com.acaroom.apicallpjt.activity

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.acaroom.apicallpjt.App
import com.acaroom.apicallpjt.Config
import com.acaroom.apicallpjt.MainActivity
import com.acaroom.apicallpjt.R
import com.acaroom.apicallpjt.recycler_view.BoardListAdapter
import com.acaroom.apicallpjt.data_domain.BoardDto
import com.acaroom.apicallpjt.apiService.BoardService
import kotlinx.android.synthetic.main.activity_board_list.*
import kotlinx.android.synthetic.main.activity_list.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class BoardListActivity : AppCompatActivity() {
    val boardDtoList : ArrayList<BoardDto> = arrayListOf<BoardDto>()

    var retrofit = Retrofit.Builder()
        .baseUrl(Config.url)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    var boardService = retrofit.create(BoardService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board_list)
        val adapter = BoardListAdapter(boardDtoList)
        mBoardRecyclerView.adapter = adapter
//        mRecyclerView.adapter= adapter//리사이클러뷰 재활용

        var dialog = AlertDialog.Builder(this@BoardListActivity)
        dialog.setTitle("오류")
        dialog.setPositiveButton("예",DialogInterface.OnClickListener() {_,_->
            val intent = Intent(this@BoardListActivity,MainActivity::class.java)
            startActivity(intent)
        })


        boardService.findBoardList()
            .enqueue(object :Callback<List<BoardDto>>{
                override fun onResponse(call: Call<List<BoardDto>>, response: Response<List<BoardDto>>) {

                    if(response.isSuccessful){
                        var result = response.body();
                        if (result != null) {
                            for(i in result.indices){
                                var board = result[i]
                                boardDtoList.add(board)
                                adapter.notifyDataSetChanged()
                                Log.i("${i} 번 결과 : ", "photo=${board.photoList?.get(0).fileName} ")
                            }
                        }
                    } else {//익셉션으로 인한 500 에러일 경우
                        val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                        dialog.setMessage(" ${jsonObj.getString("message")}").create().show()

//
                    }



                }

                override fun onFailure(call: Call<List<BoardDto>>, t: Throwable) {
                    Toast.makeText(this@BoardListActivity, t.message, Toast.LENGTH_SHORT).show()
                }
            })

    }
}