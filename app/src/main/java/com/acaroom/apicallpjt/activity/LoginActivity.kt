package com.acaroom.apicallpjt.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.acaroom.apicallpjt.*
import com.acaroom.apicallpjt.apiService.TokenService
import com.acaroom.apicallpjt.data_domain.LoginReqDto
import com.acaroom.apicallpjt.data_domain.LoginResDto
import com.royrodriguez.transitionbutton.TransitionButton
import com.royrodriguez.transitionbutton.TransitionButton.OnAnimationStopEndListener
import kotlinx.android.synthetic.main.activit_login.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_write_form.view.*
import kotlinx.android.synthetic.main.main_drawer_header.*
import kotlinx.android.synthetic.main.main_drawer_header.view.*
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

        userId.setOnKeyListener { view, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                userPass.requestFocus()
            }
            true
        }

        userPass.setOnKeyListener{ view, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(userPass.windowToken, 0)
                transition_button.performClick()
            }
            true

        }

        transition_button.setOnClickListener {
            var id = userId.text.toString()
            var pass = userPass.text.toString()
            if(id==""){
                Toast.makeText(this@LoginActivity, "???????????? ???????????????", Toast.LENGTH_SHORT).show()
            } else if(pass==""){
                Toast.makeText(this@LoginActivity, "??????????????? ???????????????", Toast.LENGTH_SHORT).show()
            } else {
                transition_button.startAnimation()
                // Do your networking task or background work here.
                val handler = Handler()
                handler.postDelayed({
                    clickLoginBtn(id, pass)

                }, 2000)
            }


        }

    }

    private fun checkLoginBtn() {
        //AuthInterceptor ???????????? ?????? intercept???????????? ??????
        var retrofit = Retrofit.Builder()
            .baseUrl(Config.url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        var tokenService = retrofit.create(TokenService::class.java);
        tokenService.check()
            .enqueue(object : Callback<LoginResDto> {
                override fun onResponse(call: Call<LoginResDto>, response: Response<LoginResDto>) {
                    var result = response.body();
                    if (result?.status != 1) App.prefs.token = ""
                    Toast.makeText(
                        this@LoginActivity,
                        "${result?.status} , ${result?.message}",
                        Toast.LENGTH_SHORT
                    ).show()

                }

                override fun onFailure(call: Call<LoginResDto>, t: Throwable) {
                    Toast.makeText(this@LoginActivity, "??????", Toast.LENGTH_SHORT).show()
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
                        Toast.makeText(this@LoginActivity, "????????? ??????!", Toast.LENGTH_SHORT).show()
                        App.prefs.token = result?.userAccess?.accessToken;//?????? ???????????? ????????? ?????? ????????????(access)
                        App.prefs.refreshToken = result?.userAccess?.refreshToken;//?????? ???????????? ????????? ?????? ????????????(refresh)
                        App.prefs.userNm = result?.userAccess?.userName;//?????? ???????????? ????????? ???????????? ????????????
                        App.prefs.userId = result?.userAccess?.userId;//?????? ???????????? ????????? ??????????????? ????????????(??? ??????,?????? ????????? ?????? ??????????????? ?????? ??????????????????)
                        App.prefs.expireDate = result?.userAccess?.expireDate
                        Log.i("??????", result.toString());

                        transition_button.stopAnimation(TransitionButton.StopAnimationStyle.EXPAND,
                            OnAnimationStopEndListener {
                                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                intent.putExtra("login_name", result.userAccess.userName)
                                //intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                                setResult(Activity.RESULT_OK, intent)
                                finish()
                            })
                    } else {
                        Toast.makeText(
                            this@LoginActivity,
                            "????????? ?????? ??????????????? ???????????????!",
                            Toast.LENGTH_SHORT
                        ).show()
                        transition_button.stopAnimation(
                            TransitionButton.StopAnimationStyle.SHAKE,
                            null
                        )

                    }
                }

                override fun onFailure(call: Call<LoginResDto>, t: Throwable) {
                    if (t.cause == null) {
                        Log.i("??????", "?????? ??????");
                        Toast.makeText(this@LoginActivity, "????????????, ??????????????? ???????????????", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        Toast.makeText(this@LoginActivity, t.message, Toast.LENGTH_SHORT).show()

                    }

                    transition_button.stopAnimation(TransitionButton.StopAnimationStyle.SHAKE, null)

                }
            })

    }
}