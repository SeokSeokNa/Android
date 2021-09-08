package com.acaroom.apicallpjt.fragment.board

import android.animation.ObjectAnimator
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.GridLayoutManager
import com.acaroom.apicallpjt.App
import com.acaroom.apicallpjt.Config
import com.acaroom.apicallpjt.MainActivity
import com.acaroom.apicallpjt.R
import com.acaroom.apicallpjt.apiService.BoardService
import com.acaroom.apicallpjt.data_domain.BoardDto
import com.acaroom.apicallpjt.data_domain.PhotoDto
import com.acaroom.apicallpjt.recycler_view.BoardDetailApdapter
import kotlinx.android.synthetic.main.fragment_board.*
import kotlinx.android.synthetic.main.fragment_board.fabMain
import kotlinx.android.synthetic.main.fragment_board_detail.*
import kotlinx.android.synthetic.main.fragment_board_detail.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class BoardDetailFragment : Fragment()  , MainActivity.onKeyBackPressedListener{
    private var isFabOpen = false
    var retrofit = Config.getApiClient()
    var boardService = retrofit.create(BoardService::class.java)
    var photoList:ArrayList<PhotoDto> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_board_detail, container, false)
        var dialog = androidx.appcompat.app.AlertDialog.Builder(view.context)
        isFabOpen = false
        dialog.setTitle("알림")

        val id = arguments?.getInt("id")!!
        val userId = arguments?.getString("userId")!!

        //자신이 쓴글이 아니면 수정,삭제 버튼 숨기기
        if (App.prefs.userId != userId) {
            view.fabMain.visibility = View.GONE
            view.fabModify.visibility = View.GONE
            view.fabRemove.visibility = View.GONE
        }


        val recycleView = view.board_photo_list
        val adapter = BoardDetailApdapter(photoList)
        recycleView.adapter = adapter

        recycleView.layoutManager = GridLayoutManager(
            activity,
            3,
            GridLayoutManager.VERTICAL,
            false
        )
