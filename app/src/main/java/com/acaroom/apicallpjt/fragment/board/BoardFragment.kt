package com.acaroom.apicallpjt.fragment.board

import android.animation.ObjectAnimator
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.acaroom.apicallpjt.Config
import com.acaroom.apicallpjt.MainActivity
import com.acaroom.apicallpjt.R
import com.acaroom.apicallpjt.activity.LoginActivity
import com.acaroom.apicallpjt.apiService.BoardService
import com.acaroom.apicallpjt.data_domain.BoardDto
import com.acaroom.apicallpjt.recycler_view.BoardListAdapter
import kotlinx.android.synthetic.main.fragment_board.*
import kotlinx.android.synthetic.main.fragment_board.view.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class BoardFragment : Fragment() , MainActivity.onKeyBackPressedListener {
    private var isFabOpen = false
    val boardDtoList : ArrayList<BoardDto> = arrayListOf<BoardDto>()
    val adapter = BoardListAdapter(boardDtoList)
    var retrofit = Config.getApiClient()
    var boardService = retrofit.create(BoardService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("상태","${savedInstanceState}")


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.i("상태","${savedInstanceState}")
        val view = inflater.inflate(R.layout.fragment_board, container, false)

        adapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY


        view.fabMain.setOnClickListener{
            toggleFab()
        }
        view.fabWrite.setOnClickListener {
            var activity :MainActivity
            activity = (getActivity() as MainActivity?)!!
            activity.supportFragmentManager.beginTransaction()
                .replace(R.id.fragment, WriteFormFragment())
                .addToBackStack(null)
                .commit()
        }

        var dialog = AlertDialog.Builder(view.context)
        dialog.setTitle("오류")
        dialog.setPositiveButton("예", DialogInterface.OnClickListener() { _, _ ->
            (activity as MainActivity).supportFragmentManager.beginTransaction()
                .remove(this)
                .commit()

            var intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
        })
        var activity: MainActivity
        activity = (getActivity() as MainActivity?)!!


        var recyclerView = view.mBoardRecyclerView2


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
            isFabOpen = false
            return view
        }


        boardService.findBoardList()
            .enqueue(object : Callback<List<BoardDto>> {
                override fun onResponse(
                    call: Call<List<BoardDto>>,
                    response: Response<List<BoardDto>>
                ) {

                    if (response.isSuccessful) {
                        var result = response.body();
                        if (result != null) {

                            for (i in result.indices) {
                                var board = result[i]
                                boardDtoList.add(board)
                                adapter.notifyDataSetChanged()
                            }
                        }
                    } else {//익셉션으로 인한 500 에러일 경우
                        val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                        dialog.setMessage(" ${jsonObj.getString("message")}").create().show()
                        //App.prefs.removeInfo()
                    }


                }

                override fun onFailure(call: Call<List<BoardDto>>, t: Throwable) {
                    Toast.makeText(container?.context, t.message, Toast.LENGTH_SHORT).show()
                }
            })

        return view
    }


    /*플로팅 버튼 애니메이션 효과 처리 및 토글 적용*/
    private fun toggleFab() {
        Toast.makeText(context, "메인 플로팅 버튼 클릭: $isFabOpen", Toast.LENGTH_SHORT).show()

        // 플로팅 액션 버튼 닫기 - 열려있는 플로팅 버튼 집어넣는 애니메이션 세팅
        if(isFabOpen) {
            ObjectAnimator.ofFloat(fabWrite, "translationY", 0f).apply { start() }
            ObjectAnimator.ofFloat(fabCamera, "translationY", 0f).apply { start() }
            fabMain.setImageResource(R.drawable.ic_add)
        } else {
            // 플로팅 액션 버튼 열기 - 닫혀있는 플로팅 버튼 꺼내는 애니메이션 세팅
            ObjectAnimator.ofFloat(fabWrite, "translationY", -200f).apply { start() }
            ObjectAnimator.ofFloat(fabCamera, "translationY", -400f).apply { start() }
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