package com.acaroom.apicallpjt.fragment.board

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.acaroom.apicallpjt.App
import com.acaroom.apicallpjt.Config
import com.acaroom.apicallpjt.R
import com.acaroom.apicallpjt.apiService.BoardService
import com.acaroom.apicallpjt.data_domain.BoardFormDto
import com.acaroom.apicallpjt.dialog.ProgressDialog
import com.royrodriguez.transitionbutton.TransitionButton
import kotlinx.android.synthetic.main.activit_login.*
import kotlinx.android.synthetic.main.fragment_write_form.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WriteFormFragment : Fragment() {
    lateinit var customProgressDialog:ProgressDialog
    lateinit var dialog:AlertDialog.Builder
    var handler = Handler()
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
        val view = inflater.inflate(R.layout.fragment_write_form, container, false);

        dialog = AlertDialog.Builder(view.context)
        dialog.setTitle("알림")



        //로딩창 객체 생성
        customProgressDialog = ProgressDialog(view.context)
        //로딩창 투명하게
        customProgressDialog.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))

        view.regist_btn.setOnClickListener {
            var title = view.board_title.text.toString()
            var content = view.board_content.text.toString();

            if(title==""){
                Toast.makeText(view.context, "제목을 입력하세요", Toast.LENGTH_SHORT).show()
            } else if(content==""){
                Toast.makeText(view.context, "내용을 입력하세요", Toast.LENGTH_SHORT).show()
            } else {

                customProgressDialog.show()
                //로딩화면 일부러 보여주려고 2초 지연줫음 ...
                handler.postDelayed({
                    postBoard(title,content)
                },2000)

            }


        }

        return view
    }


    private fun postBoard(title: String , content:String){
        boardService.postBoard(App.prefs.token , BoardFormDto(title,content))
            .enqueue(object : Callback<Number>{
                override fun onResponse(call: Call<Number>, response: Response<Number>) {



                    if (response.isSuccessful) {
                        var result = response.body();
                        if(result != null) {
                            Log.i("결과 : ", "${result} ")
                            dialog.setPositiveButton("예", DialogInterface.OnClickListener() { _, _ ->
                                    customProgressDialog.dismiss()

                            })

                            dialog.setMessage("등록되었습니다!").show()

                        }
                    }


                }

                override fun onFailure(call: Call<Number>, t: Throwable) {
                    Log.d("실패 : ", "전송 실패!!!")
                    customProgressDialog.dismiss()
                    dialog.setPositiveButton("예", DialogInterface.OnClickListener() { _, _ ->
                        customProgressDialog.dismiss()
                    })
                        dialog.setMessage("등록에 실패하였습니다 관리자에게 문의하세요!").show()

                }
            })
    }

}