//        var ver = DividerItemDecoration(recycleView.context, DividerItemDecoration.VERTICAL)
//        var hor = DividerItemDecoration(recycleView.context, DividerItemDecoration.HORIZONTAL) //세로줄
//
//        recycleView.addItemDecoration(ver)
//        recycleView.addItemDecoration(hor)



        view.fabMain.setOnClickListener{
            toggleFab()
        }
        view.fabModify.setOnClickListener {
            modifyBoard(id, userId)
        }


        view.fabRemove.setOnClickListener {
            Log.i("클릭", "!!")
            dialog.setMessage("삭제 하시겠습니까?")
            dialog.setPositiveButton("예", DialogInterface.OnClickListener() { _, _ ->
                deleteBoard(id)
            })
            dialog.setNegativeButton("아니요", DialogInterface.OnClickListener() { _, _ -> })
            dialog.setCancelable(false)
            dialog.show()
        }



        boardService.findBoardDetail(id)
            .enqueue(object : Callback<BoardDto> {
                override fun onResponse(call: Call<BoardDto>, response: Response<BoardDto>) {
                    if (response.isSuccessful) {
                        val result = response.body()
                        if (result != null) {
                            view.board_title.text = result.title
                            view.board_content.text = result.contents
                            var result_photo_list = result.photoList;
                            if (photoList.size > 0) {
                                isFabOpen = false
                            } else {
                                for (i in result_photo_list.indices) {
                                    photoList.add(result_photo_list[i])
                                    adapter.notifyDataSetChanged()
                                }
                            }


                        }
                    }

                }

                override fun onFailure(call: Call<BoardDto>, t: Throwable) {
                    TODO("Not yet implemented")
                }

            })


        return view
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


    private fun modifyBoard(id: Int, userId: String) {
        val boardModifyFragment = BoardModifyFragment()
        val bundle = Bundle()
        bundle.putInt("id", id)
        bundle.putString("userId", userId)
        bundle.putString("title", view?.board_title?.text.toString())
        bundle.putString("content", view?.board_content?.text.toString())
        bundle.putSerializable("photo_list", photoList);
        boardModifyFragment.arguments = bundle

        //activity?.supportFragmentManager?.popBackStack("boardDetail" , FragmentManager.POP_BACK_STACK_INCLUSIVE)
        activity?.supportFragmentManager?.beginTransaction()
            ?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN) //프레그먼트 열릴때 애니메이션 효과
            ?.replace(R.id.fragment, boardModifyFragment)
            ?.addToBackStack("boardModify")
            ?.commit()
    }


    private fun deleteBoard(id: Int) {
        boardService.deleteBoard(id)
            .enqueue(object : Callback<Long> {
                override fun onResponse(call: Call<Long>, response: Response<Long>) {
                    if (response.isSuccessful) {
                        val result = response.body()
                        if (result != null) {
                            Toast.makeText(context, "삭제 되었습니다!!", Toast.LENGTH_SHORT).show()
                            var activity = (activity as MainActivity?)!!
                            activity.supportFragmentManager.popBackStack("boardList", FragmentManager.POP_BACK_STACK_INCLUSIVE)
                            activity.supportFragmentManager.popBackStack("boardDetail", FragmentManager.POP_BACK_STACK_INCLUSIVE)
                            activity.supportFragmentManager.beginTransaction()
                                .replace(R.id.fragment, BoardFragment())
                                .addToBackStack("boardList")
                                .commit()
                        }
                    }
                }

                override fun onFailure(call: Call<Long>, t: Throwable) {
                    Toast.makeText(context, "실패!!!", Toast.LENGTH_SHORT).show()
                }

            })
    }

    /*플로팅 버튼 애니메이션 효과 처리 및 토글 적용*/
    private fun toggleFab() {
        //Toast.makeText(context, "메인 플로팅 버튼 클릭: $isFabOpen", Toast.LENGTH_SHORT).show()

        // 플로팅 액션 버튼 닫기 - 열려있는 플로팅 버튼 집어넣는 애니메이션 세팅
        if(isFabOpen) {
            ObjectAnimator.ofFloat(fabModify, "translationY", 0f).apply { start() }
            ObjectAnimator.ofFloat(fabRemove, "translationY", 0f).apply { start() }

            ObjectAnimator.ofFloat(fabModify_text, "translationY", 0f).apply { start()}
            ObjectAnimator.ofFloat(fabModify_text, "translationX", 0f).apply { start();end();fabModify_text.apply { text="";visibility=View.INVISIBLE } }
            ObjectAnimator.ofFloat(fabRemove_text, "translationY", 0f).apply { start()}
            ObjectAnimator.ofFloat(fabRemove_text, "translationX", 0f).apply { start();end();fabRemove_text.apply { text="";visibility=View.INVISIBLE } }
            fabMain.setImageResource(R.drawable.ic_add)
        } else {
            // 플로팅 액션 버튼 열기 - 닫혀있는 플로팅 버튼 꺼내는 애니메이션 세팅
            ObjectAnimator.ofFloat(fabModify, "translationY", -200f).apply { start() }
            ObjectAnimator.ofFloat(fabRemove, "translationY", -400f).apply { start() }
            ObjectAnimator.ofFloat(fabModify_text, "translationY", -200f).apply { start()}
            ObjectAnimator.ofFloat(fabModify_text, "translationX", -150f).apply { start()
                end()
                fabModify_text.apply {
                    text="수정";
                    visibility=View.VISIBLE
                }
            }

            ObjectAnimator.ofFloat(fabRemove_text, "translationY", -400f).apply { start()}
            ObjectAnimator.ofFloat(fabRemove_text, "translationX", -150f).apply { start()
                end()
                fabRemove_text.apply {
                    text="삭제";
                    visibility=View.VISIBLE
                }
            }

            fabMain.setImageResource(R.drawable.ic_clear)
        }
        isFabOpen = !isFabOpen
    }


}