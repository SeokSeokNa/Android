package com.acaroom.apicallpjt.fragment.board

import android.animation.ObjectAnimator
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.acaroom.apicallpjt.Config
import com.acaroom.apicallpjt.MainActivity
import com.acaroom.apicallpjt.R
import com.acaroom.apicallpjt.activity.LoginActivity
import com.acaroom.apicallpjt.apiService.BoardService
import com.acaroom.apicallpjt.data_domain.BoardDto
import com.acaroom.apicallpjt.data_domain.BoardListDto
import com.acaroom.apicallpjt.dialog.ProgressDialog
import com.acaroom.apicallpjt.recycler_view.BoardListAdapter
import kotlinx.android.synthetic.main.fragment_board.*
import kotlinx.android.synthetic.main.fragment_board.view.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class BoardFragment : Fragment(), MainActivity.onKeyBackPressedListener {
    private var isFabOpen = false
    lateinit var footer:View
    val boardDtoList: ArrayList<BoardDto?> = arrayListOf<BoardDto?>()
    val adapter = BoardListAdapter(boardDtoList ,this)
    var retrofit = Config.getApiClient()
    var boardService = retrofit.create(BoardService::class.java)
    var hasNext: Boolean = false
    var page: Int = 0
    lateinit var alertView : View


    lateinit var recyclerView: RecyclerView;
    lateinit var customProgressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_board, container, false)
        alertView = view



        view.fabMain.setOnClickListener {
            toggleFab()
        }
        view.fabWrite.setOnClickListener {
            var activity: MainActivity
            activity = (getActivity() as MainActivity?)!!
            activity.supportFragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN) //프레그먼트 열릴때 애니메이션 효과
                .replace(R.id.fragment, WriteFormFragment())
                .addToBackStack("boardForm")
                .commit()
        }


        var activity: MainActivity
        activity = (getActivity() as MainActivity?)!!
        //로딩창 객체 생성
        customProgressDialog = ProgressDialog(view.context)
        //로딩창 투명하게
        customProgressDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        customProgressDialog.setCancelable(false)


        recyclerView = view.mBoardRecyclerView2


        recyclerView.adapter = adapter
        recyclerView.layoutManager = GridLayoutManager(
            activity,
            1,
            GridLayoutManager.VERTICAL,
            false
        )
        var ver = DividerItemDecoration(recyclerView.context, DividerItemDecoration.VERTICAL)
        recyclerView.addItemDecoration(ver)
        if (boardDtoList.size > 0) {
            adapter.getFooter().visibility=View.GONE
            isFabOpen = false
            return view
        }


        callData()


        return view
    }


    private fun callData() {
        boardService.findBoardList(page)
            .enqueue(object : Callback<BoardListDto> {
                override fun onResponse(
                    call: Call<BoardListDto>,
                    response: Response<BoardListDto>
                ) {

                    if (response.isSuccessful) {
                        var result = response.body()?.data;
                        hasNext = response.body()?.hasNext!!
                        if (result != null) {

                            for (i in result.indices) {
                                var board = result[i]
                                boardDtoList.add(board)
                            }
                            adapter.notifyDataSetChanged()
                            customProgressDialog.dismiss()

                            when (hasNext) {
                                true -> adapter.getFooter().visibility = View.VISIBLE
                                else -> adapter.getFooter().visibility = View.GONE

                            }

                        }
                    } else {//익셉션으로 인한 500 에러일 경우
                        val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                        var dialog = AlertDialog.Builder(alertView.context)
                        dialog.setTitle("오류")
                        dialog.setCancelable(false)
                        dialog.setPositiveButton("예", DialogInterface.OnClickListener() { _, _ ->
                            (activity as MainActivity).supportFragmentManager.beginTransaction()
                                .remove(this@BoardFragment)
                                .commit()

                            var intent = Intent(activity, LoginActivity::class.java)
                            startActivity(intent)
                        })
                        Log.e("result","${jsonObj.getString("message")}")
                        dialog.setMessage(" ${jsonObj.getString("message")}").create().show()
                        //App.prefs.removeInfo()
                    }


                }

                override fun onFailure(call: Call<BoardListDto>, t: Throwable) {
                    Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()

                }
            })
    }


    public fun initScrollListener() {
        if (hasNext) {
            page += 1
            loadMore()
        } else {

        }
    }

    private fun loadMore() {
        customProgressDialog.show()
        var handler = Handler()
        handler.postDelayed({
            callData()
        }, 2000)
    }



    /*플로팅 버튼 애니메이션 효과 처리 및 토글 적용*/
    private fun toggleFab() {
        //Toast.makeText(context, "메인 플로팅 버튼 클릭: $isFabOpen", Toast.LENGTH_SHORT).show()

        // 플로팅 액션 버튼 닫기 - 열려있는 플로팅 버튼 집어넣는 애니메이션 세팅
        if (isFabOpen) {
            ObjectAnimator.ofFloat(fabWrite, "translationY", 0f).apply { start() }
            ObjectAnimator.ofFloat(fabCamera, "translationY", 0f).apply { start() }
            ObjectAnimator.ofFloat(fabWrite_text, "translationY", 0f).apply { start() }
            ObjectAnimator.ofFloat(fabWrite_text, "translationX", 0f).apply {
                start()

                fabWrite_text.apply {
                    text = ""
                    visibility = View.INVISIBLE
                }
            }
            fabMain.setImageResource(R.drawable.ic_add)
        } else {
            // 플로팅 액션 버튼 열기 - 닫혀있는 플로팅 버튼 꺼내는 애니메이션 세팅
            ObjectAnimator.ofFloat(fabWrite, "translationY", -200f).apply { start() }
            ObjectAnimator.ofFloat(fabCamera, "translationY", -400f).apply { start() }
            ObjectAnimator.ofFloat(fabWrite_text, "translationY", -200f).apply { start() }
            ObjectAnimator.ofFloat(fabWrite_text, "translationX", -150f).apply {
                start()
                end()
                fabWrite_text.apply {
                    text = "글쓰기";
                    visibility = View.VISIBLE
                }
            }

            fabMain.setImageResource(R.drawable.ic_clear)
        }
        isFabOpen = !isFabOpen
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        (context as MainActivity).setOnKeyBackPressedListener(this)
    }

    override fun onBackKey() {
        val activity: MainActivity? = activity as MainActivity?
        activity?.setOnKeyBackPressedListener(null);
        activity?.onBackPressed()
    }

}