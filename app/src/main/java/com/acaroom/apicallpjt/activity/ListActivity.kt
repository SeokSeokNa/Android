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
import com.acaroom.apicallpjt.apiService.MemberService
import com.acaroom.apicallpjt.data_domain.MemberDto
import com.acaroom.apicallpjt.recycler_view.ContactsListAdapter
import kotlinx.android.synthetic.main.activity_list.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class ListActivity : AppCompatActivity() {
    val contactsList : ArrayList<MemberDto> = arrayListOf<MemberDto>()

    var retrofit = Retrofit.Builder()
        .baseUrl(Config.url)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    var memberService = retrofit.create(MemberService::class.java)





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
        val adapter = ContactsListAdapter(contactsList)
        mRecyclerView.adapter= adapter //정확히는 모르겠으나 셋팅까지 다한 어뎁터를 화면에 초기화 하면 셋팅이 됨
        var dialog = AlertDialog.Builder(this@ListActivity)
        dialog.setTitle("오류")
        dialog.setPositiveButton("예", DialogInterface.OnClickListener() { _, _->
            val intent = Intent(this@ListActivity, MainActivity::class.java)
            startActivity(intent)
        })

        memberService.findUser(App.prefs.token)
            .enqueue(object : Callback<List<MemberDto>> {
                override fun onResponse(
                    call: Call<List<MemberDto>>,
                    response: Response<List<MemberDto>>
                ) {

                    if (response.isSuccessful) {
                        var result: List<MemberDto>? = response.body();
                        if (result != null) {
                            for (i in 0..result.size - 1) {
                                var member = result.get(i)
                                contactsList.add(member)
                                adapter.notifyDataSetChanged() //retrofit 결과로 받은 데이터들을 어뎁터에 업데이트 시켜줌
                                Log.i(
                                    "${i} 번 결과 : ",
                                    "id=${member?.userId} , name = ${member?.userName} "
                                )
                            }
                        }
                    } else {
                        //500익셉션 에러시 처리방법
                        val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                        dialog.setMessage(" ${jsonObj.getString("message")}").create().show()
                    }


                    /*
                       순서
                        1. 데이터 셋
                        2. 어뎁터 생성
                        3. 어뎁터에서  ViewHolder 초기화 하여 ViewHolder 화면에 값 셋팅
                        4. RecyclerView에 어뎁터 넘겨서 최종하면 그리기
                    */

                }

                override fun onFailure(call: Call<List<MemberDto>>, t: Throwable) {
                    dialog.setTitle("알람!").setMessage("통신 오류 !! = ${t.message}").show()
                    Toast.makeText(this@ListActivity, "실패!", Toast.LENGTH_SHORT).show()
                }
            })


    }
}