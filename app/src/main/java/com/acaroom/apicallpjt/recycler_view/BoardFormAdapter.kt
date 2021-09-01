package com.acaroom.apicallpjt.recycler_view

import android.net.Uri
import android.system.Os.bind
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.acaroom.apicallpjt.R
import java.io.File

class BoardFormAdapter(val boardPhotoList: ArrayList<Uri>) :RecyclerView.Adapter<BoardFormPhotoHolder>() {

    override fun getItemCount(): Int {
        return boardPhotoList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardFormPhotoHolder {
        val inflatedView = LayoutInflater.from(parent.context)
            .inflate(R.layout.board_photo_item_holder , parent , false)
        return BoardFormPhotoHolder(inflatedView)
    }

    override fun onBindViewHolder(holder: BoardFormPhotoHolder, position: Int) {

        //아이템들의 간격 설정
        val layoutParams = holder.itemView.layoutParams
        layoutParams.width = 300
        holder.itemView.requestLayout()

        //각 아이템에 데이터 설정
        val item = boardPhotoList[position]
        holder.apply {
            bind(item)
        }
    }
}