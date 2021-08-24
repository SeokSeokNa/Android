package com.acaroom.apicallpjt.recycler_view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.acaroom.apicallpjt.R
import com.acaroom.apicallpjt.data_domain.BoardDto

class BoardListAdapter(val boardDtoList : ArrayList<BoardDto>) : RecyclerView.Adapter<BoardViewHolder>() {

    override fun getItemCount(): Int {
        return boardDtoList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardViewHolder {
        val inflatedView = LayoutInflater.from(parent.context)
            .inflate(R.layout.board_holder, parent, false)//화면을 가져옴
        return BoardViewHolder(inflatedView) //ViewHolder에 가져온 화면을 넘겨줌(ContactsViewHolder 생성!!)
    }

    override fun onBindViewHolder(holder: BoardViewHolder, position: Int) {
        val item = boardDtoList[position]
        holder.apply {
            bind(item) //화면에 값 셋팅하기(ContactsViewHolder.kt에 있는 함수 호출)
        }
    }
}