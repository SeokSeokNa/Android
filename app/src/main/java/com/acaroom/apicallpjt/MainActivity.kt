package com.acaroom.apicallpjt

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.acaroom.apicallpjt.activity.*
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.main_drawer_header.*
import kotlinx.android.synthetic.main.main_drawer_header.view.*
import kotlinx.android.synthetic.main.main_toolbar.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        setSupportActionBar(main_layout_toolbar) // 툴바를 액티비티의 앱바로 지정
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // 드로어를 꺼낼 홈 버튼 활성화
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu_btn) // 홈버튼 이미지 변경
        supportActionBar?.setDisplayShowTitleEnabled(false) // 툴바에 타이틀 안보이게

        //메뉴에 이벤트걸기
        main_navigationView.setNavigationItemSelectedListener(this)

        //헤더영역 접근방법
        var nav_header = main_navigationView.getHeaderView(0)
        nav_header.login_btn_test.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }


        //로그인 완료시 헤더에 이름박기
        var loginName = intent?.getStringExtra("login_name")
        if (loginName != null) {
            nav_header.profile_name.text = loginName;
        }


//        btnGoSubActivity.setOnClickListener {
//            val intent = Intent(this, SubActivity::class.java)
//            startActivity(intent)
//        }
//
//        btnGoListActivity.setOnClickListener {
//            val intent = Intent(this, ListActivity::class.java)
//            startActivity(intent)
//        }
//
//        btnBoardListActivity.setOnClickListener {
//            val intent = Intent(this, BoardListActivity::class.java)
//            startActivity(intent)
//        }
//
//        btnGoLogin.setOnClickListener {
//            val intent = Intent(this,LoginActivity::class.java)
//            startActivity(intent)
//        }


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                main_drawer_layout.openDrawer(GravityCompat.START)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.member-> {
                val intent = Intent(this, MemberListActivity::class.java)
                startActivity(intent)
            }
            R.id.board-> {
                val intent = Intent(this, BoardListActivity::class.java)
                startActivity(intent)
            }

        }
        return false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Toast.makeText(this, "돌아왔습니다.", Toast.LENGTH_SHORT).show()
        if (resultCode == 1) {
            var login_name = data?.getStringExtra("login_name")
            var nav_header = main_navigationView.getHeaderView(0)
            nav_header.profile_name.text = login_name
        }
        super.onActivityResult(requestCode, resultCode, data)
    }






}