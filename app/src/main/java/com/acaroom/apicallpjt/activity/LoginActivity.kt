package com.acaroom.apicallpjt.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.acaroom.apicallpjt.*
import com.acaroom.apicallpjt.apiService.TokenService
import com.acaroom.apicallpjt.data_domain.LoginReqDto
import com.acaroom.apicallpjt.data_domain.LoginResDto
import com.royrodriguez.transitionbutton.TransitionButton
import com.royrodriguez.transitionbutton.TransitionButton.OnAnimationStopEndListener
import kotlinx.android.synthetic.main.activit_login.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activit_login)


        transition_button.setOnClickListener {
            var id = userId.text.toString()
            var pass = userPass.text.toString()
            if(id==""){
                Toast.makeText(this@LoginActivity, "아이디를 입력하세요", Toast.LENGTH_SHORT).show()
            } else if(pass==""){
                Toast.makeText(this@LoginActivity, "비밀번호를 입력하세요", Toast.LENGTH_SHORT).show()
            } else {
                transition_button.startAnimation()
                // Do your networking task or background work here.
                val handler = Handler()
                handler.postDelayed({
                    clickLoginBtn(id,pass)

                }, 2000)
            }


        }

    }

    private fun checkLoginBtn() {
        //AuthInterceptor 클래스에 있는 intercept메소드를 호출
        val okHttpClient = OkHttpClient.Builder().addInterceptor(AuthInterceptor()).build()
        var retrofit = Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(Config.url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        var tokenService = retrofit.create(TokenService::class.java);
        tokenService.check()
            .enqueue(object : Callback<LoginResDto> {
                override fun onResponse(call: Call<LoginResDto>, response: Response<LoginResDto>) {
                    var result = response.body();
                    if (result?.status != 1) App.prefs.token = ""
                    Toast.makeText(this@LoginActivity, "${result?.status} , ${result?.message}", Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(call: Call<LoginResDto>, t: Throwable) {
                    Toast.makeText(this@LoginActivity, "실패", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun clickLoginBtn(id: String, pass: String) {
        var retrofit = Retrofit.Builder()
            .baseUrl(Config.url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        var tokenService = retrofit.create(TokenService::class.java);

        tokenService.login(LoginReqDto(id, pass))
            .enqueue(object : Callback<LoginResDto> {
                override fun onResponse(call: Call<LoginResDto>, response: Response<LoginResDto>) {
                    var result = response.body();
                    if (result?.status == 1) {
                        Toast.makeText(this@LoginActivity, "로그인 완료!", Toast.LENGTH_SHORT).show()
                        App.prefs.token = result?.userAccess?.accessToken;//내부 저장소에 받아온 토큰 넣어두기
                        Log.i("결과", result.toString());
                        transition_button.stopAnimation(TransitionButton.StopAnimationStyle.EXPAND,
                            OnAnimationStopEndListener {
                                val intent = Intent(baseContext, MainActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                                startActivity(intent)
                            })
                    } else {
                        Toast.makeText(this@LoginActivity, "아이디 또는 비밀번호가 틀렸습니다!", Toast.LENGTH_SHORT).show()
                        transition_button.stopAnimation(TransitionButton.StopAnimationStyle.SHAKE, null)

                    }
                }

                override fun onFailure(call: Call<LoginResDto>, t: Throwable) {
                    if(t.cause==null){
                        Log.i("결과", "통신 실패");
                        Toast.makeText(this@LoginActivity, "연결실패, 관리자에게 문의하세요", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@LoginActivity, t.message, Toast.LENGTH_SHORT).show()

                    }

                    transition_button.stopAnimation(TransitionButton.StopAnimationStyle.SHAKE, null)

                }
            })

    }
}