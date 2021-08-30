package com.acaroom.apicallpjt.fragment

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.acaroom.apicallpjt.App
import com.acaroom.apicallpjt.Config
import com.acaroom.apicallpjt.MainActivity
import com.acaroom.apicallpjt.R
import com.acaroom.apicallpjt.apiService.BoardService
import com.acaroom.apicallpjt.data_domain.BoardDto
import com.acaroom.apicallpjt.recycler_view.BoardListAdapter
import kotlinx.android.synthetic.main.activity_board_list.*
import kotlinx.android.synthetic.main.fragment_board.*
import kotlinx.android.synthetic.main.fragment_board.view.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory




class BoardFragment : Fragment() {
    val boardDtoList : ArrayList<BoardDto> = arrayListOf<BoardDto>()
    var retrofit = Retrofit.Builder()
        .baseUrl(Config.url)
        .addConverterFactory(GsonConverterFactory.create())
        .build()


    var boardService = retrofit.create(BoardService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_board, container, false)
        var dialog = AlertDialog.Builder(view.context)
        dialog.setTitle("오류")
        dialog.setPositiveButton("예",DialogInterface.OnClickListener() {_,_->

        })
        val adapter = BoardListAdapter(boardDtoList)
        view.mBoardRecyclerView2.adapter = adapter


        boardService.findBoardList(App.prefs.token)
            .enqueue(object : Callback<List<BoardDto>> {
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

                    }



                }

                override fun onFailure(call: Call<List<BoardDto>>, t: Throwable) {
                    Toast.makeText(container?.context, t.message, Toast.LENGTH_SHORT).show()
                }
            })

        return view
    }

}