package com.acaroom.apicallpjt

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.acaroom.apicallpjt.activity.*
import com.acaroom.apicallpjt.fragment.board.BoardFragment
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.main_drawer_header.view.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    lateinit var navigationView:NavigationView
    lateinit var drawerLayout:DrawerLayout
    lateinit var barDrawerToggle:ActionBarDrawerToggle;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigationView = nav
        drawerLayout = main_drawer_layout

        //Drawer 조절용 토글 버튼 객체 생성
        barDrawerToggle = ActionBarDrawerToggle(this,drawerLayout,R.string.app_name , R.string.app_name)
        //ActionBar(제목줄)의 홈or업버튼의 위치에 토글아이콘이 보이게끔...
        supportActionBar?.setDisplayHomeAsUpEnabled(true);

        //삼선아이콘 모양으로 보이도록
        //토글버튼의 동기를 맞추기
        barDrawerToggle.syncState()

        //삼선 아이콘과 화살표아이콘이 자동으로 변환하도록
        drawerLayout.addDrawerListener(barDrawerToggle);




//        setSupportActionBar(main_layout_toolbar) // 툴바를 액티비티의 앱바로 지정
//        supportActionBar?.setDisplayHomeAsUpEnabled(true) // 드로어를 꺼낼 홈 버튼 활성화
//        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu_btn) // 홈버튼 이미지 변경
//        supportActionBar?.setDisplayShowTitleEnabled(false) // 툴바에 타이틀 안보이게

        //메뉴에 이벤트걸기
        navigationView.setNavigationItemSelectedListener(this)
        //헤더영역 접근방법
        var nav_header = navigationView.getHeaderView(0)
        //로그인이 만료되지 않았다면
        if(App.prefs.token!=null){
            nav_header.profile_name.text = App.prefs.userNm
        } else {
            nav_header.profile_name.text = "Guest"
        }

        //로그인Activity로 부터 넘어온 결과 받기
        var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result:ActivityResult->
            if(result.resultCode == Activity.RESULT_OK) {
                val login_name = result.data?.getStringExtra("login_name")
                nav_header.profile_name.text = login_name
            }
        }


        //사진 클릭시 로그인 창으로 이동
        nav_header.header_icon.setOnClickListener {
            if(App.prefs.token ==null) {
                val intent = Intent(this, LoginActivity::class.java)
                resultLauncher.launch(intent)
            } else {
                var dialog = AlertDialog.Builder(this)
                dialog.setTitle("알림")
                dialog.setPositiveButton("예", DialogInterface.OnClickListener() { _, _->
                    App.prefs.token = null
                    App.prefs.userNm = null
                    nav_header.profile_name.text = "Guest"
                    drawerLayout.closeDrawer(nav)
                })
                dialog.setNegativeButton("아니요",DialogInterface.OnClickListener() { _, _->

                })
                dialog.setMessage("로그아웃 하시겠습니까?").create().show()

            }

        }


    }

    //햄버거 버튼 , 뒤로가기 버튼 에 따라 알아서 움직이기
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        barDrawerToggle.onOptionsItemSelected(item)
        return super.onOptionsItemSelected(item)
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.member-> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment, BoardFragment())
                    .commit()
//                val intent = Intent(this, MemberListActivity::class.java)
//                startActivity(intent)
            }
            R.id.board-> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment, BoardFragment())
                    .commit()
//                val intent = Intent(this, BoardListActivity::class.java)
//                startActivity(intent)
            }

        }
        //Drawer를 닫기...
        drawerLayout.closeDrawer(nav);
        return false
    }




    override fun onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){ //열려있으면
            drawerLayout.closeDrawers() // 닫고
        }else{
            super.onBackPressed() //열려있지 않다면 기존 뒤로가기 버튼이 동작한다.
        }
    }



}